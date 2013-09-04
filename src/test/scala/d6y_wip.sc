
object Worksheet {
import woots._

val v = Vector(1,2,3)                             //> v  : scala.collection.immutable.Vector[Int] = Vector(1, 2, 3)
val (b,a) = v.splitAt(4)                          //> b  : scala.collection.immutable.Vector[Int] = Vector(1, 2, 3)
                                                  //| a  : scala.collection.immutable.Vector[Int] = Vector()
            
b ++ a ++ b                                       //> res0: scala.collection.immutable.Vector[Int] = Vector(1, 2, 3, 1, 2, 3)

 
 
val w1 = WChar(Id(1,1), 'A')                      //> w1  : woots.WChar = WChar(Id(1,1),A,true)
val w2 = WChar(Id(1,2), 'B')                      //> w2  : woots.WChar = WChar(Id(1,2),B,true)
val w3 = WChar(Id(1,3), 'C')                      //> w3  : woots.WChar = WChar(Id(1,3),C,true)

WString().ins(w1,0).text                          //> res1: String = A

WString().ins(w1,0).ins(w2,1).text                //> res2: String = AB

WString().ins(w1,0).ins(w2,0).text                //> res3: String = BA

val s1 = WString().ins(w1,0).ins(w2,1).ins(w3,2).text
                                                  //> s1  : String = ABC


}