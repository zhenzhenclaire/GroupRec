import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

/**
 * Created by Claire on 2015/11/2
 * ./spark-submit --name WorkCountByClaire --class WordCount /root/zhengxil/Research.jar
 */
object WordCount {

  def main (args: Array[String]) {

    val conf = new SparkConf()
    conf.setMaster("local")
    conf.setAppName("wordcount")

    val sc = new SparkContext(conf)
   val line = sc.textFile("count.txt")
    line.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_+_).collect().foreach(println)

//    val line = sc.textFile("file:///root/zhengxil/count.txt")
//    line.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_+_).saveAsTextFile("file:///root/zhengxil/result");

    sc.stop()
  }

}
