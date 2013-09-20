package woots
package snippet

import scala.xml.NodeSeq
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.{S,LiftSession}
import net.liftweb.http.RoundTripInfo
import net.liftweb.json._

object WootServices {

  def render = S.session.map(javascript) openOr NodeSeq.Empty

  private def javascript(s: LiftSession) : NodeSeq = Script { 
    JsCrVar("wootServer", s buildRoundtrip serices)
  }

  private def serices = List[RoundTripInfo]("send" -> receive _)

  private def receive(jop: JValue): JValue = {
    println(s"Yes?")
    JNull
  }
 
}