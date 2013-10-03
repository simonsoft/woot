object Worksheet {
  import woots._
  import scala.language.postfixOps
  import scala.math._

  val date = new java.util.Date()                 //> date  : java.util.Date = Thu Oct 03 10:14:23 EST 2013

  val L = (0 to 10).toVector                      //> L  : Vector[Int] = Vector(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

  var i = 1                                       //> i  : Int = 1
	
	val c  = 13                               //> c  : Int = 13
	
	val myi = min(L.length -1,L.takeWhile( _ < c ).length);
                                                  //> myi  : Int = 10
  
  while (i < (L.length-1) && (L(i) < c)) i = i + 1;
  
  println(i)                                      //> 10

                                                 
                                                  
}