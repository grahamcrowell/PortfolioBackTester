import slick.jdbc.PostgresProfile.api._

object FinDwSchema {
  println("FinDwSchema")
  val stock = TableQuery[Stock]
  val stockohlc = TableQuery[StockOHLC]
  // TODO: fix so both tables can be created at same time
  //  var schema = stock.schema ++ stockohlc.schema
  //  val schema = stockohlc.schema
  // create table(s)
  //  val priceData = new PriceIO("MSFT")
  //  val values = priceData.uploadPriceData()
  val setup = DBIO.seq((stock.schema ++ stockohlc.schema).create)

  // Definition of the stock table (always use lowercase and _)
  class Stock(tag: Tag) extends Table[(String, String, Option[String], Option[String], Option[Int])](tag, Some("dim"), "stock") {
    // Every table needs a * projection with the same type as the table's type parameter
    def * = (symbol, name, currency, exchange, nasdaq_id)

    def symbol = column[String]("symbol", O.PrimaryKey) // This is the primary key column

    def name = column[String]("company_name") // follows any standard?

    // Option specifies NULLABLE
    def currency = column[Option[String]]("usd") // ISO standard?

    // Option specifies NULLABLE
    def exchange = column[Option[String]]("stock_exchange") // and standard?

    def nasdaq_id = column[Option[Int]]("nasdaq_id") // used to get financial statement data (eg http://fundamentals.nasdaq.com/nasdaq_fundamentals.asp?CompanyID=8244&NumPeriods=50&Duration=1&documentType=1)
  }

  // Definition of the stock_ohlc table
  class StockOHLC(tag: Tag) extends Table[(String, Int, Double, Double, Double, Double, Int, Double)](tag, Some("fact"), "stock_ohlc") {
    def * = (stock_symbol, date_id, open, high, low, close, volume, adjusted_close)

    def stock_symbol = column[String]("stock_symbol")

    def date_id = column[Int]("date_id")

    // TODO: fix foreign key
    // A reified foreign key relation that can be navigated to create a join

    def open = column[Double]("open")

    def high = column[Double]("high")

    def low = column[Double]("low")

    def close = column[Double]("close")

    def volume = column[Int]("volume")

    def adjusted_close = column[Double]("adjusted_close")

    def pk = primaryKey("pk_stockohlc", (stock_symbol, date_id))

  }

}

object TestSlickExmaple {


  val suppliers = TableQuery[Suppliers]
  val coffees = TableQuery[Coffees]
  val setup = DBIO.seq(
    (suppliers.schema ++ coffees.schema).create,




    // Insert some suppliers
    suppliers += (101, "Acme, Inc.", "99 Market Street", "Groundsville", "CA", "95199"),
    suppliers += (49, "Superior Coffee", "1 Party Place", "Mendocino", "CA", "95460"),
    suppliers += (150, "The High Ground", "100 Coffee Lane", "Meadows", "CA", "93966"),
    // Equivalent SQL code:
    // insert into SUPPLIERS(SUP_ID, SUP_NAME, STREET, CITY, STATE, ZIP) values (?,?,?,?,?,?)

    // Insert some coffees (using JDBC's batch insert feature, if supported by the DB)
    coffees ++= Seq(
      ("Colombian", 101, 7.99, 0, 0),
      ("French_Roast", 49, 8.99, 0, 0),
      ("Espresso", 150, 9.99, 0, 0),
      ("Colombian_Decaf", 101, 8.99, 0, 0),
      ("French_Roast_Decaf", 49, 9.99, 0, 0)
    )
    // Equivalent SQL code:
    // insert into COFFEES(COF_NAME, SUP_ID, PRICE, SALES, TOTAL) values (?,?,?,?,?)
  )

  // Definition of the SUPPLIERS table
  class Suppliers(tag: Tag) extends Table[(Int, String, String, String, String, String)](tag, "SUPPLIERS") {
    // Every table needs a * projection with the same type as the table's type parameter
    def * = (id, name, street, city, state, zip)

    def id = column[Int]("SUP_ID", O.PrimaryKey) // This is the primary key column

    def name = column[String]("SUP_NAME")

    def street = column[String]("STREET")

    def city = column[String]("CITY")

    def state = column[String]("STATE")

    def zip = column[String]("ZIP")
  }

  // Definition of the COFFEES table
  class Coffees(tag: Tag) extends Table[(String, Int, Double, Int, Int)](tag, "COFFEES") {
    def * = (name, supID, price, sales, total)

    def name = column[String]("COF_NAME", O.PrimaryKey)

    def price = column[Double]("PRICE")

    def sales = column[Int]("SALES")

    def total = column[Int]("TOTAL")

    // A reified foreign key relation that can be navigated to create a join
    def supplier = foreignKey("SUP_FK", supID, suppliers)(_.id)

    def supID = column[Int]("SUP_ID")
  }

}