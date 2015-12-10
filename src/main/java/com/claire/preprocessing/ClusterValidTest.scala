package com.claire.preprocessing

import org.apache.spark.{SparkContext, SparkConf}

/**
  * Created by claire on 12/10/15.
  */
object ClusterValidTest {
  val dataPath = "DataResource/"

  def main (args: Array[String]) {
    val conf = new SparkConf()
    conf.setMaster("local")
    conf.setAppName("join")
    val sc = new SparkContext(conf)

    // Extract (HotelID, HotelInfo) from HotelUserInfo file
    // HotelInfo includes: name, price
    val hotelFile = sc.textFile(dataPath + "HotelUserInfo")
    val hotel = hotelFile.map(line => {
      val fields = line.split(",")
      val hotelID = fields(0)
      val hotelInfo = fields(1) + "-" + fields(9)
        (hotelID, hotelInfo)
    })

    val hotelReflectionFile = sc.textFile(dataPath + "hotelReflectionTable")
    val reflection = hotelReflectionFile.map(l => {
      val hotelID = l.split(" ")(0)
      val reflectedID = l.split(" ")(1)
      (hotelID, reflectedID)
    })

    val classAndHotel = sc.textFile(dataPath + "twentyClusters/MergedClass/part-00000")
    val hotels = classAndHotel.map(l => {
      val classID = l.split(":")(0)
      val hotels = l.split(":")(1).split(",")
      line.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_+_).collect().foreach(println)
    }

    })


}
