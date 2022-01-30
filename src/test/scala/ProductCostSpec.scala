import AppConstants.{AddToInventory, ProductAdded, ProductCostState, ProductDoesNotExists, ProductPriceUpdated, UpdateCost}
import akka.actor.{ActorRef, Props}
import akka.util.Timeout

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

class ProductCostSpec extends BasicTestSpec {
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val timeout: Timeout = Timeout(5.seconds)

  "A ProductCost actor" must {
    "update cost of the given product" in {
      val inventoryTestActor: ActorRef = system.actorOf(Props[Inventory])
      val productCostTestActor: ActorRef = system.actorOf(Props(ProductCost(inventoryTestActor)))

      inventoryTestActor ! AddToInventory("Keyboard", 10)
      expectMsg(ProductAdded)
      inventoryTestActor ! AddToInventory("Mouse", 20)
      expectMsg(ProductAdded)
      inventoryTestActor ! AddToInventory("Chair", 5)
      expectMsg(ProductAdded)

      productCostTestActor ! UpdateCost("Keyboard", 5)
      expectMsg(ProductPriceUpdated)

      productCostTestActor ! ProductCostState
      expectMsg(Map("Keyboard" -> 5.0))
    }

    "handle when the product for cost updation isn't in inventory" in {
      val inventoryTestActor: ActorRef = system.actorOf(Props[Inventory])
      val productCostTestActor: ActorRef = system.actorOf(Props(ProductCost(inventoryTestActor)))

      inventoryTestActor ! AddToInventory("Keyboard", 10)
      expectMsg(ProductAdded)
      inventoryTestActor ! AddToInventory("Mouse", 20)
      expectMsg(ProductAdded)
      inventoryTestActor ! AddToInventory("Chair", 5)
      expectMsg(ProductAdded)

      productCostTestActor ! UpdateCost("Table", 5)
      expectMsg(ProductDoesNotExists)
    }
  }
}
