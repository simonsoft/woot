package spiralarm
package woot

// # Each character has an `Id`.
// An `Id` is usually made up of a `SiteId` and a `ClockValue`, but there are two special cases 
// called `Beginning` and `Ending`, because character points to the previous and next character `Id`.

sealed trait Id {
  def < (that: Id) : Boolean
}

object Beginning extends Id {
  def < (that: Id) = that match {
    case Beginning => false
    case _         => true
  }

  override def toString = "Beginning"
}

object Ending extends Id {
  def < (that: Id) = false
  override def toString = "Ending"
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

// # Characters
// Currently coded to be a `Char`, but could be a `T`.
case class WChar(id: CharId, alpha: Char, prev: Id, next: Id, isVisible: Boolean = true)

// # Operations are inserts or deletes
sealed trait Operation {
  def wchar : WChar
  def from: SiteId
  def name : String
}

case class InsertOp(override val wchar : WChar, override val from : SiteId) extends Operation {
  def name = "ins"
}

case class DeleteOp(override val wchar : WChar, override val from : SiteId) extends Operation {
  def name = "del"
}

// # String representation
// Note there there is no `WChar` representation of Beginning and Ending: they are not included in the vector.
// The intention is for this data structure to be immutable: the `integrate` and `delete` operations produce new `WString` instances.
case class WString(
    chars: Vector[WChar] = Vector.empty,
    queue: Vector[Operation] = Vector.empty) {

  lazy val visible = chars.filter(_.isVisible)

  // ## The visible text
  def text : String = visible.map(_.alpha).mkString
  
  // ## Insert a `WChar` into the internal vector at position `pos`, returning a new `WString`
  // Position are indexed from zero
  //
  protected[woot] def ins(char: WChar, pos: Int) : WString = {  
    val (before, after) = chars splitAt pos
    copy(chars = (before :+ char) ++ after)
  }

  // ## Lookup the position in `chars` of a given `id`
  //
  // Note that the `id` is required to exist in the `WString`.
  private def indexOf(id: Id) : Int = {
    val p = id match {
      case Ending    => chars.length
      case Beginning => 0
      case _         => chars.indexWhere(_.id == id)
    }
    require(p != -1)
    p
  }
                   
  // ## The parts of this `WString` between `prev` and `next`
  // ...but excluding the neighbours themselves as required (see [RR5580] p. 8)
  private def subseq(prev: Id, next: Id) : Vector[WChar] = {
    
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

  // # Applicability test
  private def canIntegrate(op: Operation) : Boolean = op match {
    case InsertOp(c,_) => canIntegrate(c.next) && canIntegrate(c.prev)
    case DeleteOp(c,_) => chars.exists(_.id == c.id)
  }

  private def canIntegrate(id: Id) : Boolean =
    id == Ending || id == Beginning || chars.exists(_.id == id)

  // # Waiting integrations
  // If we cannot currently integrate a character, it goes into a queue.
  private def enqueue(op: Operation) : WString = copy(queue = queue :+ op)
  
  private def dequeue : WString = {
    def without(op: Operation) : Vector[Operation] = queue.filterNot(_ == op)
    queue.find(canIntegrate).map(op => copy(queue = without(op)).integrate(op)) getOrElse this
  } 
    
  // # Delete means making the character invisible.
  private def hide(c: WChar) : WString = {
    val p = chars.indexWhere(_.id == c.id)
    require(p != -1)
    val replacement =  c.copy(isVisible=false)
    val (before, rest) = chars splitAt p
    copy(chars = (before :+ replacement) ++ (rest drop 1) )
  }

  // # Integrate an insert or delete, giving a new `WString`
  def integrate(op: Operation) : WString = op match {
    // - Don't insert the same ID twice:
    case InsertOp(c,_) if chars.exists(_.id == c.id) => this

    // - Insert can go ahead if the next & prev exist:
    case InsertOp(c,_) if canIntegrate(op) => integrate(c, c.prev, c.next)

    // - We can delete any char that exists:
    case DeleteOp(c,_) if canIntegrate(op) => hide(c)

    // - Anything else goes onto the queue for another time:
    case _                                 => enqueue(op)
  }

  @scala.annotation.tailrec
  private def integrate(c: WChar, before: Id, after: Id) : WString = {
      
      // Looking at all the characters between the previous and next positions:
      subseq(before, after) match {
          // - when where's no option about where, just insert
          case Vector() =>
            ins(c, indexOf(after)).dequeue
                 
          // - when there's a choice, locate an insert point based on `Id.<`
          case search : Vector[WChar] =>
            val L : Vector[Id] = before +: trim(search).map(_.id) :+ after
            val i = math.max(1, math.min(L.length-1, L.takeWhile( _ < c.id ).length))
            integrate(c, L(i-1), L(i))
      }

  }
  
  // Don't consider characters that have a `prev` or `next` in the set of 
  // locations to consider (i.e., ones that are between the insert points of interest)
  // See last paragraph of first column of page 5 of CSCW06 (relating to figure 4b)
  private def trim(cs: Vector[WChar]) : Vector[WChar] =
    for { 
      c <- cs
      if cs.forall(x => x.id != c.next && x.id != c.prev)
    } yield c

}
