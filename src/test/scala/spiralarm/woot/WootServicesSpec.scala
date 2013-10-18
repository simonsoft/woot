package spiralarm
package woot

import snippet.WootServices

import org.specs2.mutable._

class WootServicesSpec extends Specification {


  "Service" should {

    "find zero as a clock value in an empty model" in {
      WootServices.maxClockValue("site1", WString()) must_== 0
    }

    "find largest clock value in a model for a site" in {

      val model = WString().
        integrate(InsertOp(WChar(CharId("site1",100),'A',Beginning,Ending), from="site1")).
        integrate(InsertOp(WChar(CharId("site2",200),'A',Beginning,Ending), from="site2"))

      WootServices.maxClockValue("site1", model) must_== 100
      WootServices.maxClockValue("site2", model) must_== 200
      WootServices.maxClockValue("site3", model) must_== 0
    }

  }

}
