package woots
package snippet

import java.util.concurrent.LinkedBlockingQueue
import net.liftweb.common.Loggable
import net.liftweb.actor.LiftActor
import net.liftweb.json.JsonAST.JValue
import net.liftweb.json.DefaultFormats

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

  // TODO: It be additionally indexed by document - otherwise this is a single document system (currently)
  private val qs = collection.mutable.Map[SiteId, LinkedBlockingQueue[JValue]]()

  def messageHandler: PartialFunction[Any, Unit] = {
    case AddSite(siteId) if sites contains siteId ⇒ logger.warn("Site already added")

    case AddSite(siteId) ⇒  sites ::= siteId
    	println(s" added $siteId to $sites")

    case RemoveSite(siteId) ⇒  sites = sites.filter(_ != siteId)
        println(s" removed $siteId from $sites")
    case GetQueue(siteId) ⇒ 
    reply(qs.getOrElseUpdate(siteId, new LinkedBlockingQueue[JValue]))
    case PushToQueue(operation: JValue) ⇒
      import JsonFormats.JOp

      try {
        for (op ← operation.extractOpt[JOp].map(_.toOperation)) {
          model = model.integrate(op)
        }
      } catch {
        case x: Throwable ⇒ logger.error("Unable to integrate operation", x) // e.g., match/deserialization error?
      }
      qs.values.foreach(q ⇒ { println(s"Pushing onto ${q.hashCode()}"); q.add(operation) })
    case GetModel() ⇒  reply(model)
    case otherwise ⇒ logger.error(s"Unknown msg $otherwise")
  }

}

