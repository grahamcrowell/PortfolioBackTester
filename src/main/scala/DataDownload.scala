import java.util.Calendar
import java.util.function.Consumer
import java.{text, util}
import java.text.{DateFormat, SimpleDateFormat}

import FinDwSchema.StockOHLC
import slick.lifted.TableQuery
import yahoofinance.YahooFinance
import yahoofinance.histquotes.{HistoricalQuote, Interval}
import slick.jdbc.PostgresProfile.api._
import scala.collection.JavaConverters._
//import scala.Predef.StringFormat

/**
  * see http://alvinalexander.com/scala/how-to-write-scala-http-get-request-client-source-fromurl#setting-the-timeout-while-using-scala-io-source-fromurl
  *
  */
object DataDownload {


  def yahoolib(): Unit = {
    val stock = YahooFinance.get("INTC")
    println(stock.print())
    var start = Calendar.getInstance()
    start.set(1900, 1, 1)
    val monthlyData = stock.getHistory(start)
    println(monthlyData)
    println(monthlyData.get(0))

    var end = Calendar.getInstance().getTime
    var interval = Interval.DAILY
    println(interval)
    val dailyData = stock.getHistory(start, interval)
    println(dailyData)
    println(dailyData.get(0))
    val dailyPriceData = dailyData.get(0)
    val open = dailyPriceData.getOpen()
    println(open)


  }
}


object PriceIO {

  def consume(historicalQuote: HistoricalQuote): Tuple8[String, Int, Double, Double, Double, Double, Int, Double] = {
    val date_id = new SimpleDateFormat("yyyyMMdd").format(historicalQuote.getDate().getTime).toInt
    val row = (historicalQuote.getSymbol(), date_id, historicalQuote.getOpen().doubleValue, historicalQuote.getHigh().doubleValue, historicalQuote.getLow().doubleValue, historicalQuote.getClose().doubleValue, historicalQuote.getVolume().intValue, historicalQuote.getAdjClose().doubleValue)
    return row
  }


}


class PriceIO(symbol: String = "INTC") {


  def uploadPriceData(): Seq[Tuple8[String, Int, Double, Double, Double, Double, Int, Double]] = {
    val stock = YahooFinance.get(symbol)
    println(stock.print)
    //    TODO: function that checks if symbol exists in database

    var start = Calendar.getInstance()
    start.set(1900, 1, 1)

    // get data
    // get TableQuery
    val stock_ohlc = TableQuery[StockOHLC]
    var interval = Interval.DAILY
    println(interval)
    val java_list_HistoricalQuote = stock.getHistory(start, interval)
    //    List<HistoricalQuote> (Java)
    println(java_list_HistoricalQuote)
    val vector_HistoricalQuote = java_list_HistoricalQuote.asScala
//      [Tuple8[String, Int, Double, Double, Double, Double, Int, Double]
    //    (java_list_HistoricalQuote.iterator())
    //    var vector_HistoricalQuote = Vector()
//    java_list_HistoricalQuote

    //      .map((historicalQuote: HistoricalQuote) => case {consume(_)})
    //    java_list_HistoricalQuote.forEach(println(_))


    //    val sql_rows = vector_HistoricalQuote.view.map { case _ => consume(_) }

    val sql_rows = for (historicalQuote <- vector_HistoricalQuote) yield {
      val date_id = new SimpleDateFormat("yyyyMMdd").format(historicalQuote.getDate().getTime).toInt
      (historicalQuote.getSymbol(), date_id, historicalQuote.getOpen().doubleValue, historicalQuote.getHigh().doubleValue, historicalQuote.getLow().doubleValue, historicalQuote.getClose().doubleValue, historicalQuote.getVolume().intValue, historicalQuote.getAdjClose().doubleValue)
    }

    return sql_rows
  }
}