import AppConstants.{CustomerExists, DecreaseItemsFromInventory}
import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await

object Utils {

  def isValidCustomer(customerActor: ActorRef, customerEmail: String)
                     (implicit timeout: Timeout): Boolean =
    Await.result(customerActor ? CustomerExists(customerEmail),
      timeout.duration).asInstanceOf[Boolean]

  def generateOrderNo: String = scala.util.Random.nextInt(1000000000).toString

  def calculateOrderTotal(itemDetails: Map[String, List[Double]],
                          inventory: ActorRef)
                         (implicit timeout: Timeout): Double = {
    var totalAmount: Double = 0
    for (item <- itemDetails.keysIterator) {
      totalAmount += (itemDetails(item).head * itemDetails(item)(1))
      inventory ? DecreaseItemsFromInventory(item, itemDetails(item)(1).toInt)
    }
    totalAmount
  }
}
