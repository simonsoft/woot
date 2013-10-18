package spiralarm
package woot
package snippet

import java.util.concurrent.LinkedBlockingQueue
import net.liftweb.common.Loggable
import net.liftweb.actor.LiftActor
import net.liftweb.json.JsonAST.JValue
import net.liftweb.json.DefaultFormats

case class AddSite(id: SiteId)
case class RemoveSite(id: SiteId)
case class PushToQueue(operation: JValue)
case class Setup(model: WString, queue: LinkedBlockingQueue[JValue])

object Broadcaster extends LiftActor with Loggable {

  implicit val formats = DefaultFormats

  var model = WString()

  // TODO: additionally indexed by document (currently we are a single document system)
  private val qs = collection.mutable.Map[SiteId, LinkedBlockingQueue[JValue]]()

  def messageHandler: PartialFunction[Any, Unit] = {

    case AddSite(siteId) if qs.keySet contains siteId ⇒
      logger.warn(s"Site $siteId already added")
      reply {
        Setup(model, qs(siteId))
      }

    case AddSite(siteId) ⇒
      logger.info(s"Adding $siteId")
      val q = qs.getOrElseUpdate(siteId, new LinkedBlockingQueue[JValue])
      reply {
        Setup(model, q)
      }

    case RemoveSite(siteId) ⇒
        logger.info(s" removed $siteId")
        qs -= siteId

    case PushToQueue(operation: JValue) ⇒
      import JsonFormats.JOp

      try {
        for (op ← operation.extractOpt[JOp].map(_.toOperation)) {
          model = model.integrate(op)

          qs.par.filterKeys(_ != op.from).collect{
            case (s,q) ⇒
              logger.debug(s"${op.wchar.alpha} ${op.name} -> site starting: ${s.take(5)} queue: ${q.hashCode()}")
              q.add(operation)
          }
        }
      } catch {
        case x: Throwable ⇒ logger.error(s"Unable to integrate: $operation", x) // e.g., match/deserialization error
      }

    case otherwise ⇒ logger.error(s"Unknown msg $otherwise")
  }

}

