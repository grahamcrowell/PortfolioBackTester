/**
  * Created by gcrowell on 4/28/2017.
  */

//import slick.jdbc.H2Profile.api._
import FinDwSchema.Stock
import slick.jdbc.PostgresProfile.api._

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

object Tests {

  def SlickFinDwAws(): Unit = {
    println("connecting to aws (over internet) using TypeSafe configuration file: application.conf")
    val db = Database.forConfig("aws")
    try {
      println("Create the stock table")
      val foo = FinDwSchema
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


  //  http://queirozf.com/entries/slick-3-reference-and-examples
  def QueryDb: Unit = {
    val db = Database.forConfig("aws")
    val stock = TableQuery[Stock]

    Await.result(
      db.run(stock.map(_.name).result).map { res =>
        println(res)
      }, Duration.Inf)
  }
}

object Main {

  def test(): Unit = {
    //    Tests.SlickAws()
    //    Tests.HttpDownload()
    //    Tests.YahooLib()
    val x = Tests.QueryDb

    //    Tests.UpdateData()
  }

  def main(args: Array[String]): Unit = {
    println(s"start")
    println(s"current working directory: ${System.getProperty("user.dir")}")
    test()
    println("end")
  }

}
