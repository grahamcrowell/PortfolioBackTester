/**
  * Created by gcrowell on 4/28/2017.
  */

//import slick.jdbc.H2Profile.api._
import slick.jdbc.PostgresProfile.api._


object Tests {
  def SlickAws(): Unit = {


    println("connecting to aws (over internet) using TypeSafe configuration file: application.conf")
    val db = Database.forConfig("aws")
    try {
      println("Create the tables, including primary and foreign keys")
      val foo = TestSlickExmaple
      val setupFuture = db.run(foo.setup)
    } finally db.close

    println("tables created in postgreSql instance running on AWS")
    println("see <database>.<schema>: findw.public")
  }

  def HttpDownload(): Unit = {
    val url = "http://chart.finance.yahoo.com/table.csv?s=AMZN&a=2&b=28&c=2017&d=3&e=28&f=2017&g=d&ignore=.csv"
    val content = DataDownload.get(url)
    println(content.value)

  }

  def YahooLib(): Unit = {
    val data = DataDownload.yahoolib()
  }
}

object Main {

  def test(): Unit = {
    //    Tests.SlickAws()
    //    Tests.HttpDownload()
    Tests.YahooLib()
  }

  def main(args: Array[String]): Unit = {
    println(s"start")
    println(s"current working directory: ${System.getProperty("user.dir")}")
    test()
    println("end")
  }

}
