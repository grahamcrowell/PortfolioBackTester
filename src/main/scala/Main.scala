/**
  * Created by gcrowell on 4/28/2017.
  */

import slick.jdbc.H2Profile.api._


object Foo {


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

object Main {

  def main(args: Array[String]): Unit = {
    println("start")

    println("connecting to h2mem1 (built in db for dev) using TypeSafe configuration file: application.conf")
    val db = Database.forConfig("h2mem1")
    try {
      println("Create the tables, including primary and foreign keys")
      val foo = Foo
      val setupFuture = db.run(foo.setup)
    } finally db.close

    println("end")
  }

}
