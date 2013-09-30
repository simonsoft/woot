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

  implicit val formats = DefaultFormats
  
  def render = S.session.map(javascript) openOr NodeSeq.Empty

  private def javascript(s: LiftSession) : NodeSeq = Script { 
    JsCrVar("wootServer", s buildRoundtrip services)
  }

  private def services = List[RoundTripInfo](
    "send" -> receive _,
    "init" -> init _)

  import JsonFormats._


  private def receive(jop: JValue): JValue = {
    println("Received:"+jop)
    BroadcastStream push jop
    JNull
  }

  // TODO: how to identify the document the user wants to work with?
  // TODO: how to signify a new/unsaved document?
  private def init(config: JValue, onChange: RoundTripHandlerFunc) : Unit = {
    println("Loading WOOT model "+config)
   
    val snapshot = BroadcastStream.model
    println("Current model is:"+snapshot.text)
    
    val chars : JValue = snapshot.chars.map(toJson)
    val queue : JValue = snapshot.queue.map(toJson)

    // TODO: what siteId should be use? What clock value?
    val site = new Random().nextInt()
    val initClockValue = 1

    val doc = ("chars" -> chars) ~ ("queue" -> queue) ~ ("site" -> site) ~ ("clockValue" -> initClockValue)
    onChange.send(doc)

    val q = BroadcastStream.q(site)
    println("Streaming for site "+site)
    Stream.continually(q.take()).foreach(v => onChange.send(v))
  }


  object BroadcastStream {

    var model = WString()

    // TODO: the Long is the site, but should it be additionally indexed by document - otherwise this is a single document system (currently)
    // TODO: how to clean up this map?
    private val qs = collection.mutable.Map[Int,LinkedBlockingQueue[JValue]]()

    def q(site: Int) = qs.getOrElseUpdate(site, new LinkedBlockingQueue[JValue])

    def push(v: JValue) = {


      try {
        for(op <- v.extractOpt[JOp].map(_.toOperation)) {
          println("Integrating "+op)
          model = model.integrate(op)
          println(model)
          println("Model: "+model.text)
        }
      } catch {
        case x : Throwable => println(x) // e.g., match/deserialization error?
     }

      qs.values.foreach(q => { println("Pushing onto "+q.hashCode()); q.add(v) }  )
    }
  }

}