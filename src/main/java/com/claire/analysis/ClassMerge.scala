package com.claire.analysis

import org.apache.spark.{SparkConf, SparkContext}

/**
 * Merge user and hotel of the same class
  * Created by Claire on 2015/11/2
 */

object ClassMerge {

  def main (args: Array[String]) {

    val conf = new SparkConf()
    conf.setMaster("local")
    conf.setAppName("ClassMerge")

    val sc = new SparkContext(conf)

    val line = sc.textFile("DataResource/twentyClusters/thousand20Class.txt")

    line.map(l => {
      val temp = l.split(":")
        val id = temp(0)
        val cluster = temp(1)
      (cluster, id)
    }).reduceByKey((l,y) => (l + "," + y)).map( x => x._1 + ":" + x._2)
      .saveAsTextFile("DataResource/twentyClusters/MergedClass")

    sc.stop()
  }

}
