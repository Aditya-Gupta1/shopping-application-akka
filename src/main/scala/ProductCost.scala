import AppConstants.{DoesExistsInInventory, GetProductCost, ProductCostState, ProductDoesNotExists, ProductPriceUpdated, UpdateCost}
import akka.actor.{Actor, ActorRef}
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{Await, ExecutionContext}

case class ProductCost(inventory: ActorRef)
                      (implicit val executionContext: ExecutionContext,
                       implicit val timeout: Timeout) extends Actor with LazyLogging {

  var productPrices: Map[String, Double] = Map()

  override def receive: Receive = {
    case UpdateCost(product: String, cost: Double) =>
      val exists = Await.result(inventory ? DoesExistsInInventory(product),
        timeout.duration).asInstanceOf[Boolean]

      if (exists) {
        productPrices = productPrices ++ Map(product -> cost)
        logger.info(s"Product[$product] with price[$cost] saved.")
        sender() ! ProductPriceUpdated
      } else {
        logger.error(s"Product[$product] does not exists.")
        sender() ! ProductDoesNotExists
      }
    case GetProductCost(product: String) => sender() ! productPrices.getOrElse(product, -1.0)
    case ProductCostState => sender() ! productPrices
  }
}
