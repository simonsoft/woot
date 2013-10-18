package spiralarm
package woot
package snippet

import scala.xml.NodeSeq
import scala.collection.immutable.Stream._

import net.liftweb.http.js.JsCmds._
import net.liftweb.http.{S, LiftSession, RoundTripInfo }
import net.liftweb.json._

import net.liftweb.common.Loggable
import net.liftweb.http.js.JE._
import net.liftweb.util.Helpers

object WootServices extends Loggable {

  def render = S.session.map(javascript) openOr NodeSeq.Empty

  private def javascript(s: LiftSession): NodeSeq = Script {
    OnLoad(SetExp(JsVar("wootServer"), s buildRoundtrip services))
  }

  private def services = List[RoundTripInfo](
    "send" -> receive _,
    "init" -> init _)


  private def receive(jop: JValue): JValue = {
    Broadcaster ! PushToQueue(jop)
    JNull
  }

  // TODO: Identify the document the user wants to work with, or new/unsaved docs.
  private def init(config: JValue) : Stream[JValue] = {
    logger.info(s"Loading WOOT model $config")

    import JsonFormats._

    val site = Helpers.nextFuncName

    // Ensure site is cleaned up when session expires
    S.session.foreach(_.addSessionCleanup { _ => Broadcaster ! RemoveSite(site) } )

    val stream = for {
      Setup(snapshot, queue) <- (Broadcaster !! AddSite(site)).asA[Setup]
      initClockValue         =  maxClockValue(site, snapshot)
    } yield toJson(snapshot,site,initClockValue) #:: Stream.continually(queue.take())

    stream openOr Stream.empty
  }

  // The largest clock value in the model for the site is the starting clock value for that site.
  def maxClockValue(site: SiteId, model: WString) : ClockValue =
    model.chars.filter(_.id.ns == site).map(_.id.ng).foldLeft(0L) {
      math.max
    }

}