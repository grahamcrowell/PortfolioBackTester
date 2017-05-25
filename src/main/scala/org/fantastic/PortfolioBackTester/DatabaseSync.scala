package org.fantastic.PortfolioBackTester

import slick.jdbc.PostgresProfile.api._

object FinDwSchema {
  println("FinDwSchema")
  val stock = TableQuery[Stock]
  val stockohlc = TableQuery[StockOHLC]
  // TODO: fix so both tables can be created at same time
  // create table(s)
  val setup = DBIO.seq((stock.schema ++ stockohlc.schema).create)

  def uploadData(symbol: String): DBIOAction[Unit, NoStream, Effect.Write] = {
    val db = Database.forConfig("aws")
    // declare a query against org.fantastic.findw.Stock table
    val stock = TableQuery[Stock]
    val price = TableQuery[StockOHLC]

    val priceDownloader = new PriceIO(symbol)
    val priceData = priceDownloader.getData()
    val upload = DBIO.seq(
      stockohlc ++= priceData
    )
    upload
  }


}
