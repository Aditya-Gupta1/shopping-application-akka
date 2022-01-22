import AppConstants.{AddOrder, CustomerDoesNotExists, CustomerExists, GetItemFromInventory, GetProductCost, IsOrderProcessed, Order, OrderItem, OrdersState}
import akka.actor.{Actor, ActorRef}
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{Await, ExecutionContext}
import scala.util.control.Breaks._

case class Orders(customer: ActorRef, inventory: ActorRef,
                  productCost: ActorRef)
                 (implicit val executionContext: ExecutionContext,
                  implicit val timeout: Timeout)
  extends Actor with LazyLogging {

  var orders: Map[String, Order] = Map()

  override def receive: Receive = {
    case AddOrder(customerEmail: String, orderItems: Set[OrderItem]) =>
      val validCustomer: Boolean = Await.result(customer ? CustomerExists(customerEmail),
        timeout.duration).asInstanceOf[Boolean]

      if(validCustomer) {
        logger.info("Customer valid. Processing Order...")
        val orderNo: String = scala.util.Random.nextInt(1000000000).toString
        var totalAmount: Double = 0
        var allItemsCostValid: Boolean = true
        var allItemsExists: Boolean = true

        // TODO-1: Check if the quantity in order is >= quantity in inventory
        breakable {
          for (item <- orderItems) {
            val doesExists: Boolean = Await.result(inventory ? GetItemFromInventory(item.productName),
              timeout.duration).asInstanceOf[Int] != -1
            val itemPrice: Double = Await.result(productCost ? GetProductCost(item.productName),
              timeout.duration).asInstanceOf[Double]

            if (!doesExists) {
              logger.error(s"Product[${item.productName}] does not exists!")
              allItemsExists = false
              break
            }
            else if (itemPrice == -1) {
              logger.error(s"Product[${item.productName}]'s price is not available!")
              allItemsCostValid = false
              break
            }
            if (allItemsExists && allItemsCostValid)
              totalAmount += (itemPrice * item.quantity)
          }
        }
        if(!allItemsExists)
          sender() ! IsOrderProcessed(processed = false, "One/more items product does not exists.")
        else if(!allItemsCostValid)
          sender() ! IsOrderProcessed(processed = false, "One or more item's cost invalid.")
        else {
          val order: Order = Order(orderNo, orderItems, totalAmount, customerEmail)
          orders = orders ++ Map(orderNo -> order)
          // TODO-2: Add order to the customer object/map.
          logger.info(s"Order Processed for customer[$customerEmail]: $order")
          sender() ! IsOrderProcessed(processed = true)
        }
      }
      else {
        logger.error(s"Customer[$customerEmail] does not exists!")
        sender() ! CustomerDoesNotExists(s"Customer[$customerEmail] does not exists!")
      }
    case OrdersState => sender() ! orders
  }
}