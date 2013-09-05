import org.specs2.mutable._

import woots._

class WootSpec extends Specification {

  "Single User Woot" should {

    
    "insertion of a WChar at a specific position (internal test)" in {

      val w1 = WChar(Id(1,1), 'A') 
      val w2 = WChar(Id(1,2), 'B') 
      val w3 = WChar(Id(1,3), 'C')   
    	
      new WString().text must_== ""      
      new WString().ins(w1, 0).text must_== "A"
      new WString().ins(w1, 0).ins(w2,1).text must_== "AB"
      new WString().ins(w1, 0).ins(w2,0).text must_== "BA"
      new WString().ins(w1, 0).ins(w2,1).ins(w3,2).text must_== "ABC"  
    }

    "insertion based on previous and next IDs" in {
      
      val w1 = WChar(Id(1,1), 'A') 
      val w2 = WChar(Id(1,2), 'B') 
      val w3 = WChar(Id(1,3), 'C') 
      val s1 = WString().ins(w1,0).ins(w2,1).ins(w3,2)
      
      val wx = WChar(Id(1,4), 'X') 
      s1.insertBetween(wx, s1.neighbours(0)).text must_== "XABC"
      s1.insertBetween(wx, s1.neighbours(1)).text must_== "AXBC"
      s1.insertBetween(wx, s1.neighbours(2)).text must_== "ABXC"
      s1.insertBetween(wx, s1.neighbours(3)).text must_== "ABCX"
   
    }
    

  }

}
