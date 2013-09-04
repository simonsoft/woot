package woots

import math.{min,max}


  case class Id(ns: SiteId, ng: ClockValue) {
    def inc = copy(ng = ng + 1)
  }

  object Id {
   def genFrom(seed: Id) : Stream[Id] = Stream.cons(seed, genFrom(seed.inc))
   //val Beginning = Id(-1,0)
   //val Ending = Id(-1,1)
  }

  // TODO: what is the real starting point?
  // private val ids = Id genFrom Id(1,1)


  case class WChar(id: Id, alpha: Char, isVisible: Boolean = true)

  case class Neighbours(prev: Option[WChar], next: Option[WChar]) 
  
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

      // - "Insert" by create a new vector
      val (before, after) = chars splitAt p
      WString((before :+ char) ++ after)
     }

    
    // ## The previous and next at a given visible position
    def neighbours(visiblePosition: Int) : Neighbours = 
      Neighbours.nil
      

  }

