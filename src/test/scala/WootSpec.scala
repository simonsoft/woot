import org.specs2.mutable._

import woots._

class WootSpec extends Specification {

  "Single User Woot" should {

    "support insertion of a WChar" in {

      val w1 = WChar(Id(1,1), 'A') 
      val w2 = WChar(Id(1,2), 'B') 
      val w3 = WChar(Id(1,3), 'C')   
    	
      new WString().text must_== ""      
      new WString().ins(w1, 0).text must_== "A"
      new WString().ins(w1, 0).ins(w2,1).text must_== "AB"
      new WString().ins(w1, 0).ins(w2,0).text must_== "BA"
      new WString().ins(w1, 0).ins(w2,1).ins(w3,2).text must_== "ABC"  
    }


  }

}
