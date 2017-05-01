/**
  * Created by gcrowell on 4/28/2017.
  */

//import slick.jdbc.H2Profile.api._
import slick.jdbc.PostgresProfile.api._


object Tests {

  def SlickFinDwAws(): Unit = {
    println("connecting to aws (over internet) using TypeSafe configuration file: application.conf")
    val db = Database.forConfig("aws")
    try {
      println("Create the stock table")
      val foo = TestSlickStock
      val setupFuture = db.run(foo.setup)
    } finally db.close
  }


  def SlickExampleAws(): Unit = {


    println("connecting to aws (over internet) using TypeSafe configuration file: application.conf")
    val db = Database.forConfig("local")
    try {
      println("Create the tables, including primary and foreign keys")
      val foo = TestSlickExmaple
      //      val setupFuture = db.run(foo.setup)
    } finally db.close

    println("tables created in postgreSql instance running on AWS")
    println("see <database>.<schema>: findw.public")
  }



  def YahooLib(): Unit = {
    val data = DataDownload.yahoolib()
  }
}

object Main {

  def test(): Unit = {
    //    Tests.SlickAws()
    //    Tests.HttpDownload()
//    Tests.YahooLib()
    Tests.SlickFinDwAws()
  }

  def main(args: Array[String]): Unit = {
    println(s"start")
    println(s"current working directory: ${System.getProperty("user.dir")}")
    test()
    println("end")
  }

}
