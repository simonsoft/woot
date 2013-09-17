package woots

import org.specs2.mutable._

class WootSpec extends Specification {

  "Single User Woot" should {
    
    val w1 = WChar(CharId(1,1), 'A', Beginning, Ending) 
    val w2 = WChar(CharId(1,2), 'B', w1.id,     Ending) 
    val w3 = WChar(CharId(1,3), 'C', w2.id,     Ending)   
   
    "allow insertion of a WChar at a specific position (internal test)" in {

      new WString().text must_== ""      
      new WString().ins(w1, 0).text must_== "A"
      new WString().ins(w1, 0).ins(w2,1).text must_== "AB"
      new WString().ins(w1, 0).ins(w2,1).ins(w3,2).text must_== "ABC"  
    }

    "allow insertion based on previous and next IDs" in {
      
      val s1 = WString().ins(w1,0).ins(w2,1).ins(w3,2)
      
      s1.integrate( WChar(CharId(1,4), 'X', Beginning, w1.id) ).text must_== "XABC"
      s1.integrate( WChar(CharId(1,4), 'X', w1.id, w2.id) ).text must_== "AXBC"
      s1.integrate( WChar(CharId(1,4), 'X', w2.id, w3.id) ).text must_== "ABXC"
      s1.integrate( WChar(CharId(1,4), 'X', w3.id, Ending) ).text must_== "ABCX"
   
    }
    
  }
  
  "First example of section 3.5 (p. 11)" should {
   
    val site1 = WString() 
    val site2 = WString()
      
    val o1 = WChar(CharId(1,1), '1', Beginning, Ending)
    val o2 = WChar(CharId(2,1), '2', Beginning, Ending)
   
    val site3 = WString().integrate(o1)
    
    val o3 = WChar(CharId(3,1), '3', Beginning, o1.id)
    val o4 = WChar(CharId(3,2), '4', o1.id, Ending)

    "site2 results in 3124" in {
      site2.integrate(o2).
         integrate(o1).
         integrate(o3).
         integrate(o4).
         text must_== "3124"
    }
    
     "site3 results in 3124" in {
       site3.integrate(o3).
          integrate(o4).
          integrate(o2).
          text must_== "3124"
    }
     
     // TODO: Can we use ScalaCheck to randomly run ops through s1?
    
  }
  

}
