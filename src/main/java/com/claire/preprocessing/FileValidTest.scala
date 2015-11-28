import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Claire on 11/28/2015.
 */
object FileValidTest {
  val dataPath = "DataResource/"

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf()
    conf.setMaster("local")
    conf.setAppName("FileValidTest")

    val sc = new SparkContext(conf)
    val line = sc.textFile(dataPath + "HotelUserInfo")

    val hotelInRegion = line.map(
      l => {
        val temp = l.split(",")
//        if (temp.length != 20)
//          (null, null)
//        else
          (temp(0), temp(2))
      })
      .distinct()
      .map(f => (f._2, 1)).reduceByKey(_ + _)

    hotelInRegion.coalesce(1, true).saveAsTextFile(dataPath + "FileValidTest")
  }

}
