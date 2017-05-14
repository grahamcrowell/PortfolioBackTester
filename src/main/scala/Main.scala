/**
  * Created by gcrowell on 4/28/2017.
  */

//import slick.jdbc.H2Profile.api._
import java.util.{Timer, TimerTask}

import FinDwSchema.Stock
import slick.jdbc.PostgresProfile.api._

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import ExecutionContext.Implicits.global
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

  def WaitForQuery: Unit = {
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

  def UpdateAllStockPriceData: Unit = {

    // database config
    val db = Database.forConfig("aws")
    // declare a query against Stock table
    val stock = TableQuery[Stock]

    def update_symbols(symbols: Seq[String]) {
      println("update symbol prices")
      println(symbols)
      try {
        symbols.filter(! _.isEmpty()).foreach(symbol => {
          println(s"upload price data for $symbol")
          val updatePriceFuture = db.run(FinDwSchema.uploadData(symbol))
          while(!updatePriceFuture.isCompleted)
            {
              println("waiting updatePriceFuture execution to complete")
              Thread.sleep(1000L)
            }
        })
      } finally db.close()
    }

    // initiate/begin the query
    // runit is an instance of "Future"
    val runit = db.run(stock.map(_.symbol).result).map { symbols =>
      update_symbols(symbols)
    }



    // check if query has finished executing
    while (!runit.isCompleted) {
      // if query not complete then wait for 1000 ms and check again
      println("waiting query execution to complete")
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
    //    Tests.UploadPriceData("INTC")
    Tests.UpdateAllStockPriceData
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
