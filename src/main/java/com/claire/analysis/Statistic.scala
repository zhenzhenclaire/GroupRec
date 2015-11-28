import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Claire on 11/10/2015.
 */
object Statistic {
  val dataPath = "DataResource/"

  //Data Sample :
  //((208454,Sofitel New York,NY,45 West 44th Street,10036,New York City,40.7557843449132,-73.9817968688859),ATripAdvisorMember,414,New York City New York,5,-1,-1,-1,-1,-1,-1,-1,02/16/2004))
  def main (args: Array[String]): Unit = {
    val conf = new SparkConf()
    conf.setMaster("local")
    conf.setAppName("statistic")

    val sc = new SparkContext(conf)
    val line = sc.textFile(dataPath + "HotelUserInfo")

    /**
     * WordCount by filed 'Hotel_ID' and then count the number of key
     */
    val temp = line.map(_.split(",")(0)).map((_, 1)).reduceByKey(_+_)
    val hotelNum = temp.count.toString
    println("# of hotels : " + hotelNum)

    /**
     * WordCount by filed 'User_ID' and then count the number of key
     */
    val temp1 = line.map(_.split(",")(8)).map((_, 1)).reduceByKey(_+_)
    val userNum = temp1.count.toString
    println("# of users : " + userNum)

    sc.stop()
  }
}
