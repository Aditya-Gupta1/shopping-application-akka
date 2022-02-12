import AppConstants.{AddOrder, AddOrderToCustomer, CustomerDoesNotExists, DecreaseItemsFromInventory, GetItemFromInventory, GetProductCost, Order, OrderItem, OrderProcessingOutput, OrdersState}
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
    case AddOrder(customerEmail: String, orderItems: Set[OrderItem], refund: Boolean) =>
      if(Utils.isValidCustomer(customer, customerEmail)) {
        logger.info("Customer valid. Processing Order...")

        var allItemsCostValid: Boolean = true
        var allItemsExists: Boolean = true
        var insufficientQuantity: Boolean = false
        var processingMessage: String = ""
        var itemDetails: Map[String, List[Double]] = Map()

        breakable {
          for (item <- orderItems) {
            val itemQuantityAvailable: Int = Await.result(inventory ? GetItemFromInventory(item.productName),
              timeout.duration).asInstanceOf[Int]
            val doesExists: Boolean = itemQuantityAvailable != -1
            val itemPrice: Double = Await.result(productCost ? GetProductCost(item.productName),
              timeout.duration).asInstanceOf[Double]

            if (!doesExists) {
              logger.error(s"Product[${item.productName}] does not exists!")
              allItemsExists = false
              processingMessage = "One/more items product does not exists."
              break
            }
            else if (itemPrice == -1) {
              logger.error(s"Product[${item.productName}]'s price is not available!")
              allItemsCostValid = false
              processingMessage = "One or more item's cost invalid."
              break
            }
            else if(itemQuantityAvailable < item.quantity) {
              logger.error(s"Product[${item.productName}] has only $itemQuantityAvailable items left. " +
              s"Order Placed for ${item.quantity} items.")
              insufficientQuantity = true
              processingMessage = "Not enough quantity of items for one or more products."
              break
            }
            if (allItemsExists && allItemsCostValid && !insufficientQuantity) {
              itemDetails = itemDetails ++ Map(item.productName ->
                List(itemPrice, item.quantity, itemQuantityAvailable))
            }
          }
        }
        if(!allItemsExists || !allItemsCostValid || insufficientQuantity)
          {
            sender() ! OrderProcessingOutput(processed = false, processingMessage)
            logger.error(s"Order Processing Failed.")
          }
        else {
          val totalAmount: Double = Utils.calculateOrderTotal(itemDetails, inventory, refund)
          val orderNo: String = Utils.generateOrderNo
          val order: Order = Order(orderNo, orderItems, totalAmount, customerEmail)
          orders = orders ++ Map(orderNo -> order)

          customer ? AddOrderToCustomer(customerEmail, order)
          logger.info(s"Order Processed for customer[$customerEmail]: $order")
          sender() ! OrderProcessingOutput(processed = true)
        }
      }
      else {
        logger.error(s"Customer[$customerEmail] does not exists!")
        sender() ! CustomerDoesNotExists(s"Customer[$customerEmail] does not exists!")
      }
    case OrdersState => sender() ! orders
  }
}