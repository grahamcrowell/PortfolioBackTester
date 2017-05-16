/**
  * Created by gcrowell on 4/28/2017.
  */

import FinDwSchema.{Stock, StockOHLC}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration.Duration

object Tests {

  def SlickFinDwAws(): Unit = {
    println("connecting to local postgres using TypeSafe configuration file: application.conf")
    val db = Database.forConfig("aws")
    try {
      println("Create the tables, including primary and foreign keys")
      val setupFuture = db.run(FinDwSchema.setup)
    } finally db.close

    println("tables created in postgreSql instance running locally")
    println("see <database>.<schema>: findw.public")
  }

  def SlickFinDwLocal(): Unit = {
    println("connecting to local postgres using TypeSafe configuration file: application.conf")
    val db = Database.forConfig("local")
    try {
      println("Create the tables, including primary and foreign keys")
      val setupFuture = db.run(FinDwSchema.setup)
    } finally db.close

    println("tables created in postgreSql instance running locally")
    println("see <database>.<schema>: findw.public")
  }

  def UploadPriceData(symbol: String): Unit = {
    println("connecting to local postgres using TypeSafe configuration file: application.conf")
    val db = Database.forConfig("local")
    try {
      println(s"upload price data for $symbol")
      val setupFuture = db.run(FinDwSchema.uploadData(symbol))
    } finally db.close

    println("tables created in postgreSql instance running locally")
    println("see <database>.<schema>: findw.public")
  }


  //  http://queirozf.com/entries/slick-3-reference-and-examples
  def AwaitDbQuery: Unit = {

    val db = Database.forConfig("aws")
    val stock = TableQuery[Stock]

    Await.result(
      db.run(stock.map(_.name).result).map { res =>
        println(res)
      }, Duration.Inf)
  }

  def simpleMovingAverage(period: Int): Unit = {
    val db = Database.forConfig("aws")
    val stock = TableQuery[Stock]
    val price = TableQuery[StockOHLC]

    val q2 = for {
      c <- price if c.stock_symbol === "AA"
    } yield (c.adjusted_close, c.stock_symbol, c.date_id, c.adjusted_close)

    val q1 = q2.map {
      case (s,p,t,v) => (s)
    }



    Await.result(
      db.run(
        q1.result).map { res =>
        println(res)
        val q3 = res.toList
        val q4 = q3.iterator.sliding(period).toList
        println(q4)
        val totals = q4.map {
          _.sum/period
        }
        println(totals)

//        val sums = q4.map {
//          _.sum
//        }
//        println(sums)
      }



      , Duration.Inf

    )



  }

  /**
    *
    * get most recent date id for each stock in price table
    *
    */
  def getPriceRecency: Unit = {

    val db = Database.forConfig("aws")
    val stock = TableQuery[Stock]
    val price = TableQuery[StockOHLC]

    /**
      * sequence of chained query-like expressions
      */
    def leftJoin(): Unit = {
      val leftOuterJoin = for {
        s <- stock
        p <- price
        if s.symbol === p.stock_symbol

      } yield (s.symbol, p.date_id)

      val leftOuterJoinGrouped = (for {
        s <- stock
        p <- price
        if s.symbol === p.stock_symbol

      } yield (s, p)).groupBy(_._1.symbol)

      val leftOuterJoinAgg = leftOuterJoinGrouped.map {
        case (s, p) => (s, p.map {
          _._2.date_id
        }.max)
      }

      // DISINTCT SYMBOL
      Await.result(
        db.run(
          leftOuterJoin.map(_._1).distinct.result).map { res =>
          println(res)
        }
        , Duration.Inf

      )

      // MAX DATE GROUP BY SYMBOL
      Await.result(
        db.run(
          leftOuterJoinAgg.result).map { res =>
          println(res)
        }
        , Duration.Inf
      )
    }

    leftJoin()

    // all stock names
    Await.result(
      db.run(stock.map(_.name).result).map { res =>
        println(res)
      }, Duration.Inf)
  }

  def isCompletedDbQuery: Unit = {
    // database config
    val db = Database.forConfig("aws")
    // declare a query against Stock table
    val stock = TableQuery[Stock]
    // initiate/begin the query
    // runit is an instance of "Future"
    val runit = db.run(stock.map(_.name).result).map { res =>
      println(res)
    }

    // check if query has finished executing
    while (!runit.isCompleted) {
      // if query not complete then wait for 1000 ms and check again
      println("waiting for query")
      Thread.sleep(1000L)
    }
  }
}


object Main {

  def test(): Unit = {
    //    Tests.SlickAws()
    //    Tests.HttpDownload()
    //    Tests.YahooLib()
    //    Tests.UpdateData()
    //    val x = Tests.QueryDb
    //    val y = Tests.WaitForQuery
    //    Tests.SlickFinDwLocal
    //    Tests.SlickFinDwAws()
    //    Tests.UploadPriceData("A")
    //    Tests.UpdateAllStockPriceData
    //Tests.getPriceRecency
    Tests.simpleMovingAverage(5)
  }

  def main(args: Array[String]): Unit = {
    println(s"program starting")
    val start = System.currentTimeMillis()
    println(s"current working directory: ${System.getProperty("user.dir")}")

    test()

    val end = System.currentTimeMillis()
    println(s"execution complete ${end - start} ms")
  }

}
