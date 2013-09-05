
object Worksheet {
import woots._

 
val w1 = WChar(Id(1,1), 'A')                      //> w1  : woots.WChar = WChar(Id(1,1),A,true)
val w2 = WChar(Id(1,2), 'B')                      //> w2  : woots.WChar = WChar(Id(1,2),B,true)
val w3 = WChar(Id(1,3), 'C')                      //> w3  : woots.WChar = WChar(Id(1,3),C,true)

"A" == WString().ins(w1,0).text                   //> res0: Boolean = true

"AB" == WString().ins(w1,0).ins(w2,1).text        //> res1: Boolean = true

"BA" == WString().ins(w1,0).ins(w2,0).text        //> res2: Boolean = true

val s1 = WString().ins(w1,0).ins(w2,1).ins(w3,2)  //> s1  : woots.WString = WString(Vector(WChar(Id(1,1),A,true), WChar(Id(1,2),B,
                                                  //| true), WChar(Id(1,3),C,true)))

s1.neighbours(0)                                  //> res3: woots.Neighbours = Neighbours(None,Some(Id(1,1)))
s1.neighbours(1)                                  //> res4: woots.Neighbours = Neighbours(Some(Id(1,1)),Some(Id(1,2)))
s1.neighbours(2)                                  //> res5: woots.Neighbours = Neighbours(Some(Id(1,2)),Some(Id(1,3)))
s1.neighbours(3)                                  //> res6: woots.Neighbours = Neighbours(Some(Id(1,3)),None)
WString().neighbours(0)                           //> res7: woots.Neighbours = Neighbours(None,None)

true == s1.canApply(s1.neighbours(0))             //> res8: Boolean = true
true == s1.canApply(s1.neighbours(1))             //> res9: Boolean = true
true == s1.canApply(s1.neighbours(2))             //> res10: Boolean = true
false == s1.canApply( Neighbours(None,Some(Id(10,20))) )
                                                  //> res11: Boolean = true
s1.subseq(s1.neighbours(0))                       //> res12: Vector[woots.WChar] = Vector()
s1.subseq(s1.neighbours(2))                       //> res13: Vector[woots.WChar] = Vector()

val wx = WChar(Id(1,4), 'X')                      //> wx  : woots.WChar = WChar(Id(1,4),X,true)
"XABC" == s1.insertBetween(wx, s1.neighbours(0)).text
                                                  //> res14: Boolean = true

"AXBC" == s1.insertBetween(wx, s1.neighbours(1)).text
                                                  //> res15: Boolean = true
"ABXC" == s1.insertBetween(wx, s1.neighbours(2)).text
                                                  //> res16: Boolean = true


"ABCX" == s1.insertBetween(wx, s1.neighbours(3)).text
                                                  //> res17: Boolean = true



}