
object Worksheet {
import woots._

case class Operation(c: WChar, ns: Neighbours)
 
 // Setion 3.5: Example 1
 
 val s1 = WString()                               //> s1  : woots.WString = WString(Vector())
 val s2 = WString()                               //> s2  : woots.WString = WString(Vector())
 
 val o1 = Operation(WChar(Id(1,1), '1'), s1.neighbours(0))
                                                  //> o1  : Worksheet.Operation = Operation(WChar(Id(1,1),1,true),Neighbours(None,
                                                  //| None))
 
 val o2 = Operation(WChar(Id(2,1), '2'), s2.neighbours(0))
                                                  //> o2  : Worksheet.Operation = Operation(WChar(Id(2,1),2,true),Neighbours(None,
                                                  //| None))
 
 val s3 = WString().insertBetween(o1.c, o2.ns)    //> * Simple insert of '1' at 0
                                                  //| s3  : woots.WString = WString(Vector(WChar(Id(1,1),1,true)))
   
   
 val o3 = Operation(WChar(Id(3,1),'3'), s3.neighbours(0))
                                                  //> o3  : Worksheet.Operation = Operation(WChar(Id(3,1),3,true),Neighbours(None,
                                                  //| Some(Id(1,1))))
 val o4 = Operation(WChar(Id(3,2),'4'), s3.neighbours(1))
                                                  //> o4  : Worksheet.Operation = Operation(WChar(Id(3,2),4,true),Neighbours(Some(
                                                  //| Id(1,1)),None))
val s2a = s2.insertBetween(o2.c,o2.ns)            //> * Simple insert of '2' at 0
                                                  //| s2a  : woots.WString = WString(Vector(WChar(Id(2,1),2,true)))
val ss = s2a.subseq(o1.ns)                        //> ss  : Vector[woots.WChar] = Vector(WChar(Id(2,1),2,true))
ss.head.id                                        //> res0: woots.Id = Id(2,1)
ss.takeWhile(_.id < o1.c.id)                      //> res1: scala.collection.immutable.Vector[woots.WChar] = Vector()
val point = ss.dropWhile(_.id < o1.c.id).headOption.map(_.id)
                                                  //> point  : Option[woots.Id] = Some(Id(2,1))
s2a.prevOf(point)                                 //> res2: Option[woots.Id] = None
 
s2a.subseq( Neighbours(None,Some(Id(2,1))) )      //> res3: Vector[woots.WChar] = Vector()
 
 
 // should be "3124"
               
s2.insertBetween(o2.c,o2.ns).insertBetween(o1.c, o1.ns).insertBetween(o3.c, o3.ns).insertBetween(o4.c, o4.ns).text
                                                  //> * Simple insert of '2' at 0
                                                  //| * Integrating (WChar(Id(1,1),1,true),Neighbours(None,None)) yields Vector(WC
                                                  //| har(Id(2,1),2,true))
                                                  //|   Resolved to Some(Id(2,1))
                                                  //| * Simple insert of '1' at 0
                                                  //| * Simple insert of '3' at 0
                                                  //| * Integrating (WChar(Id(3,2),4,true),Neighbours(Some(Id(1,1)),None)) yields 
                                                  //| Vector(WChar(Id(2,1),2,true))
                                                  //|   Resolved to None
                                                  //| * Simple insert of '4' at 3
                                                  //| res4: String = 3124




                                                  
                                                  
                                                  
                                                  
}