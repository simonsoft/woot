package woots

import math.{min,max}

sealed trait Id {
  def < (that: Id) : Boolean
}

case class CharId(ns: SiteId, ng: ClockValue) extends Id { 
  def inc = copy(ng = ng + 1)
  def < (that: Id) = that match {
    case CharId(site, clock) => (ns < site) || (ns == site && ng < clock)  
    case _ => !(that < this)
  }
}

object Beginning extends Id {
  def < (that: Id) = true
}

object Ending extends Id {
  def < (that: Id) = false
}


object CharId {
  def genFrom(seed: CharId) : Stream[CharId] = Stream.cons(seed, genFrom(seed.inc))
}

// TODO: what is the real starting point?
// private val ids = Id genFrom Id(1,1)


case class WChar(id: Id, alpha: Char, prev: Id, next: Id, isVisible: Boolean = true) 


case class WString(chars: Vector[WChar] = Vector.empty) {

  private lazy val visible = chars.filter(_.isVisible)

  // ## The visible text
  def text : String = visible.map(_.alpha).mkString

  // ## Insert a `WChar` into the internal vector at position `pos`
  // NB: position indexed from zero
  //
  protected[woots] def ins(char: WChar, pos: Int) : WString = {  
    // - Bound the insert point between 0 and text.length
    val p = min(text.length, max(pos,0))

    // - "Insert" by creating a new vector
    val (before, after) = chars splitAt p
    WString((before :+ char) ++ after)
  }

  // ## Compute the previous `Id` for a given visible position.
  // For use when we are going to insert a new character at a
  // particular position. 
  // 
  // For example, given the `WString` "AB":
  //
  //    prevIdAt(0) = Beginning
  //    prevIdAt(1) = A.id
  //    prevIdAt(2) = B.id
  
  private def prevIdAt(visibleInsertPos: Int) : Id = {
    require(visibleInsertPos >= 0 && visibleInsertPos <= visible.length)
    visibleInsertPos match {
      case 0 => Beginning
      case n => visible(n-1).id
    }
  }
  

  // ## Compute the next `Id` for a given visible position.
  // For use when we are going to insert a new character at a
  // particular position. 
  // 
  // For example, given the `WString` "AB":
  //
  //    nextIdAt(0) = A.id
  //    nextIdAt(1) = B.id
  //    nextIdAt(2) = Ending
  private def nextIdAt(visibleInsertPos: Int) : Id = {
    require(visibleInsertPos >= 0 && visibleInsertPos <= visible.length)
    visibleInsertPos match {
      case n if n >= visible.length => Ending
      case n                        => visible(n).id
    }
  }
  
  private def index(id: Id) : Option[Int] = 
    chars.indexWhere(_.id == id) match {
    case -1 => None
    case n  => Some(n)
  } 
  
  private def indexOf(id: Id) : Int = {
    val p = id match {
      case Ending => chars.length
      case Beginning => 0
      case _ => chars.indexWhere(_.id == id) 
    }
    require(p != -1)
    p
  }
  
//  // ## Test to see if the conditions for a character insert/update apply
//  def canApply(ns: Neighbours) : Boolean = {
//    
//    // Either:
//    //
//    // - the prev (or next) signifies the beginning (or end) position; or
//    // - the prev (or next) can be found
//    def canApply(id: Option[Id]) : Boolean =
//        id.isEmpty || id.flatMap(index).isDefined
//        
//    canApply(ns.prev) && canApply(ns.next)
//  }
    
  // ## Compute index just after the location of the `previous` `ID`.
  //
  // For example, considering "AB"...
  //
  //     inserting X around Beginning = 0
  //     inserting X around A.id = 1
  //     inserting X around B.id = 2 (append)
  private def insertIndexAfter(prev: Id) : Int =
      index(prev).map(_ + 1) getOrElse 0
                
  // ## The parts of this `WString` between `prev` and `next`
  // ...but excluding the neighbours themselves.
  private def subseq(prev: Id, next: Id) : Vector[WChar] = {
    // Precondition: `require(canApply(ns))`  
    
    val from = prev match {
      case Beginning => 0
      case id => chars.indexWhere(_.id == id) + 1
    }
     
    val until = next match {
      case Ending => chars.length
      case id => chars.indexWhere(_.id == id)
    } 
    
    chars.slice(from,until)  
  }


  def integrate(c: WChar) : WString = integrate(c, c.prev, c.next)
  
  def integrate(c: WChar, before: Id, after: Id) : WString = {
      
      // Looking at all the characters between the previous and next positions:
      subseq(before, after) match {
          // - when where's no decision about where, just insert
          case Vector() => 
            println(s"* Simple insert of '${c}'")
            ins(c, indexOf(after))
          
          // - when there's a choice, locate an insert point based on `Id.<`
          case search : Vector[WChar] => 
            println(s"* Integrating ($c) yields $search")
                        
            var i = 1
            val L : Vector[Id] = before +: search.sorted.map(_.id) :+ after
            while (i < (L.length - 1) && L(i) < c.id) i = i + 1 
            
            println(s"  Resolved to $i ${L(i)}")
            integrate(c, L(i-1), L(i) )
      }

  }
  
      


}

