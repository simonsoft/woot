import org.specs2.mutable._

import woots._

class WootSpec extends Specification {

  "Single User Woot" should {
 
    "allow insertion of a WChar at a specific position (internal test)" in {

      val w1 = WChar(Id(1,1), 'A') 
      val w2 = WChar(Id(1,2), 'B') 
      val w3 = WChar(Id(1,3), 'C')   
    	
      new WString().text must_== ""      
      new WString().ins(w1, 0).text must_== "A"
      new WString().ins(w1, 0).ins(w2,1).text must_== "AB"
      new WString().ins(w1, 0).ins(w2,0).text must_== "BA"
      new WString().ins(w1, 0).ins(w2,1).ins(w3,2).text must_== "ABC"  
    }

    "allow insertion based on previous and next IDs" in {
      
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
  
  "First example of section 3.5 (p. 11)" should {
   
    case class Operation(c: WChar, ns: Neighbours)
    
    implicit class IntegrateOp(s: WString) {
      def integrate(op: Operation) : WString =
        s.insertBetween(op.c, op.ns)
    }
    
    val s1 = WString() 
    val s2 = WString()
      
    val o1 = Operation(WChar(Id(1,1), '1'), s1.neighbours(0))
    val o2 = Operation(WChar(Id(2,1), '2'), s2.neighbours(0))
   
    val s3 = WString().integrate(o1)
    
    val o3 = Operation(WChar(Id(3,1),'3'), s3.neighbours(0))
    val o4 = Operation(WChar(Id(3,2),'4'), s3.neighbours(1))

    "site2 results in 3124" in {
      s2.integrate(o2).
         integrate(o1).
         integrate(o3).
         integrate(o4).
         text must_== "3124"
    }
    
     "site3 results in 3124" in {
       s3.integrate(o3).
          integrate(o4).
          integrate(o2).
          text must_== "3124"
    }
     
     // TODO: Can we use ScalaCheck to randomly run ops through s1?
    
  }
  

}
