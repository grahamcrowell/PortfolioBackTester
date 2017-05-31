
package org.fantastic.PortfolioBackTester

import java.util.{Calendar, Date}
import java.text.SimpleDateFormat

import slick.lifted.TableQuery
import slick.jdbc.PostgresProfile.api.Database
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import Common._
import yahoofinance.YahooFinance
import yahoofinance.histquotes.Interval


abstract class UpdateTaskSpec

case class UpdatePriceTaskSpec(symbol: String, fromDate: Option[Int]) extends UpdateTaskSpec

case object PriceSync {

  def getOutDated(): Seq[UpdatePriceTaskSpec] = {
    val db = Database.forConfig("aws")
    val stock = TableQuery[Stock]
    val price = TableQuery[StockOHLC]
    val epoch = 19000101

    val leftOuterJoin = (for {
      (s, p) <- stock joinLeft price on (_.symbol === _.stock_symbol)
    } yield (s.symbol, p.map(_.date_id))).groupBy(_._1)

    val leftOuterJoinGroup = leftOuterJoin.map {
      case (s, p) => (s, p.map {
        _._2.getOrElse(epoch)
      }.max)
    }

    val expectedMostRecentDate = getExpectedMostRecentDate()
    val filterUpToDate = leftOuterJoinGroup.filter(_._2 < expectedMostRecentDate)

    // wait for query results to return
    val result = Await.result(
      db.run(filterUpToDate.result)
      , Duration.Inf
    )

    result.map {
      case (s, p) => UpdatePriceTaskSpec(s, p)
    }
  }

  def getExpectedMostRecentDate(now: Calendar = Calendar.getInstance()): Int = {
    val offsetDayCount = getOffsetDayCount(now)
    now.add(Calendar.DAY_OF_MONTH, offsetDayCount)
    now
  }

  def getOffsetDayCount(today: Calendar): Int = {
    val todayDayOfWeek = today.get(Calendar.DAY_OF_WEEK)
    val todayHourOfDay = today.get(Calendar.HOUR_OF_DAY)
    todayDayOfWeek match {
      case Calendar.SATURDAY => -1
      case Calendar.SUNDAY => -2
      case Calendar.MONDAY => todayHourOfDay match {
        case _ => if (todayHourOfDay < 13) -3 else 0
      }
      // if todayDayOfWeek is T, W, H, or F and markets open (ie <13) then yesterday is expected
      case _ => if (todayHourOfDay < 13) -1 else 0
    }
  }

//  def downloadData(task: UpdatePriceTaskSpec): Seq[Tuple8[String, Int, Double, Double, Double, Double, Int, Double]] = {
//    // TODO: function that checks if symbol exists in database
//    val stock = YahooFinance.get(task.symbol)
//
//    val interval = Interval.DAILY
//    val vector_HistoricalQuote = stock.getHistory(fromDate, interval).asScala
//    val sql_rows = for (historicalQuote <- vector_HistoricalQuote) yield {
//      val date_id = new SimpleDateFormat("yyyyMMdd").format(historicalQuote.getDate().getTime).toInt
//      (historicalQuote.getSymbol(), date_id, historicalQuote.getOpen().doubleValue, historicalQuote.getHigh().doubleValue, historicalQuote.getLow().doubleValue, historicalQuote.getClose().doubleValue, historicalQuote.getVolume().intValue, historicalQuote.getAdjClose().doubleValue)
//    }
//    return sql_rows
//  }
}
