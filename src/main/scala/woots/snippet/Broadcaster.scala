package woots
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

  private var sites: List[SiteId] = Nil

  // TODO: It be additionally indexed by document - otherwise this is a single document system (currently)
  private val qs = collection.mutable.Map[SiteId, LinkedBlockingQueue[JValue]]()

  def messageHandler: PartialFunction[Any, Unit] = {

    case AddSite(siteId) if sites contains siteId ⇒
      logger.warn(s"Site $siteId already added")
      reply {
        Setup(model, qs(siteId))
      }

    case AddSite(siteId) ⇒
      logger.info(s"Adding $siteId")
      sites ::= siteId
      val q = qs.getOrElseUpdate(siteId, new LinkedBlockingQueue[JValue])
      reply {
        Setup(model, q)
      }

    case RemoveSite(siteId) ⇒
        logger.info(s" removed $siteId from $sites and $qs")    
        sites = sites.filter(_ != siteId)
        qs -= siteId

    case PushToQueue(operation: JValue) ⇒
      import JsonFormats.JOp

      try {
        for (op ← operation.extractOpt[JOp].map(_.toOperation)) {
          model = model.integrate(op)

          //qs.filterKeys(_ != op.from).values.foreach(q ⇒ { println(s"Pushing onto ${q.hashCode()}"); q.add(operation) })

          qs.filterKeys(_ != op.from).collect{
            case (s,q) ⇒
              logger.info(s"${op.wchar.alpha} ${op.getClass().toString}-> site starting: ${s.take(5)} queue: ${q.hashCode()}")
              q.add(operation)
          }

        }
      } catch {
        case x: Throwable ⇒ logger.error("Unable to integrate operation", x) // e.g., match/deserialization error?
      }

    case otherwise ⇒ logger.error(s"Unknown msg $otherwise")
  }

}

