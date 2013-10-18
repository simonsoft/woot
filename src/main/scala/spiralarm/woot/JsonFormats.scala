package spiralarm
package woot

import net.liftweb.json._
import net.liftweb.json.JsonAST.JValue
import net.liftweb.json.JsonDSL._

object JsonFormats {

  implicit val formats = DefaultFormats

  case class JId(site: Option[SiteId], clock: Option[Int], beginning: Option[Boolean], ending: Option[Boolean]) {
    def toId: Id = (beginning.isDefined, ending.isDefined, site, clock) match {
      case (true, false, _, _) ⇒ Beginning
      case (false, true, _, _) ⇒ Ending
      case (_, _, Some(s), Some(c)) ⇒ CharId(s, c)
    }
  }

  case class JCharId(site: SiteId, clock: Int) {
    def toId = CharId(site, clock)
  }

  case class JWChar(alpha: String, id: JCharId, prev: JId, next: JId, isVisible: Boolean) {
    def toWChar: WChar = WChar(id.toId, alpha.head, prev.toId, next.toId, isVisible = isVisible)
  }

  case class JOp(op: String, from: SiteId, wchar: JWChar) {
    def toOperation: Operation = op match {
      case "ins" ⇒ InsertOp(wchar.toWChar, from)
      case "del" ⇒ DeleteOp(wchar.toWChar, from)
    }
  }

  def toJson(op: Operation): JValue =
    ("op" -> op.name) ~ ("from" -> op.from) ~ ("wchar" -> toJson(op.wchar))

  def toJson(wchar: WChar): JValue = {
    import wchar._
    ("alpha" -> alpha.toString) ~
      ("isVisible" -> isVisible) ~
      ("id" -> toJson(id)) ~
      ("prev" -> toJson(prev)) ~
      ("next" -> toJson(next))
  }

  def toJson(id: CharId): JValue =
    ("site" -> id.ns) ~ ("clock" -> id.ng)

  def toJson(id: Id): JValue =
    id match {
      case Beginning ⇒ ("beginning" -> true)
      case Ending ⇒ ("ending" -> true)
      case ci: CharId ⇒ toJson(ci)
    }

  def toJson(model: WString, site: SiteId, initClockValue: ClockValue) : JValue = {
    val chars: JValue = model.chars.map(toJson)
    val queue: JValue = model.queue.map(toJson)
    val doc = ("chars" -> chars) ~ ("queue" -> queue) ~ ("site" -> site) ~ ("clockValue" -> initClockValue)
    doc
  }
}
