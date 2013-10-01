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
    Broadcaster ! PushToQueue(jop)
    JNull
  }

  // TODO: how to identify the document the user wants to work with?
  // TODO: how to signify a new/unsaved document?
  private def init(config: JValue, onChange: RoundTripHandlerFunc) : Unit = {
    println("Loading WOOT model "+config)
   
    val site = S.session.map(_.uniqueId).getOrElse("SITE_ID_UNAVAILABLE")
    
    // TODO: what siteId should be use? What clock value?
    val initClockValue = 1    
    
    for { snapshot <- (Broadcaster !! GetModel()).asA[WString] } {
    	   val chars : JValue = snapshot.chars.map(toJson)
           val queue : JValue = snapshot.queue.map(toJson)
           val doc = ("chars" -> chars) ~ ("queue" -> queue) ~ ("site" -> site) ~ ("clockValue" -> initClockValue)
           onChange.send(doc)
    	 }  

    for  { queue <- (Broadcaster !! GetQueue(site)).asA[LinkedBlockingQueue[JValue]]
    } {
    	println("Streaming for site "+site)
    	Stream.continually(queue.take()).foreach(v => onChange.send(v))      
    }

  }

}