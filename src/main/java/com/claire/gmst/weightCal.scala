package com.claire.gmst

import java.io.{File, PrintWriter}

import com.claire.util.Config
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import scala.io.Source
/**
 * Created by Claire on 11/10/2015.
 */
class weightCal{
  val weight = Array.ofDim[Double](Config.numOfUsers,Config.numOfHotels)
  val userItemRatingPath = Config.parsedMatrixPath
  val hotelLocationPath = Config.userHotelInfo
  val clusteringResult = Config.clusteringResult
  val mergedUser = Config.mergedUser
  val mergedHotel = Config.mergedHotel
  val ratingModel = Config.ratingModel
  var weightTemp = Array.ofDim[Double](Config.numOfUsers, Config.numOfHotels)

  val conf = new SparkConf()
  conf.setMaster("local")
  conf.setAppName("weightCal")

  val sc = new SparkContext(conf)

  /*
    Calculate missing weight avg(avg(uclass) + avg(iclass))
     */
  def missingCal(user:Int,hotel:Int): Double ={
    var uCount = 0
    var uScore:Double = 0.0
    var hCount = 0
    var hScore:Double = 0.0

    for(line <- Source.fromFile(mergedUser + "/part-00000").getLines()) {
      val users = line.split(":")(1).split(",").toSet
      if (users.contains(user+"")) {
        for (user <- users) {
          if (weightTemp(user.toInt)(hotel) != 0) {

            uScore = weightTemp(user.toInt)(hotel) + uScore
            uCount = uCount + 1
          }
        }
      }
    }

    for(line <- Source.fromFile(mergedHotel + "/part-00000").getLines()){
      val hotels = line.split(":")(1).split(",").toSet
      if(hotels.contains(hotel+"")){
        for(hotel <- hotels){
          if(weightTemp(user)(hotel.toInt) != 0){
            hScore = weightTemp(user)(hotel.toInt) + hScore
            hCount = hCount + 1
          }
        }
      }
    }

//    println(user + "----->" + hotel + ":" + uScore + "->" + uCount + ";" + hScore + "->" + hCount)

    if (uCount != 0 && hCount != 0){
      return (uScore/uCount + hScore/hCount)/2
    }else if (uCount != 0 && hCount == 0){
      return (uScore/uCount)
    }else if (uCount == 0 && hCount != 0){
      return (hScore/hCount)
    }else{
      return (0.0)
    }
  }


  def init (): Unit = {

    /*
    Add rating to weights
     */
    for(line <- Source.fromFile(userItemRatingPath).getLines){
      val ids = line.split(",")
      weight(ids(1).toInt)(ids(0).toInt) = ids(2).toDouble
    }

    println("weight:" + weight(11819)(311))
    /*
    Load location info
     */
//    val line = sc.textFile(hotelLocationPath)
//    val hotelLocation = line.map(
//      l => {
//        val temp = l.split(",")
//        //hotelId,location
//        val hotelId = temp(0)
//        val loc = temp(5) + "," + temp(6)
//
//        (hotelId,loc)
//      })
//
    /*
    Calculate missing value
     */
    weightTemp = weight.clone()
    println("weight:" + weightTemp(11819)(311))
//    val clusterLine = sc.textFile(clusteringResult)
//
//    //println("clusterline:" + clusterLine.count())
//    // Find items with same class
//    val hotelClusters = clusterLine.map(
//      l => {
//        val temp = l.split(":")
//        if(temp(0).equals("hotel")){
//          (temp(2),temp(1))
//        }
//        else{
//          (null,null)
//        }
//      }).filter(x => x._1 != null && x._2 != null).reduceByKey((l,y) => (l + "," + y)).map( x => x._1 + ":" + x._2)
//
//    //println("hotelClusters:" + hotelClusters.count())
//    hotelClusters.saveAsTextFile(mergedHotel)
//
//    // Find users with same class
//    val userClusters = clusterLine.map(
//      l => {
//        val temp = l.split(":")
//        if(temp(0).equals("user")){
//          (temp(2),temp(1))
//        }
//        else{
//          (null,null)
//        }
//      }).filter(x => x._1 != null && x._2 != null).reduceByKey((l,y) => (l + "," + y)).map( x => x._1 + ":" + x._2)
//      .saveAsTextFile(mergedUser)

//
//    val length = Config.numOfHotels * Config.numOfUsers;
//    var weightMatrix : Array[String] = new Array[String](length)
//    var count = 0;
//    for(i <- 0 until(weight.length)){
//      for(j <- 0 until(weight(0).length)){
//        weightMatrix(count) = (i+"," + j + "," + weight(i)(j))
//        count += 1
//      }
//    }
//    val lines = sc.parallelize(weightMatrix)
//    val result = lines.map(x => {
//      val temp = x.split(",")
//      if(temp(2).toDouble != 0){
//        (x)
//      }else{
//        val v = missingCal(temp(0).toInt, temp(1).toInt)
//        (temp(0) + "," + temp(1) + "," + v)
//      }
//    })
//    result.saveAsTextFile("output")
//    println(result.count())


//
    val writer = new PrintWriter(new File(ratingModel))
    for(i <- 0 until(weight.length)){
      for(j <- 0 until(weight(0).length)){
        if(weight(i)(j) == 0) {
          weight(i)(j) = missingCal(i, j)
        }
        writer.write(i + "," + j + "," + weight(i)(j))
        writer.write("\n")
      }
    }
    writer.close()
    stop()
  }

  def stop(): Unit ={
    sc.stop()
  }

}
