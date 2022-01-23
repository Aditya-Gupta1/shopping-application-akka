import AppConstants.CustomerExists
import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await

object Utils {

  def isValidCustomer(customerActor: ActorRef, customerEmail: String)
                     (implicit timeout: Timeout): Boolean =
    Await.result(customerActor ? CustomerExists(customerEmail),
      timeout.duration).asInstanceOf[Boolean]
}
