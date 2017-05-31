
package org.fantastic.PortfolioBackTester

import java.text.SimpleDateFormat
import java.util.Calendar

/**
  * Created by Dust on 5/28/2017.
  */
object Common {

  val dateIdFormat = new SimpleDateFormat("yyyyMMdd")

  implicit def toInt(x: Calendar): Int = {
    dateIdFormat.format(x.getTime()).toInt
  }

  implicit def toCalendar(dateId: Int): Calendar = {
    val calendarOut = Calendar.getInstance()
    calendarOut.setTime(dateIdFormat.parse(dateId.toString))
    calendarOut
  }

}

