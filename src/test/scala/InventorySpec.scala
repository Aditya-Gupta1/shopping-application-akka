import AppConstants.{AddToInventory, DecreaseItemsFromInventory, DeleteProduct, DoesExistsInInventory, GetItemFromInventory, InventoryState, IsEmpty, NotEnoughProductQuantity, ProductAdded, ProductDeleted, ProductQuantityDecreased}
import akka.actor.{ActorRef, Props}

class InventorySpec extends BasicTestSpec {

  "An Inventory actor" must {

    "return the inventory state" in {
      val inventoryTestActor: ActorRef = system.actorOf(Props[Inventory])

      val testInventory: Map[String, Int] = Map("Keyboard" -> 10, "Mouse" -> 20, "Chair" -> 5)
      inventoryTestActor ! AddToInventory("Keyboard", 10)
      expectMsg(ProductAdded)
      inventoryTestActor ! AddToInventory("Mouse", 20)
      expectMsg(ProductAdded)
      inventoryTestActor ! AddToInventory("Chair", 5)
      expectMsg(ProductAdded)

      inventoryTestActor ! InventoryState
      expectMsg(testInventory)
    }

    "add products to the inventory" in {
      val inventoryTestActor: ActorRef = system.actorOf(Props[Inventory])

      val testInventory: Map[String, Int] = Map("Keyboard" -> 10, "Mouse" -> 20, "Chair" -> 5)
      inventoryTestActor ! AddToInventory("Keyboard", 10)
      expectMsg(ProductAdded)
      inventoryTestActor ! AddToInventory("Mouse", 20)
      expectMsg(ProductAdded)
      inventoryTestActor ! AddToInventory("Chair", 5)
      expectMsg(ProductAdded)
    }

    "check if a product is empty in the inventory" in {
      val inventoryTestActor: ActorRef = system.actorOf(Props[Inventory])

      inventoryTestActor ! AddToInventory("Keyboard", 10)
      expectMsg(ProductAdded)
      inventoryTestActor ! AddToInventory("Mouse", 20)
      expectMsg(ProductAdded)
      inventoryTestActor ! AddToInventory("Chair", 5)
      expectMsg(ProductAdded)

      inventoryTestActor ! IsEmpty("Keyboard")
      expectMsg(false)

      inventoryTestActor ! IsEmpty("Table")
      expectMsg(true)
    }

    "get the count of items of the product in the inventory" in {
      val inventoryTestActor: ActorRef = system.actorOf(Props[Inventory])

      inventoryTestActor ! AddToInventory("Keyboard", 10)
      expectMsg(ProductAdded)
      inventoryTestActor ! AddToInventory("Mouse", 20)
      expectMsg(ProductAdded)
      inventoryTestActor ! AddToInventory("Chair", 5)
      expectMsg(ProductAdded)

      inventoryTestActor ! GetItemFromInventory("Keyboard")
      expectMsg(10)

      inventoryTestActor ! GetItemFromInventory("Table")
      expectMsg(-1)
    }

    "delete product from the inventory" in {
      val inventoryTestActor: ActorRef = system.actorOf(Props[Inventory])
      val testInventory: Map[String, Int] = Map("Mouse" -> 20, "Chair" -> 5)
      inventoryTestActor ! AddToInventory("Keyboard", 10)
      expectMsg(ProductAdded)
      inventoryTestActor ! AddToInventory("Mouse", 20)
      expectMsg(ProductAdded)
      inventoryTestActor ! AddToInventory("Chair", 5)
      expectMsg(ProductAdded)

      inventoryTestActor ! DeleteProduct("Keyboard")
      expectMsg(ProductDeleted)

      inventoryTestActor ! InventoryState
      expectMsg(testInventory)
    }

    "decrease product items as specified" in {
      val inventoryTestActor: ActorRef = system.actorOf(Props[Inventory])
      inventoryTestActor ! AddToInventory("Keyboard", 10)
      expectMsg(ProductAdded)
      inventoryTestActor ! AddToInventory("Mouse", 20)
      expectMsg(ProductAdded)
      inventoryTestActor ! AddToInventory("Chair", 5)
      expectMsg(ProductAdded)

      inventoryTestActor ! DecreaseItemsFromInventory("Keyboard", 5)
      expectMsg(ProductQuantityDecreased)

      inventoryTestActor ! GetItemFromInventory("Keyboard")
      expectMsg(5)

      inventoryTestActor ! DecreaseItemsFromInventory("Keyboard", 10)
      expectMsg(NotEnoughProductQuantity)
    }

    "check if a product is present in inventory" in {
      val inventoryTestActor: ActorRef = system.actorOf(Props[Inventory])
      inventoryTestActor ! AddToInventory("Keyboard", 10)
      expectMsg(ProductAdded)
      inventoryTestActor ! AddToInventory("Mouse", 20)
      expectMsg(ProductAdded)
      inventoryTestActor ! AddToInventory("Chair", 5)
      expectMsg(ProductAdded)

      inventoryTestActor ! DoesExistsInInventory("Keyboard")
      expectMsg(true)

      inventoryTestActor ! DoesExistsInInventory("Headphones")
      expectMsg(false)
    }
  }

}
