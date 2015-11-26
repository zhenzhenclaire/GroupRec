/**
 * Created by admin on 2015/11/3.
 * Ref: http://developer.51cto.com/art/201401/426591.htm
 */
object Join {
  val dataPath = "DataResource/"
  def main (args: Array[String]) {
    val conf = new SparkConf()
    conf.setMaster("local")
    conf.setAppName("join")
    val sc = new SparkContext(conf)

    // Extract (HotelID, HotelInfo) from hotelFile
    // HotelInfo includes: name, region, street-address, postal-code, locality
    val hotelFile = sc.textFile(dataPath + "Hotel.txt")
    val hotel = hotelFile.map(line => {
      val index = line.indexOf(",")
      val HotelInfo = line.substring(index + 1)
      val HotelID = line.substring(0, index)
      (HotelID, HotelInfo)
    })

    // Extract  (HotelID, GPS) from locationFile
    // GPS includes: longitude & latitude
    val locationFile = sc.textFile(dataPath + "hotel_id_position.txt")
    val location = locationFile.map(line =>{
      val index = line.indexOf(" ")
      val HotelID = line.substring(0,index)
      val GPS = line.substring( index + 1)
      (HotelID, GPS)
    })


    // Extract (HotelID, UserID, Price, Rating) from ratingFile
    val ratingFile = sc.textFile(dataPath + "CD-00001-00000001.dat")
    val rating = ratingFile.map(line => {
      val fields = line.split(",")
      val Hotel_User = fields(0) + "," + fields(1)
      val Price = fields(2)
      val len = Hotel_User.length + Price.length + 1;
      val Price_Ratings =  Price + "," + line.substring(len + 1)
      (Hotel_User, Price_Ratings)
    })

    // Extract  (HotelID, UserID, Date) from commentFile
    val commentFile = sc.textFile(dataPath + "CD-00001-00000002.dat")
    val comments = commentFile.map(line =>{
      val fields = line.split(",")
      val Hotel_User = fields(0) + "," + fields(1)
      val Date = fields(2)
      (Hotel_User, Date)
    })

    /**
     * join(otherDataset, [numTasks])
     * (K,V) + (K,W) => (K, (V,W))
     */

    //By first join, we get <HotelId, HotelInfo, GPS>
    val hotelAllInfoFile = hotel
      .join(location)
      .map(f => (f._1, f._2._1, f._2._2))
    hotelAllInfoFile.saveAsTextFile(dataPath + "HotelInfo")
    //hotelAllInfo.collect.foreach(println)

    //By second join, we get <Hotel_User, Price_Ratings, Date>
    val userInfoFile = rating
      .join(comments)
      .map(f => (f._1, f._2._1, f._2._2))
    userInfoFile.saveAsTextFile(dataPath + "UserInfo")

    //Read from HotelInfo and normalize to <hotelId,hotelInfo>
    val hotelAllInfo = sc.textFile(dataPath + "HotelInfo").map(line => {
      val index = line.indexOf(",")
      val hotelId = line.substring(0,index)
      val hotelInfo = line.substring(index + 1)
      (hotelId,hotelInfo)
    })

    //Read from hotelUserInfo and normalize to <hotelId,hUserInfo>
    val UserInfo = sc.textFile(dataPath + "UserInfo").map(line => {
      val index = line.indexOf(",")
      val hotelId = line.substring(0,index)
      val userInfo = line.substring(index + 1)
      (hotelId,userInfo)
    })

    //By third join, we get <hotelId, hotelInfo, userInfo>
    val hotelUserInfoFile = hotelAllInfo
      .join(UserInfo)
      .map(f => (f._1, f._2._1, f._2._2))
    hotelUserInfoFile.coalesce(3,true).saveAsTextFile(dataPath + "HotelUserInfo")
  }
}
