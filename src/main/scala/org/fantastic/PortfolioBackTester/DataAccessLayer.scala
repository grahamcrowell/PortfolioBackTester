
package org.fantastic.PortfolioBackTester

import java.util.Calendar
import java.text.SimpleDateFormat

import slick.lifted.TableQuery
import slick.jdbc.PostgresProfile.api.Database
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global


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
    Common.calToInt(now)
  }

  def getOffsetDayCount(today: Calendar): Int = {
    if (today.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) -1
    else if (today.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) -2
    else if ((today.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) && (today.get(Calendar.HOUR_OF_DAY) < 13)) -3
    else 0
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