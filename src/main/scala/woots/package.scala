
package object woots {

  type SiteId = Long
  type ClockValue = Long

  // ## `WChar` nodes are sorted by the insert algorithm based on `id`.
  implicit object WCharOrdering extends Ordering[WChar] {
    def compare(a: WChar, b: WChar) : Int = 
      if (a.id < b.id) -1 else 1
  }
  
}
