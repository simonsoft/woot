package woots.snippet

import java.util.concurrent.LinkedBlockingQueue
import net.liftweb.common.Loggable
import net.liftweb.http.AddAListener
import net.liftweb.http.RemoveAListener
import net.liftweb.common.SimpleActor
import net.liftweb.actor.LiftActor
import woots.SiteId
import net.liftweb.json.JsonAST.JValue
import woots.JsonFormats.JOp
import net.liftweb.json.DefaultFormats
import woots.WString

trait Msg

case class AddSite(id: SiteId)
case class RemoveSite(id: SiteId)
case class GetQueue(id: SiteId)
case class PushToQueue(operation: JValue)
case class GetModel()

object Broadcaster extends LiftActor with Loggable {

  implicit val formats = DefaultFormats

  var model = WString()

  private var sites: List[SiteId] = Nil

  // TODO: the Int is the site, but should it be additionally indexed by document - otherwise this is a single document system (currently)
  // TODO: how to clean up this map?  
  private val qs = collection.mutable.Map[Int, LinkedBlockingQueue[JValue]]()

  def messageHandler: PartialFunction[Any, Unit] = {
    case AddSite(siteId) ⇒
      logger.info(s"Add site $siteId")
      sites ::= siteId
    case RemoveSite(siteId) ⇒
      logger.info(s"Remove site $siteId")
      sites = sites.filter(_ != siteId)
    case GetQueue(siteId) ⇒
      logger.info(s"Get Queue site $siteId")
      reply(qs.getOrElseUpdate(siteId, new LinkedBlockingQueue[JValue]))
    case PushToQueue(operation: JValue) ⇒
      logger.info("Push to Queue site")
      try {
        for (op ← operation.extractOpt[JOp].map(_.toOperation)) {
          model = model.integrate(op)
        }
      } catch {
        case x: Throwable ⇒ logger.error("Unable to integrate operation", x) // e.g., match/deserialization error?
      }
      qs.values.foreach(q ⇒ { println(s"Pushing onto ${ q.hashCode()}"); q.add(operation) })
    case GetModel() => reply(model)
    case otherwise ⇒ logger.error(s"Unknown msg $otherwise")
  }

}
  
 