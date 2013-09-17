object Worksheet {
import woots._
  
val site2 = WString()                             //> site2  : woots.WString = WString(Vector())
val o1 = WChar(CharId(1,1), '1', Beginning, Ending)
                                                  //> o1  : woots.WChar = WChar(CharId(1,1),1,woots.Beginning$@6803514a,woots.Endi
                                                  //| ng$@31ce069f,true)
val o2 = WChar(CharId(2,1), '2', Beginning, Ending)
                                                  //> o2  : woots.WChar = WChar(CharId(2,1),2,woots.Beginning$@6803514a,woots.Endi
                                                  //| ng$@31ce069f,true)
val o3 = WChar(CharId(3,1), '3', Beginning, o1.id)//> o3  : woots.WChar = WChar(CharId(3,1),3,woots.Beginning$@6803514a,CharId(1,1
                                                  //| ),true)
val o4 = WChar(CharId(3,2), '4', o1.id, Ending)   //> o4  : woots.WChar = WChar(CharId(3,2),4,CharId(1,1),woots.Ending$@31ce069f,t
                                                  //| rue)
val site3 = WString().integrate(o1)               //> * Simple insert of '1'
                                                  //| site3  : woots.WString = WString(Vector(WChar(CharId(1,1),1,woots.Beginning$
                                                  //| @6803514a,woots.Ending$@31ce069f,true)))


site3.integrate(o3).
          integrate(o4).
          integrate(o2).
          text                                    //> * Simple insert of '3'
                                                  //| * Simple insert of '4'
                                                  //| * Integrating (WChar(CharId(2,1),2,woots.Beginning$@6803514a,woots.Ending$@3
                                                  //| 1ce069f,true)) yields Vector(3, 1, 4)
                                                  //| Reduced: Vector(1)
                                                  //|   Resolved to 2 woots.Ending$@31ce069f
                                                  //| * Integrating (WChar(CharId(2,1),2,woots.Beginning$@6803514a,woots.Ending$@3
                                                  //| 1ce069f,true)) yields Vector(4)
                                                  //| Reduced: Vector(4)
                                                  //|   Resolved to 1 CharId(3,2)
                                                  //| * Simple insert of '2'
                                                  //| res0: String = 3124

site3.integrate(o3).integrate(o4).integrate(o2).text == "3124"
                                                  //> * Simple insert of '3'
                                                  //| * Simple insert of '4'
                                                  //| * Integrating (WChar(CharId(2,1),2,woots.Beginning$@6803514a,woots.Ending$@3
                                                  //| 1ce069f,true)) yields Vector(3, 1, 4)
                                                  //| Reduced: Vector(1)
                                                  //|   Resolved to 2 woots.Ending$@31ce069f
                                                  //| * Integrating (WChar(CharId(2,1),2,woots.Beginning$@6803514a,woots.Ending$@3
                                                  //| 1ce069f,true)) yields Vector(4)
                                                  //| Reduced: Vector(4)
                                                  //|   Resolved to 1 CharId(3,2)
                                                  //| * Simple insert of '2'
                                                  //| res1: Boolean = true
  

 
  /*site2.integrate(o2).
     integrate(o1).
     integrate(o3).
     integrate(o4).
     text == "3124"*/
                                         
                                                  
                                        
                                                  
}