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

  def QueryDb: Unit = {
    val db = Database.forConfig("aws")
    val stock = TableQuery[Stock]
    val session = db.createSession()

    println(session.conn.toString())
//    val q = stock.map(_.name)

    Await.result(
      db.run(stock.map(_.name).result).map { res =>
        // res is a Vector[String]
        println(res)
      }, Duration.Inf)


    def find(symbol: String) = db.run(stock.filter(_.symbol === symbol).result.headOption)
    val res = find("AA")
    res.onComplete { case row => println(row.get)}
    println(find("AA"))


    def userNameByIDRange(symbol: Rep[String]) =
      for {
        u <- stock if u.symbol === symbol
      } yield u.name

    val userNameByIDRangeCompiled = Compiled(userNameByIDRange _)

    val namesAction1 = userNameByIDRangeCompiled("MSFT")

    def getNames(id: Int) = db.run(
      sql"SELECT company_name FROM stock WHERE user_id = 'MSFT'"
        .as[(String)].headOption)

    println(getNames(123))
    println(getNames(123).value)


    val q = for (c <- stock) yield c.exchange
    val a = q.result
    val f: Future[Seq[Option[String]]] = db.run(a)

    f.onSuccess { case s => println(s"Result: ${s}") }

    def foo(): Unit = {
      println("fuck u slick")
      db.run(stock.result).map(_.foreach {
        case (company_name) =>
          println("  " + company_name)
      })
      import ExecutionContext.Implicits.global
      val s = "Hello"
      val f: Future[String] = Future {
        s + " future!"
      }
      f foreach {
        msg => println(msg)
      }
    }
    foo()
    session.close()
  }

  //
  //  def UpdateData(): Unit = {
  //    val data = PriceIO.uploadPriceData("MSFT")
  //
  //  }
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
