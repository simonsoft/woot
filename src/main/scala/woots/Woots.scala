package woots

import math.{min,max}


case class Id(ns: SiteId, ng: ClockValue) { 
  def inc = copy(ng = ng + 1)
  def < (that: Id) = (ns < that.ns) || (ns == that.ns && ng < that.ng)  
}

object Id {
  def genFrom(seed: Id) : Stream[Id] = Stream.cons(seed, genFrom(seed.inc))
}

// TODO: what is the real starting point?
// private val ids = Id genFrom Id(1,1)


case class WChar(id: Id, alpha: Char, isVisible: Boolean = true) 


// Neighbours
// ==========
// The `Id` of the `WChar` before and after a new `WChar`
// Using `None` to signify beginning and ending (which may be a mistake).
case class Neighbours(prev: Option[Id], next: Option[Id]) 

// String representation
// =====================
//
// The string is a `Vector` of `WChar`. The `next` and `previous`
// are implied for each `WChar` based on the `WChar` position in
// the vector.
case class WString(chars: Vector[WChar] = Vector.empty) {

  private lazy val visible = chars.filter(_.isVisible)

  // ## The visible text
  def text : String = visible.map(_.alpha).mkString

  // ## Insert a `WChar` into the internal vector at position `pos`
  // NB: position indexed from zero
  //
  def ins(char: WChar, pos: Int) : WString = {  
    // - Bound the insert point between 0 and text.length
    val p = min(text.length, max(pos,0))

    // - "Insert" by creating a new vector
    val (before, after) = chars splitAt p
    WString((before :+ char) ++ after)
  }


  // ## Compute the previous and next for a given visible position.
  // For use when we are going to insert a new character at a
  // particular position. 
  //
  // For example, given the `WString` "ABC":
  //
  //     neighbours(0) =~ (None, A.id)
  //     neighbours(1) =~ (A.id, B.id)
  def neighbours(visibleInsertPos: Int) : Neighbours = {
    require(visibleInsertPos >= 0 && visibleInsertPos <= visible.length)

    visibleInsertPos match {
      case _ if visible.length == 0 => Neighbours(None                 , None)
      case 0 						            => Neighbours(None                 , Some(visible.head.id))
      case n if n >= visible.length => Neighbours(Some(visible.last.id), None)
      case n 						            => Neighbours(Some(visible(n-1).id), Some(visible(n).id))
    }

  }
  
  private def index(id: Id) : Option[Int]  = 
    chars.indexWhere(_.id == id) match {
    case -1 => None
    case n  => Some(n)
  } 
  
  // ## Test to see if the conditions for a character insert/update apply
  def canApply(ns: Neighbours) : Boolean = {
    
    // Either:
    //
    // - the prev (or next) signifies the beginning (or end) position; or
    // - the prev (or next) can be found
    def canApply(id: Option[Id]) : Boolean =
        id.isEmpty || id.flatMap(index).isDefined
        
    canApply(ns.prev) && canApply(ns.next)
  }
    
  // ## Compute index just after the location of the `previous` `ID`.
  //
  // For example, considering "ABC"...
  //
  //     inserting X around None, B.id = 0
  //     inserting X around A.id, B.id = 1
  //     inserting X around B.id, C.id = 2
  //     inserting X around C.id, None = 3 (append) 
  def insertIndexAfter(prev: Option[Id]) : Int =
      prev.flatMap(index).map(_ + 1) getOrElse 0
      
  // ## Compute the previous node of a given node.
  // TODO: make less ugly - is the ugly a consequence of `None` for end marker?
  def prevOf(node: Option[Id]) : Option[Id] = 
    node match {
      case None => Some(chars.last.id)
      case _    => for {
        prevIndex <- node.flatMap(index).map(_ - 1)
        if prevIndex >= 0
      } yield chars(prevIndex).id
  }
          
  // ## The parts of this `WString` between `neighs.prev` and `neighs.next`
  // ...but excluding the neighbours themselves.
  def subseq(ns: Neighbours) : Vector[WChar] = {
    // Precondition: `require(canApply(ns))`  
    
    val from = insertIndexAfter(ns.prev)
    
    val until = 
      ns.next.flatMap(index) getOrElse chars.length
    
    chars.slice(from,until)  
  }


      
  // ## Integrate a `WChar` into the `WString`.
  def insertBetween(c: WChar, ns: Neighbours) : WString = {
      // Precondition: `require(canApply(ns))`  

      // Looking at all the characters between the previous and next positions:
      subseq(ns) match {
          // - when where's no decision about where, just insert after `prev`:
          // (NB: I believe this could be insert _at_ `ns.next` instead of _after_ `ns.prev`)
          case Vector() => 
            println(s"* Simple insert of '${c.alpha}' at ${insertIndexAfter(ns.prev)}")
            ins(c, insertIndexAfter(ns.prev))
          
          // - when there's a choice, locate an insert point based on `Id.<`
          case search : Vector[WChar] => 
            println(s"* Integrating ($c,$ns) yields $search")
            val point = search.sorted.dropWhile(_.id < c.id).headOption.map(_.id)
            println(s"  Resolved to $point")
            insertBetween(c, Neighbours(prevOf(point), point))
      }

  }
  
      


}

