package woots
package snippet

import scala.xml.NodeSeq
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.{RoundTripHandlerFunc, S, LiftSession, RoundTripInfo}
import net.liftweb.json._
import net.liftweb.json.JsonDSL._

import java.util.concurrent.LinkedBlockingQueue
import java.util.Random

object WootServices {

  def render = S.session.map(javascript) openOr NodeSeq.Empty

  private def javascript(s: LiftSession) : NodeSeq = Script { 
    JsCrVar("wootServer", s buildRoundtrip services)
  }

  private def services = List[RoundTripInfo](
    "send" -> receive _,
    "init" -> init _)

  private def receive(jop: JValue): JValue = {
    println(jop)
    // TODO: Local integrate op
    BroadcastStream push jop
    JNull
  }

  // TODO: how to identify the document the user wants to work with?
  // TODO: how to signafy a new/unsaved document?
  private def init(config: JValue, onChange: RoundTripHandlerFunc) : Unit = {
    println("Loading woot model "+config)

    // TODO: get from model?
    val chars = JArray(Nil)
    val queue = JArray(Nil)
    val site = new Random().nextLong()
    val initClockValue = 1L

    // TODO: what siteId should be use? What clock value?
    val doc = ("chars" -> chars) ~ ("queue" -> queue) ~ ("site" -> site) ~ ("clockValue" -> initClockValue)
    onChange.send(doc)

    val q = BroadcastStream.q(site)
    println("Streaming from "+q.hashCode())
    Stream.continually(q.take()).foreach(v => onChange.send(v))
  }


  object BroadcastStream {

    // TODO: the Long is the site, but should it be additionally indexed by document - otherwise this is a single document system (currently)
    // TODO: how to clean up this map?
    private val qs = collection.mutable.Map[Long,LinkedBlockingQueue[JValue]]()

    def q(site: Long) = qs.getOrElseUpdate(site, new LinkedBlockingQueue[JValue])

    def push(v: JValue) = {
      qs.values.foreach(q => { println("Pushing onto "+q.hashCode()); q.add(v) }  )
    }
  }

}