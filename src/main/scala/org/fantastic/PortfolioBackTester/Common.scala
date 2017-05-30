
package org.fantastic.PortfolioBackTester

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

/**
  * Created by Dust on 5/28/2017.
  */
object Common {

  implicit def toInt(x: Calendar) : Int = {
    val s = new SimpleDateFormat("yyyyMMdd")
    s.format(x.getTime()).toInt
  }

  implicit def toCalendar(x: Int) : Calendar = {
    val calendarOut = Calendar.getInstance()
    val s = new SimpleDateFormat("yyyyMMdd")
    calendarOut.setTime(s.parse(x.toString()))
    calendarOut
  }


  implicit def toDate(x: Int) : Date = {
    val s = new SimpleDateFormat("yyyyMMdd")
    s.parse(x.toString())
  }

}

