object Worksheet {
  import woots._

  val date = new java.util.Date()                 //> date  : java.util.Date = Fri Sep 27 12:16:08 EST 2013

	val vector = Vector(Beginning,CharId(1,0),CharId(1,1),CharId(1,2),CharId(1,3),CharId(1,4),CharId(1,5),CharId(1,6),CharId(1,7),Ending)
                                                  //> vector  : scala.collection.immutable.Vector[woots.Id] = Vector(woots.Beginni
                                                  //| ng$@19f7456f, CharId(1,0), CharId(1,1), CharId(1,2), CharId(1,3), CharId(1,4
                                                  //| ), CharId(1,5), CharId(1,6), CharId(1,7), woots.Ending$@58d6c6a0)

	val character = WChar(CharId(1,4), 'M', Beginning, Ending)
                                                  //> character  : woots.WChar = WChar(CharId(1,4),M,woots.Beginning$@19f7456f,woo
                                                  //| ts.Ending$@58d6c6a0,true)
                                                  
  val string = WString()                          //> string  : woots.WString = WString(Vector(),Vector())
  
  val i = string.ith(vector,character)            //> i  : Int = 4
  val vi = string.ith(vector,character)           //> vi  : Int = 4
  vector(i)                                       //> res0: woots.Id = CharId(1,3)
  
  vector(vi)                                      //> res1: woots.Id = CharId(1,3)
                                                  
                                                  
}