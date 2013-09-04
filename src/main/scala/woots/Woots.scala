package woots

import math.{min,max}


case class Id(ns: SiteId, ng: ClockValue) {
  def inc = copy(ng = ng + 1)
}

object Id {
  def genFrom(seed: Id) : Stream[Id] = Stream.cons(seed, genFrom(seed.inc))
}

// TODO: what is the real starting point?
// private val ids = Id genFrom Id(1,1)


case class WChar(id: Id, alpha: Char, isVisible: Boolean = true)

case class Neighbours(prev: Option[Id], next: Option[Id]) 

object Neighbours {
  lazy val nil = Neighbours(None,None)
}

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

  // ## Insert a WChar into the internal vector at position `pos`
  // NB: position indexed from zero
  //
  def ins(char: WChar, pos: Int) : WString = {  
    // - Bound the insert point between 0 and text.length
    val p = min(text.length, max(pos,0))

    // - "Insert" by creating a new vector
    val (before, after) = chars splitAt p
    WString((before :+ char) ++ after)
  }


  // ## The previous and next at a given visible position
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
  
  // ## Integrate a `WChar` into the string.
  def insertAround(c: WChar, neighs: Neighbours) : WString = ???



}

