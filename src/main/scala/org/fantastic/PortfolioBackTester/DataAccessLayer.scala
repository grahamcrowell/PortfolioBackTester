
package org.fantastic.PortfolioBackTester

import java.util.Calendar
import java.text.SimpleDateFormat

import slick.lifted.TableQuery
import slick.jdbc.PostgresProfile.api.Database
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import Common._


abstract class UpdateTaskSpec

case class UpdatePriceTaskSpec(symbol: String, fromDate: Option[Int]) extends UpdateTaskSpec

trait Source {
  def getExpectedMostRecentDate(): Calendar
}

trait Destination {
  def getLastUpdate(): Seq[UpdatePriceTaskSpec]
}

trait Updatable {
  def getLastUpdate(symbol: String): UpdatePriceTaskSpec


}


case object PriceSync {

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




    // wait for query results to return
    val result = Await.result(
      db.run(leftOuterJoinGroup.result)
      , Duration.Inf
    )

    result.map {
      case (s, p) => UpdatePriceTaskSpec(s, p)
    }

  }



}