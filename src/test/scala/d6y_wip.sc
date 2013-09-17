object Worksheet {
import woots._
  
val site2 = WString()                             //> site2  : woots.WString = WString(Vector())
val o1 = WChar(CharId(1,1), '1', Beginning, Ending)
                                                  //> o1  : woots.WChar = WChar(CharId(1,1),1,woots.Beginning$@713a4e73,woots.Endi
                                                  //| ng$@2af49a18,true)
val o2 = WChar(CharId(2,1), '2', Beginning, Ending)
                                                  //> o2  : woots.WChar = WChar(CharId(2,1),2,woots.Beginning$@713a4e73,woots.Endi
                                                  //| ng$@2af49a18,true)
val o3 = WChar(CharId(3,1), '3', Beginning, o1.id)//> o3  : woots.WChar = WChar(CharId(3,1),3,woots.Beginning$@713a4e73,CharId(1,1
                                                  //| ),true)
val o4 = WChar(CharId(3,2), '4', o1.id, Ending)   //> o4  : woots.WChar = WChar(CharId(3,2),4,CharId(1,1),woots.Ending$@2af49a18,t
                                                  //| rue)
val site3 = WString().integrate(o1)               //> * Simple insert of 'WChar(CharId(1,1),1,woots.Beginning$@713a4e73,woots.Endi
                                                  //| ng$@2af49a18,true)'
                                                  //| site3  : woots.WString = WString(Vector(WChar(CharId(1,1),1,woots.Beginning$
                                                  //| @713a4e73,woots.Ending$@2af49a18,true)))


site3.integrate(o3).
          integrate(o4).
          integrate(o2).
          text                                    //> * Simple insert of 'WChar(CharId(3,1),3,woots.Beginning$@713a4e73,CharId(1,1
                                                  //| ),true)'
                                                  //| * Simple insert of 'WChar(CharId(3,2),4,CharId(1,1),woots.Ending$@2af49a18,t
                                                  //| rue)'
                                                  //| * Integrating (WChar(CharId(2,1),2,woots.Beginning$@713a4e73,woots.Ending$@2
                                                  //| af49a18,true)) yields Vector(WChar(CharId(3,1),3,woots.Beginning$@713a4e73,C
                                                  //| harId(1,1),true), WChar(CharId(1,1),1,woots.Beginning$@713a4e73,woots.Ending
                                                  //| $@2af49a18,true), WChar(CharId(3,2),4,CharId(1,1),woots.Ending$@2af49a18,tru
                                                  //| e))
                                                  //|   Resolved to 2 CharId(3,1)
                                                  //| * Simple insert of 'WChar(CharId(2,1),2,woots.Beginning$@713a4e73,woots.Endi
                                                  //| ng$@2af49a18,true)'
                                                  //| res0: String = 2314

site3.integrate(o3).integrate(o4).integrate(o2).text == "3124"
                                                  //> * Simple insert of 'WChar(CharId(3,1),3,woots.Beginning$@713a4e73,CharId(1,1
                                                  //| ),true)'
                                                  //| * Simple insert of 'WChar(CharId(3,2),4,CharId(1,1),woots.Ending$@2af49a18,t
                                                  //| rue)'
                                                  //| * Integrating (WChar(CharId(2,1),2,woots.Beginning$@713a4e73,woots.Ending$@2
                                                  //| af49a18,true)) yields Vector(WChar(CharId(3,1),3,woots.Beginning$@713a4e73,C
                                                  //| harId(1,1),true), WChar(CharId(1,1),1,woots.Beginning$@713a4e73,woots.Ending
                                                  //| $@2af49a18,true), WChar(CharId(3,2),4,CharId(1,1),woots.Ending$@2af49a18,tru
                                                  //| e))
                                                  //|   Resolved to 2 CharId(3,1)
                                                  //| * Simple insert of 'WChar(CharId(2,1),2,woots.Beginning$@713a4e73,woots.Endi
                                                  //| ng$@2af49a18,true)'
                                                  //| res1: Boolean = false
 

 
  /*site2.integrate(o2).
     integrate(o1).
     integrate(o3).
     integrate(o4).
     text == "3124"*/
                                         
                                                  
                                        
                                                  
}