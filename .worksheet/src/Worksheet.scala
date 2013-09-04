
object Worksheet {
import woots._;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(57); 

val v = Vector(1,2,3);System.out.println("""v  : scala.collection.immutable.Vector[Int] = """ + $show(v ));$skip(25); 
val (b,a) = v.splitAt(4);System.out.println("""b  : scala.collection.immutable.Vector[Int] = """ + $show(b ));System.out.println("""a  : scala.collection.immutable.Vector[Int] = """ + $show(a ));$skip(25); val res$0 = 
            
b ++ a ++ b;System.out.println("""res0: scala.collection.immutable.Vector[Int] = """ + $show(res$0));$skip(34); 

 
 
val w1 = WChar(Id(1,1), 'A');System.out.println("""w1  : woots.WChar = """ + $show(w1 ));$skip(29); 
val w2 = WChar(Id(1,2), 'B');System.out.println("""w2  : woots.WChar = """ + $show(w2 ));$skip(29); 
val w3 = WChar(Id(1,3), 'C');System.out.println("""w3  : woots.WChar = """ + $show(w3 ));$skip(26); val res$1 = 

WString().ins(w1,0).text;System.out.println("""res1: String = """ + $show(res$1));$skip(36); val res$2 = 

WString().ins(w1,0).ins(w2,1).text;System.out.println("""res2: String = """ + $show(res$2));$skip(36); val res$3 = 

WString().ins(w1,0).ins(w2,0).text;System.out.println("""res3: String = """ + $show(res$3));$skip(55); 

val s1 = WString().ins(w1,0).ins(w2,1).ins(w3,2).text;System.out.println("""s1  : String = """ + $show(s1 ))}


}
