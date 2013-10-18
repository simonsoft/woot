package spiralarm
package woot

import org.specs2.mutable._
import org.specs2.ScalaCheck
import org.scalacheck.Arbitrary
import org.scalacheck.Gen

class WootSpec extends Specification with ScalaCheck {

  object insertAssumingSenderIsCharCreator {
    implicit class InsertHelper(s: WString) {
      def integrate(c: WChar) = s.integrate(InsertOp(c, c.id.ns))
    }
  }
  
  " Id " should {
    val a1 = CharId("a",1)
    val a2 = CharId("a",2)

    val b1 = CharId("b",1)
    val b2 = CharId("b",2)
    
    
    " compare by < " in {
      Beginning < Ending    must_== true
      Ending    < Beginning must_== false
      a1        < Beginning must_== false
      Beginning < a1        must_== true
      a1        < Ending    must_== true
      Ending    < a1        must_== false
      a1        < a2        must_== true
      a1        < b1        must_== true
      a1        < b2        must_== true
      a1        < a1        must_== false
      a2        < b2        must_== true    
    }
  }

  "Single User Woot" should {
    
    val w1 = WChar(CharId("1",1), 'A', Beginning, Ending) 
    val w2 = WChar(CharId("1",2), 'B', w1.id,     Ending) 
    val w3 = WChar(CharId("1",3), 'C', w2.id,     Ending)   
   
    "allow insertion of a WChar at a specific position (internal test)" in {

      new WString().text must_== ""      
      new WString().ins(w1, 0).text must_== "A"
      new WString().ins(w1, 0).ins(w2,1).text must_== "AB"
      new WString().ins(w1, 0).ins(w2,1).ins(w3,2).text must_== "ABC"  
    }

    "allow insertion based on previous and next IDs" in {
      
      val s1 = WString().ins(w1,0).ins(w2,1).ins(w3,2)

      import insertAssumingSenderIsCharCreator._

      s1.integrate( WChar(CharId("1",4), 'X', Beginning, w1.id) ).text must_== "XABC"
      s1.integrate( WChar(CharId("1",4), 'X', w1.id, w2.id) ).text must_== "AXBC"
      s1.integrate( WChar(CharId("1",4), 'X', w2.id, w3.id) ).text must_== "ABXC"
      s1.integrate( WChar(CharId("1",4), 'X', w3.id, Ending) ).text must_== "ABCX"
   
    }

    "integrate is idempotent based on ID" in {
      val op = InsertOp(WChar(CharId("1",1), 'A', Beginning, Ending), "1")
      WString().integrate(op).integrate(op).text must_== "A"
    }
    
  }
  
  "First example in RR5580 section 3.5 (p. 11)" should {
   
    val site2 = WString()
      
    val o1 = WChar(CharId("1",1), '1', Beginning, Ending)
    val o2 = WChar(CharId("2",1), '2', Beginning, Ending)

    import insertAssumingSenderIsCharCreator._

    val site3 = WString().integrate(o1)
    
    val o3 = WChar(CharId("3",1), '3', Beginning, o1.id)
    val o4 = WChar(CharId("3",2), '4', o1.id, Ending)

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

    implicit def opSeq : Arbitrary[List[WChar]] = Arbitrary {
      for ( os <- Gen.oneOf( List(o1,o2,o3,o4).permutations.toList ) ) 
        yield os
    }
     
    "site1 results in 3124 regardless of order" ! propNoShrink {
      (ops: List[WChar]) => 
        //println(" Trying "+ops.map(_.alpha))
        ops.foldLeft(new WString)( (s,c) => s integrate c).text must_== "3124" 
    }
  }
  
  "Deleting" should {
    val o1 = WChar(CharId("1",1), '1', Beginning, Ending)
    val o2 = WChar(CharId("2",1), '2', Beginning, Ending)
    val o3 = WChar(CharId("3",1), '3', Beginning, o1.id)
    val o4 = WChar(CharId("3",2), '4', o1.id, Ending)

    import insertAssumingSenderIsCharCreator._


    "mean a character is not visible if removed" in {
      WString().
       integrate(o2).
       integrate(o1).
       integrate(o3).
       integrate(DeleteOp(o2, "1")).
       integrate(o4).
       text must_== "314"
    }
    
    "mean no characters are left if all are deleted" in {
      WString().
       integrate(o2).
       integrate(o1).
       integrate(o3).
       integrate(DeleteOp(o2,"1")).
       integrate(DeleteOp(o4,"1")).
       integrate(DeleteOp(o1,"1")).
       integrate(DeleteOp(o3,"1")).
       integrate(o4).
       text must_== ""
    }

    "allows a character to be added back again" in {

      val a1 = WChar(CharId("1",1), 'A', Beginning, Ending)
      val b1 = WChar(CharId("1",2), 'B', a1.id, Ending)
      val a2 = WChar(CharId("1",3), 'A', Beginning, b1.id)

      val seq = InsertOp(a1,"1") :: InsertOp(b1,"1") :: DeleteOp(a1,"1") :: InsertOp(a2,"1") :: Nil

      seq.foldLeft(WString()) { _ integrate _ }.text must_== "AB"
    }

    
  }
  
  "TP2 puzzle (p. 16 of RR5580)" should {
    
    val a = WChar(CharId("1",1), 'a', Beginning, Ending)
    val b = WChar(CharId("1",2), 'b', a.id, Ending)
    val c = WChar(CharId("1",3), 'c', b.id, Ending)
    val d = WChar(CharId("1",4), 'd', c.id, Ending)
   
    // Set up:
    val site = List(a,b,c,d).foldLeft(WString()) { _ integrate InsertOp(_, "1") }
    site.text must_== "abcd"

    val ops = 
      InsertOp(WChar(CharId("1",5), 'x', c.id, d.id), "1") ::
      DeleteOp(b, "2") ::
      InsertOp(WChar(CharId("3",1), 'y', b.id, c.id), "3") ::
      Nil
       
    implicit def opSeq : Arbitrary[List[Operation]] = Arbitrary {
      for ( op <- Gen.oneOf(ops.permutations.toList ) ) 
        yield op
      }
       
    "always result in aycxd" ! propNoShrink {
      (ops: List[Operation]) => 
        ops.foldLeft(site)(_ integrate _).text must_== "aycxd"
    }
      
  }

  
 
}
