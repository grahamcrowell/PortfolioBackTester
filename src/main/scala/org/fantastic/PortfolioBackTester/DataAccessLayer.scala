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

case class UpdatePriceTaskSpec(symbol: String, fromDate: Calendar) extends UpdateTaskSpec

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
    val s = new SimpleDateFormat("yyyyMMdd")
    s.format(now.getTime()).toInt
  }

  def getOffsetDayCount(today: Calendar): Int = {
    if (today.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) -1
    else if (today.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) -2
    else if ((today.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) && (today.get(Calendar.HOUR_OF_DAY) < 13)) -3
    else 0
  }

//  def getOutDated(): Seq[UpdatePriceTaskSpec] = {
//    val db = Database.forConfig("aws")
//    val stock = TableQuery[Stock]
//    val price = TableQuery[StockOHLC]
//
//    val leftOuterJoinGrouped = (for {
//      (s, p) <- stock joinLeft price on (_.symbol === _.stock_symbol)
//    } yield (s.symbol, p.map(_.date_id)))


//    val leftOuterJoinAgg = leftOuterJoinGrouped.map {
//      case (s, p) => (s, p.)
//    }

//
//    Await.result(
//      db.run(
//        leftOuterJoinAgg.result).map { res => println(res) }, Duration.Inf
//    )

//    Seq[UpdatePriceTaskSpec]()

//  }


}