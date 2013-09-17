
object Worksheet {
import woots._
 
 7 +: Vector(8) :+ 9                              //> res0: scala.collection.immutable.Vector[Int] = Vector(7, 8, 9)
 
 
val site2 = WString()                             //> site2  : woots.WString = WString(Vector())
      
val o1 = WChar(CharId(1,1), '1', Beginning, Ending)
                                                  //> o1  : woots.WChar = WChar(CharId(1,1),1,woots.Beginning$@dd86b03,woots.Endin
                                                  //| g$@7b59990e,true)
val o2 = WChar(CharId(2,1), '2', Beginning, Ending)
                                                  //> o2  : woots.WChar = WChar(CharId(2,1),2,woots.Beginning$@dd86b03,woots.Endin
                                                  //| g$@7b59990e,true)
val o3 = WChar(CharId(3,1), '3', Beginning, o1.id)//> o3  : woots.WChar = WChar(CharId(3,1),3,woots.Beginning$@dd86b03,CharId(1,1)
                                                  //| ,true)
val o4 = WChar(CharId(3,2), '4', o1.id, Ending)   //> o4  : woots.WChar = WChar(CharId(3,2),4,CharId(1,1),woots.Ending$@7b59990e,t
                                                  //| rue)
 site2.integrate(o2).
     integrate(o1).text                           //> * Simple insert of 'WChar(CharId(2,1),2,woots.Beginning$@dd86b03,woots.Endin
                                                  //| g$@7b59990e,true)'
                                                  //| * Integrating (WChar(CharId(1,1),1,woots.Beginning$@dd86b03,woots.Ending$@7b
                                                  //| 59990e,true)) yields Vector(WChar(CharId(2,1),2,woots.Beginning$@dd86b03,woo
                                                  //| ts.Ending$@7b59990e,true))
                                                  //|   Resolved to 1 CharId(2,1)
                                                  //| * Simple insert of 'WChar(CharId(1,1),1,woots.Beginning$@dd86b03,woots.Endin
                                                  //| g$@7b59990e,true)'
                                                  //| res1: String = 12
  site2.integrate(o2).
     integrate(o1).
     integrate(o3).
     text                                         //> * Simple insert of 'WChar(CharId(2,1),2,woots.Beginning$@dd86b03,woots.Endin
                                                  //| g$@7b59990e,true)'
                                                  //| * Integrating (WChar(CharId(1,1),1,woots.Beginning$@dd86b03,woots.Ending$@7b
                                                  //| 59990e,true)) yields Vector(WChar(CharId(2,1),2,woots.Beginning$@dd86b03,woo
                                                  //| ts.Ending$@7b59990e,true))
                                                  //|   Resolved to 1 CharId(2,1)
                                                  //| * Simple insert of 'WChar(CharId(1,1),1,woots.Beginning$@dd86b03,woots.Endin
                                                  //| g$@7b59990e,true)'
                                                  //| * Simple insert of 'WChar(CharId(3,1),3,woots.Beginning$@dd86b03,CharId(1,1)
                                                  //| ,true)'
                                                  //| res2: String = 312
          
  site2.integrate(o2).
     integrate(o1).
     integrate(o3).
     integrate(o4).
     text                                         //> * Simple insert of 'WChar(CharId(2,1),2,woots.Beginning$@dd86b03,woots.Endin
                                                  //| g$@7b59990e,true)'
                                                  //| * Integrating (WChar(CharId(1,1),1,woots.Beginning$@dd86b03,woots.Ending$@7b
                                                  //| 59990e,true)) yields Vector(WChar(CharId(2,1),2,woots.Beginning$@dd86b03,woo
                                                  //| ts.Ending$@7b59990e,true))
                                                  //|   Resolved to 1 CharId(2,1)
                                                  //| * Simple insert of 'WChar(CharId(1,1),1,woots.Beginning$@dd86b03,woots.Endin
                                                  //| g$@7b59990e,true)'
                                                  //| * Simple insert of 'WChar(CharId(3,1),3,woots.Beginning$@dd86b03,CharId(1,1)
                                                  //| ,true)'
                                                  //| * Integrating (WChar(CharId(3,2),4,CharId(1,1),woots.Ending$@7b59990e,true))
                                                  //|  yields Vector(WChar(CharId(2,1),2,woots.Beginning$@dd86b03,woots.Ending$@7b
                                                  //| 59990e,true))
                                                  //|   Resolved to 2 woots.Ending$@7b59990e
                                                  //| * Simple insert of 'WChar(CharId(3,2),4,CharId(1,1),woots.Ending$@7b59990e,t
                                                  //| rue)'
                                                  //| res3: String = 3124
 

 
  site2.integrate(o2).
     integrate(o1).
     integrate(o3).
     integrate(o4).
     text == "3124"                               //> * Simple insert of 'WChar(CharId(2,1),2,woots.Beginning$@dd86b03,woots.Endin
                                                  //| g$@7b59990e,true)'
                                                  //| * Integrating (WChar(CharId(1,1),1,woots.Beginning$@dd86b03,woots.Ending$@7b
                                                  //| 59990e,true)) yields Vector(WChar(CharId(2,1),2,woots.Beginning$@dd86b03,woo
                                                  //| ts.Ending$@7b59990e,true))
                                                  //|   Resolved to 1 CharId(2,1)
                                                  //| * Simple insert of 'WChar(CharId(1,1),1,woots.Beginning$@dd86b03,woots.Endin
                                                  //| g$@7b59990e,true)'
                                                  //| * Simple insert of 'WChar(CharId(3,1),3,woots.Beginning$@dd86b03,CharId(1,1)
                                                  //| ,true)'
                                                  //| * Integrating (WChar(CharId(3,2),4,CharId(1,1),woots.Ending$@7b59990e,true))
                                                  //|  yields Vector(WChar(CharId(2,1),2,woots.Beginning$@dd86b03,woots.Ending$@7b
                                                  //| 59990e,true))
                                                  //|   Resolved to 2 woots.Ending$@7b59990e
                                                  //| * Simple insert of 'WChar(CharId(3,2),4,CharId(1,1),woots.Ending$@7b59990e,t
                                                  //| rue)'
                                                  //| res4: Boolean = true
                                         
                                                  
                                        
                                                  
}