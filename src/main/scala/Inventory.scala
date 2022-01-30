import AppConstants.{AddToInventory, DecreaseItemsFromInventory, DeleteProduct, DoesExistsInInventory, GetItemFromInventory, InventoryState, IsEmpty, NotEnoughProductQuantity, ProductAdded, ProductDeleted, ProductQuantityDecreased}
import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging

class Inventory extends Actor with LazyLogging {

  var inventory: Map[String, Int] = Map()

  override def receive: Receive = {
    case IsEmpty(product: String) =>
      sender() ! !(inventory.contains(product) && inventory.getOrElse(product, 0) != 0)

    case GetItemFromInventory(product: String) =>
      sender() ! inventory.getOrElse(product, -1)

    case DeleteProduct(product: String) =>
      inventory -= product
      logger.info(s"Product Deleted: $product")
      sender() ! ProductDeleted

    case AddToInventory(product: String, quantity: Int) =>
      val currentQuantity = inventory.getOrElse(product, 0)
      inventory = inventory ++ Map(product -> (currentQuantity + quantity))
      logger.info(s"Product[$product] added to inventory: $quantity")
      sender() ! ProductAdded

    case DecreaseItemsFromInventory(product: String, quantity: Int) =>
      val currentQuantity = inventory(product)
      if (currentQuantity < quantity) {
        logger.error(s"Cannot delete $quantity items of product [$product]."
          + s" Current items: $currentQuantity")
        sender() ! NotEnoughProductQuantity
      }
      else {
        inventory = inventory ++ Map(product -> (currentQuantity - quantity))
        logger.info(s"$quantity items of product [$product] deleted." +
        s" Current items: ${currentQuantity - quantity}")
        sender() ! ProductQuantityDecreased
      }

    case DoesExistsInInventory(product: String) =>
      sender() ! inventory.contains(product)

    case InventoryState => sender() ! inventory
  }
}
