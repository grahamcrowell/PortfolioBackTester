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

  def simpleMovingAverage(period: Int, name: String): Unit = {
    //connect to db and setup stock and price table
    val db = Database.forConfig("aws")
    val stock = TableQuery[Stock]
    val price = TableQuery[StockOHLC]

    //output: adjusted close, symbol, date
    val q2 = for {
      c <- price if c.stock_symbol === name
    } yield (c.adjusted_close, c.stock_symbol, c.date_id)

    //output: future[tuple2]
    val q1 = q2.map {
      case (s,p,t) => (s,t)
    }

    //function to get the moving average for each date
    //input: sequence of tuples??
    //output: sum of sequence of tuples, max date
    def listofSeqTuple(list: Seq[Tuple2[Double,Int]]): Tuple2[Double, Int] = {
      list.unzip match { case (l1, l2) => (l1.sum/period, l2.max) }
    }

    //input -> window of values -> moving average
    //input: future[tuple2]
    //output: List[tuple2]
    Await.result(
      db.run(
        q1.result).map { res => println(res) //maps to Vector and prints results
        val q3 = res.toList// create list of tuple2 [(Double,Int)]
        val q4 = q3.iterator.sliding(period).toList //create list of the tuples based on the size of period
        //println(q4)
        val q5 = q4.map {listofSeqTuple} //List[Tuple2[Double,Int]] is this the only thing returned??
        println(q5)
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
    Tests.simpleMovingAverage(5,"AA")
    Tests.simpleMovingAverage(5,"A")
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
