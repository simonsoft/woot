object Worksheet {
import woots._
  
  
val site = WString()                              //> site  : woots.WString = WString(Vector(),Vector())
val o1 = WChar(CharId(1,1), '1', Beginning, Ending)
                                                  //> o1  : woots.WChar = WChar(CharId(1,1),1,woots.Beginning$@3f03c0ad,woots.Endi
                                                  //| ng$@f5ebe2d,true)
val o2 = WChar(CharId(2,1), '2', Beginning, Ending)
                                                  //> o2  : woots.WChar = WChar(CharId(2,1),2,woots.Beginning$@3f03c0ad,woots.Endi
                                                  //| ng$@f5ebe2d,true)
val o3 = WChar(CharId(3,1), '3', Beginning, o1.id)//> o3  : woots.WChar = WChar(CharId(3,1),3,woots.Beginning$@3f03c0ad,CharId(1,1
                                                  //| ),true)
val o4 = WChar(CharId(3,2), '4', o1.id, Ending)   //> o4  : woots.WChar = WChar(CharId(3,2),4,CharId(1,1),woots.Ending$@f5ebe2d,tr
                                                  //| ue)
  site.integrate(o2).
     integrate(o1).
     integrate(o3).
     delete(o2).
     delete(o4).
     delete(o1).
     delete(o3).
     integrate(o4).
     text                                         //> res0: String = ""
 
  site.integrate(o2).
     integrate(o1).
     integrate(o3).
     integrate(o4).
     text == "3124"                               //> res1: Boolean = true
                                         
                                                  
                                        
                                                  
}