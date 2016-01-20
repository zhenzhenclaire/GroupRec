package com.claire.gmst

import java.io.{File, PrintWriter}

import com.claire.util.Config
import org.apache.spark.{SparkConf, SparkContext}
import scala.io.Source
/**
 * Created by Claire on 11/10/2015.
 */
object weightCal{


  val weight = Array.ofDim[Double](Config.numOfHotels,Config.numOfUsers)
  val userItemRatingPath = Config.parsedMatrixPath
  val hotelLocationPath = Config.userHotelInfo
  val clusteringResult = Config.clusteringResult
  val mergedUser = Config.mergedUser
  val mergedHotel = Config.mergedHotel
  val ratingModel = Config.ratingModel
  var weightTemp = Array.ofDim[Double](Config.numOfHotels, Config.numOfUsers)

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

    for(line <- Source.fromFile(mergedUser).getLines()) {
      if (line.contains(user)) {
        val users = line.split(":")(1).split(",")
        for (user <- users) {
          if (weightTemp(user.toInt)(hotel) != 0) {
            uScore = weightTemp(user.toInt)(hotel) + uScore
            uCount = uCount + 1
          }
        }
      }
    }

    for(line <- Source.fromFile(mergedHotel).getLines()){
      if(line.contains(hotel)){
        val hotels = line.split(":")(1).split(",")
        for(hotel <- hotels){
          if(weightTemp(user)(hotel.toInt) != 0){
            hScore = weightTemp(user)(hotel.toInt) + hScore
            hCount = uCount + 1
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


  def main (args: Array[String]): Unit = {
    Config.init

    /*
    Add rating to weights
     */
    for(line <- Source.fromFile(userItemRatingPath).getLines){
      val ids = line.split(",")
      weight(ids(0).toInt)(ids(1).toInt) = ids(2).toDouble
    }

    /*
    Load location info
     */
    val line = sc.textFile(hotelLocationPath)
    val hotelLocation = line.map(
      l => {
        val temp = l.split(",")
        //hotelId,location
        val hotelId = temp(0)
        val loc = temp(5) + "," + temp(6)

        (hotelId,loc)
      })

    /*
    Calculate missing value
     */
    weightTemp = weight.clone()

    val clusterLine = sc.textFile(clusteringResult)

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
      }).reduceByKey((l,y) => (l + "," + y)).map( x => x._1 + ":" + x._2)
      .saveAsTextFile(mergedHotel)

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
      }).reduceByKey((l,y) => (l + "," + y)).map( x => x._1 + ":" + x._2)
      .saveAsTextFile(mergedUser)

    val writer = new PrintWriter(new File(ratingModel))
    for(i <- 0 until(weight.length)){
      for(j <- 0 until(weight(0).length)){
        if(weight(i)(j) == 0){
          weight(i)(j) = missingCal(j,i)
          writer.write(i + "," + j + "," + weight(i)(j))
        }
      }
    }
    writer.close()

  }
  sc.stop()
}
