
package org.fantastic.PortfolioBackTester

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

/**
  * Created by Dust on 5/28/2017.
  */
object Common {

  implicit def calToInt (x: Calendar) : Int = {
    val s = new SimpleDateFormat("yyyyMMdd")
    s.format(x.getTime()).toInt
  }

  implicit def intToCal (x: Int) : Date = {
    val s = new SimpleDateFormat("yyyyMMdd")
    s.parse(x.toString())
  }

}

