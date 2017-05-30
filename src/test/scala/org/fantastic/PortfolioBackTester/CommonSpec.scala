
package org.fantastic.PortfolioBackTester

import java.util.Calendar

import org.scalatest.{FunSpec, Matchers}

/**
  * Created by gcrowell on 2017-05-28.
  */
class CommonSpec extends FunSpec with Matchers {
  describe("Conversions") {
    it("converts from Int to Calendar") {
      import Common._
      val int = 20170101
      val calendar: Calendar = int
      val expected = Calendar.getInstance()
      expected.set(2017, 0, 0)
      calendar.get(Calendar.YEAR) should be (expected.get(Calendar.YEAR))
      calendar.get(Calendar.MONTH) should be (expected.get(Calendar.MONTH))
      calendar.get(Calendar.DAY_OF_MONTH) should be (expected.get(Calendar.DAY_OF_MONTH))
    }
  }
}
