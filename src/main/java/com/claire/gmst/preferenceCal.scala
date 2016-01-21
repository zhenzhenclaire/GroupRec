package com.claire.gmst

import java.io.{File, PrintWriter}

import com.claire.util.Config
import org.apache.spark.{SparkConf, SparkContext}
import scala.collection.immutable.HashMap
import scala.io.Source

/**
 * Created by Claire on 11/10/2015.
 */
class preferenceCal{
  val preference = Array.ofDim[Double](Config.numOfUsers,Config.numOfHotels)
  val userItemRatingPath = Config.parsedMatrixPath
  val hotelLocationPath = Config.userHotelInfo
  val clusteringResult = Config.clusteringResult
  val mergedUser = Config.mergedUser
  val mergedHotel = Config.mergedHotel
  val ratingModel = Config.ratingModel
  var preferenceTemp = Array.ofDim[Double](Config.numOfUsers, Config.numOfHotels)

  val conf = new SparkConf()
  conf.setMaster("local")
  conf.setAppName("preferenceCal")
  conf.set("spark.executor.memory", "6g")
  conf.set("spark.driver.memory","6g")
  val sc = new SparkContext(conf)

  def prepareLoc(): Unit ={
    val line = sc.textFile(hotelLocationPath)
    val hotelLocation = line.map(
      l => {
        val temp = l.split(",")
        //hotelId,location
        val hotelId = temp(0)
        val loc = temp(5) + "," + temp(6)

        (hotelId,loc)
      })
  }

  def mergeClass(): Unit ={
    val clusterLine = sc.textFile(clusteringResult)

    //println("clusterline:" + clusterLine.count())
    // Find items with same class
    val hotelClusters = clusterLine.map(
      l => {
        val temp = l.split(":")
        if(temp(0).equals("hotel")){
          (temp(2),temp(1))
        }
        else{
          (null,null)
        }
      }).filter(x => x._1 != null && x._2 != null).reduceByKey((l,y) => (l + "," + y)).map( x => x._1 + ":" + x._2)

    //println("hotelClusters:" + hotelClusters.count())
    hotelClusters.saveAsTextFile(mergedHotel)

    // Find users with same class
    val userClusters = clusterLine.map(
      l => {
        val temp = l.split(":")
        if(temp(0).equals("user")){
          (temp(2),temp(1))
        }
        else{
          (null,null)
        }
      }).filter(x => x._1 != null && x._2 != null).reduceByKey((l,y) => (l + "," + y)).map( x => x._1 + ":" + x._2)
      .saveAsTextFile(mergedUser)
  }

  /*
    Calculate missing preference avg(avg(uclass) + avg(iclass))
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
          if (preferenceTemp(user.toInt)(hotel) != 0) {

            uScore = preferenceTemp(user.toInt)(hotel) + uScore
            uCount = uCount + 1
          }
        }
      }
    }

    for(line <- Source.fromFile(mergedHotel + "/part-00000").getLines()){
      val hotels = line.split(":")(1).split(",").toSet
      if(hotels.contains(hotel+"")){
        for(hotel <- hotels){
          if(preferenceTemp(user)(hotel.toInt) != 0){
            hScore = preferenceTemp(user)(hotel.toInt) + hScore
            hCount = hCount + 1
          }
        }
      }
    }

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

  def missingCal(user:Int,hotel:Int,userMapBroadcast:Map[Int,Set[String]],hotelMapBroadcast:Map[Int,Set[String]]): Double ={
    var uCount = 0
    var uScore:Double = 0.0
    var hCount = 0
    var hScore:Double = 0.0

    val users = userMapBroadcast.get(user).get
    for (u <- users) {
      if (preferenceTemp(u.toInt)(hotel) != 0) {
        uScore = preferenceTemp(u.toInt)(hotel) + uScore
        uCount = uCount + 1
      }
    }


    val hotels = hotelMapBroadcast.get(hotel).get
    for(h <- hotels){
      if(preferenceTemp(user)(h.toInt) != 0){
        hScore = preferenceTemp(user)(h.toInt) + hScore
        hCount = hCount + 1
      }
    }

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


  def initpreferenceMatrx(): Unit ={
    /*
    Add rating to preferences
     */
    for(line <- Source.fromFile(userItemRatingPath).getLines){
      val ids = line.split(",")
      preference(ids(1).toInt)(ids(0).toInt) = ids(2).toDouble
    }
  }

  def init (): Unit = {

    initpreferenceMatrx()

    //mergeClass()

    prepareLoc()


    /*
    Calculate missing value
     */
    preferenceTemp = preference.clone()

    var userMap:Map[Int,Set[String]] = Map[Int,Set[String]]()
    var hotelMap:Map[Int,Set[String]] = Map[Int,Set[String]]()

    for(line <- Source.fromFile(mergedUser + "/part-00000").getLines()) {
      val users = line.split(":")(1).split(",").toSet
      userMap += (line.split(":")(0).toInt -> users)
    }
    val userMapBroadcast = sc.broadcast(userMap)

    for(line <- Source.fromFile(mergedHotel + "/part-00000").getLines()) {
      val hotels = line.split(":")(1).split(",").toSet
      hotelMap += (line.split(":")(0).toInt -> hotels)
    }
    val hotelMapBroadcast = sc.broadcast(hotelMap)

    calpreferenceByMap(userMapBroadcast.value,hotelMapBroadcast.value)
    //calpreferenceByFor()

    stop()
  }

  def calpreferenceByFor(): Unit ={
    val writer = new PrintWriter(new File(ratingModel))
    for(i <- 0 until(preference.length)){
      for(j <- 0 until(preference(0).length)){
        if(preference(i)(j) == 0) {
          preference(i)(j) = missingCal(i, j)
        }
        writer.write(i + "," + j + "," + preference(i)(j))
        writer.write("\n")
      }
    }
    writer.close()
  }

  def calpreferenceByMap(userMapBroadcast:Map[Int,Set[String]],hotelMapBroadcast:Map[Int,Set[String]]): Unit ={
    val length = Config.numOfHotels * Config.numOfUsers;

    val preferenceMatrix : Array[String] = new Array[String](length)
    var count = 0;
    for(i <- 0 until(preference.length)){
      for(j <- 0 until(preference(0).length)){
        preferenceMatrix(count) = (i+"," + j + "," + preference(i)(j))
        count += 1
      }
    }
    val lines = sc.parallelize(preferenceMatrix)

    println(lines.count())
    lines.saveAsTextFile("temp")

//
//    val result = lines.map(x => {
//      val temp = x.split(",")
//      if(temp(2).toDouble != 0){
//        (x)
//      }else{
//        val v = missingCal(temp(0).toInt, temp(1).toInt,userMapBroadcast,hotelMapBroadcast)
//        (temp(0) + "," + temp(1) + "," + v)
//      }
//    })
//    result.saveAsTextFile("output")
  }

  def stop(): Unit ={
    sc.stop()
  }

}
