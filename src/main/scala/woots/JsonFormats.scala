package woots

object JsonFormats {

  // TODO: This packaging/repackaging is dull. Why not use the same classes for JavaScript and WString ?

  case class JId(site: Option[Int], clock: Option[Int], beginning: Option[Boolean], ending: Option[Boolean]) {
    def toId : Id = (beginning.isDefined, ending.isDefined, site, clock) match {
      case (true , false, _, _ ) => Beginning
      case (false, true , _, _ ) => Ending
      case (  _,    _   , Some(s), Some(c)) => CharId(s,c)
    }
  }

  case class JCharId(site: Int, clock: Int) {
    def toId = CharId(site,clock)
  }

  case class JWChar(alpha: String, id:JCharId, prev: JId, next: JId, isVisible: Boolean) {
    def toWChar : WChar = WChar(id.toId, alpha.head, prev.toId, next.toId, isVisible=isVisible)
  }

  case class JOp(op: String, from: SiteId, wchar: JWChar) {
    def toOperation : Operation = op match {
      case "ins" => InsertOp(wchar.toWChar, from)
      case "del" => DeleteOp(wchar.toWChar, from)
    }
  }

  def toJson(wchar: WChar) : JWChar =
    JWChar(wchar.alpha.toString, toJson(wchar.id), toJson(wchar.prev), toJson(wchar.next), wchar.isVisible)

  def toJson(id: CharId) = JCharId(id.ns, id.ng)

  def toJson(id: Id) : JId =
    id match {
      case Beginning          => JId(None,       None,        Some(true), None)
      case Ending             => JId(None,       None,        None,       Some(true))
      case CharId(site,clock) => JId(Some(site), Some(clock), None,       None)
    }
}
