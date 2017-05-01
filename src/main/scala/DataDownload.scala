import java.util.Calendar

import yahoofinance.YahooFinance
import yahoofinance.histquotes.Interval

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
