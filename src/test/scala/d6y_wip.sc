
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
 
 val s3 = WString().insertBetween(o1.c, o2.ns)    //> s3  : woots.WString = WString(Vector(WChar(Id(1,1),1,true)))
   
   
 val o3 = Operation(WChar(Id(3,1),'3'), s3.neighbours(0))
                                                  //> o3  : Worksheet.Operation = Operation(WChar(Id(3,1),3,true),Neighbours(None,
                                                  //| Some(Id(1,1))))
 val o4 = Operation(WChar(Id(3,2),'4'), s3.neighbours(1))
                                                  //> o4  : Worksheet.Operation = Operation(WChar(Id(3,2),4,true),Neighbours(Some(
                                                  //| Id(1,1)),None))
 
 // should be "3124"
s2.insertBetween(o2.c,o2.ns).insertBetween(o1.c, o1.ns).insertBetween(o3.c, o3.ns).insertBetween(o4.c, o4.ns).text
                                                  //> Ins(WChar(Id(1,1),1,true),Neighbours(None,None)) : Vector(WChar(Id(2,1),2,tr
                                                  //| ue))
                                                  //| Ins(WChar(Id(3,1),3,true),Neighbours(None,Some(Id(1,1)))) : Vector(WChar(Id(
                                                  //| 2,1),2,true))
                                                  //| Ins(WChar(Id(3,2),4,true),Neighbours(Some(Id(1,1)),None)) : Vector(WChar(Id(
                                                  //| 2,1),2,true))
                                                  //| res0: String = 2
  
 


}