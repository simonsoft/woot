package woots

import math.{min,max}

// ## References
// - [RR5580] Oster et al. (2005) _Real time group editors without Operational transformation_, report paper 5580, INRIA.
// - [CSCW06] Oster et al. (2006) _Data Consistency for P2P Collaborative Editing_, CSCW'06.

// # Each character has an `Id`.
// An `Id` is usually made up of a `SiteId` and a `ClockValue`, but there are two special cases 
// called `Beginning` and `Ending`.
//
// These special values exist because every character points to the previous and next character `Id`
// of where it wanted to be inserted. The special values are for the first and last
// characters to point to.

sealed trait Id {
  def < (that: Id) : Boolean
}

object Beginning extends Id {
  def < (that: Id) = true
}

object Ending extends Id {
  def < (that: Id) = false
}

case class CharId(ns: SiteId, ng: ClockValue) extends Id { 
  // Each new character retains the site id, but increments the logical local clock, making the `Id` unique.
  def inc = copy(ng = ng + 1)
  
  // The `<` comparison is defined in _Definition 7_ (p. 8) of [RR5580]
  def < (that: Id) = that match {
    case CharId(site, clock) => (ns < site) || (ns == site && ng < clock)  
    case Beginning           => false
    case Ending              => true
  }
}

object CharId {
  def genFrom(seed: CharId) : Stream[CharId] = Stream.cons(seed, genFrom(seed.inc))
}


// # Characters
// Currently coded to be a `Char`, but could be a `T`.
case class WChar(id: Id, alpha: Char, prev: Id, next: Id, isVisible: Boolean = true) 


// # String representation
// Note there there is no `WChar` representation of Beginning and Ending: they are not included in the vector.
case class WString(chars: Vector[WChar] = Vector.empty) {

  private lazy val visible = chars.filter(_.isVisible)

  // ## The visible text
  def text : String = visible.map(_.alpha).mkString

  // ## Insert a `WChar` into the internal vector at position `pos`, returning a new `WSring`
  // Position are indexed from zero
  //
  protected[woots] def ins(char: WChar, pos: Int) : WString = {  
    // - Bound the insert point between 0 and text.length
    val p = min(text.length, max(pos,0))

    // - "Insert" by creating a new vector
    val (before, after) = chars splitAt p
    WString((before :+ char) ++ after)
  }

  // ## Lookup the position in `chars` of a given `id`
  //
  // Note that the `id` is required to exist in the `WString`.
  private def indexOf(id: Id) : Int = {
    val p = id match {
      case Ending => chars.length
      case Beginning => 0
      case _ => chars.indexWhere(_.id == id) 
    }
    require(p != -1)
    p
  }
      
                
  // ## The parts of this `WString` between `prev` and `next`
  // ...but excluding the neighbours themselves as required
  // by the Woot algorithm: see [RR5580] p. 8. 
  private def subseq(prev: Id, next: Id) : Vector[WChar] = {
    // Precondition: `require(canApply(ns))`  
    
    val from = prev match {
      case Beginning => 0
      case id        => indexOf(id) + 1
    }
     
    val until = next match {
      case Ending => chars.length
      case id     => indexOf(id)
    } 
    
    chars.slice(from,until)  
  }


  // # Integration is the process of merging a `WChar` into a `WString` producing a new `WString`
  def integrate(c: WChar) : WString = integrate(c, c.prev, c.next)
  
  def integrate(c: WChar, before: Id, after: Id) : WString = {
      
      // Looking at all the characters between the previous and next positions:
      subseq(before, after) match {
          // - when where's no option about where, just insert
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

