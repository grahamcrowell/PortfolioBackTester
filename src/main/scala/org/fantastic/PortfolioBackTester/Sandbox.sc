//package org.fantastic.PortfolioBackTester

import java.text.SimpleDateFormat

import org.fantastic.PortfolioBackTester._
import slick.jdbc.PostgresProfile.api._

  import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration

val t = Tests.getDb()
//val db : Database = t.getDb()
val stock = TableQuery[Stock]
val price = TableQuery[StockOHLC]

val leftOuterJoinGrouped = (for {
(s, p) <- stock joinLeft price on (_.symbol === _.stock_symbol)
} yield (s, p) ).groupBy (_._1.symbol)





