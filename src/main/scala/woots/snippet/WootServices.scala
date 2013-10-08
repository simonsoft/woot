package woots
package snippet

import scala.xml.NodeSeq
import scala.collection.immutable.Stream._

import net.liftweb.http.js.JsCmds._
import net.liftweb.http.{S, LiftSession, RoundTripInfo }
import net.liftweb.json._

import net.liftweb.common.Loggable
import net.liftweb.http.js.JE._

object Trace extends Loggable {
  def apply[T](f : => T) : T = {
    val r = f
    logger.info(s"Trace($r)")
    r
  }
}

object WootServices extends Loggable {

  implicit val formats = DefaultFormats

  def render = S.session.map(javascript) openOr NodeSeq.Empty

  private def javascript(s: LiftSession): NodeSeq = Script {
    OnLoad( SetExp(JsVar("wootServer"), s buildRoundtrip services))
  }

  private def services = List[RoundTripInfo](
    "send" -> receive _,
    "init" -> init _)

  import JsonFormats._

  private def receive(jop: JValue): JValue = {
    Broadcaster ! PushToQueue(jop)
    JNull
  }

  def maxClockValue(site: SiteId, model: WString) : ClockValue =
    model.chars.filter(_.id.ns == site).
                map(_.id.ng).
                foldLeft(0L) { math.max }

  // TODO: how to identify the document the user wants to work with?
  // TODO: how to signify a new/unsaved document?
  private def init(config: JValue) : Stream[JValue] = {
    logger.info(s"Loading WOOT model $config")

    val stream = for {
      site <- (S.session.map(_.uniqueId))
      Setup(snapshot, queue) <- ((Broadcaster !! AddSite(site)).asA[Setup])
      initClockValue = (maxClockValue(site, snapshot))
    } yield toJson(snapshot,site,initClockValue) #:: Stream.continually(queue.take())

    stream openOr Stream.empty
  }

}