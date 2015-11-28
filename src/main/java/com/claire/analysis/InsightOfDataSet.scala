import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Claire on 11/24/2015.
 */
object InsightOfDataSet {
  val dataPath = "DataResource/"

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf()
    conf.setMaster("local")
    conf.setAppName("InsightOfData")

    val sc = new SparkContext(conf)
    val line = sc.textFile(dataPath + "HotelUserInfo")

//    /**
//     * <Region, # of regions>
//     */
//    val hotelInRegion = line.map(
//      l => {
//        val temp = l.split(",")
//        if(temp.length != 20)
//          (null,null)
//        else
//          (temp(0), temp(2))
//      }).filter(
//        m => {
//          m._1 != null
//        }
//      )
//      .distinct()
//      .map(f => (f._2, 1)).reduceByKey(_+_)
//
//    hotelInRegion.coalesce(1,true).saveAsTextFile(dataPath + "HotelRegionCode")
//    val regionNum = hotelInRegion.count.toString
//    println("# of regions : " + regionNum)

    /**
     * <User, # of ratings to different hotels>
     */
    val userHotels = line.map(
      l => {
        val temp = l.split(",")
        if(temp.length != 20)
          (null,null)
        else
          //userId,hotelId
          (temp(8), temp(0))
      }).filter(
        m => {
          m._1 != null
        }
      )
      .distinct()
      .map(f => (f._1, 1)).reduceByKey(_+_)

//    userHotels.sortBy(x => x._2,false).coalesce(1,true).saveAsTextFile(dataPath + "UserToHotels")
//    val userNum = userHotels.count.toString
//    println("# of userInDifferentHotels : " + userNum)

    userHotels.map(line =>{
      ("sum",line._2)
    }).reduceByKey(_+_).collect().foreach(println)

  }
}
