import AppConstants.{AddToInventory, DecreaseItemsFromInventory, DeleteProduct, DoesExistsInInventory, GetItemFromInventory, InventoryState, IsEmpty}
import akka.actor.{ActorRef, Props}

class InventorySpec extends BasicTestSpec {

  "An Inventory actor" must {

    "return the inventory state" in {
      val inventoryTestActor: ActorRef = system.actorOf(Props[Inventory])

      val testInventory: Map[String, Int] = Map("Keyboard" -> 10, "Mouse" -> 20, "Chair" -> 5)
      inventoryTestActor ! AddToInventory("Keyboard", 10)
      inventoryTestActor ! AddToInventory("Mouse", 20)
      inventoryTestActor ! AddToInventory("Chair", 5)

      inventoryTestActor ! InventoryState
      expectMsg(testInventory)
    }

    "add products to the inventory" in {
      val inventoryTestActor: ActorRef = system.actorOf(Props[Inventory])

      val testInventory: Map[String, Int] = Map("Keyboard" -> 10, "Mouse" -> 20, "Chair" -> 5)
      inventoryTestActor ! AddToInventory("Keyboard", 10)
      inventoryTestActor ! AddToInventory("Mouse", 20)
      inventoryTestActor ! AddToInventory("Chair", 5)

      inventoryTestActor ! InventoryState
      expectMsg(testInventory)
    }

    "check if a product is empty in the inventory" in {
      val inventoryTestActor: ActorRef = system.actorOf(Props[Inventory])

      inventoryTestActor ! AddToInventory("Keyboard", 10)
      inventoryTestActor ! AddToInventory("Mouse", 20)
      inventoryTestActor ! AddToInventory("Chair", 5)

      inventoryTestActor ! IsEmpty("Keyboard")
      expectMsg(false)

      inventoryTestActor ! IsEmpty("Table")
      expectMsg(true)
    }

    "get the count of items of the product in the inventory" in {
      val inventoryTestActor: ActorRef = system.actorOf(Props[Inventory])

      inventoryTestActor ! AddToInventory("Keyboard", 10)
      inventoryTestActor ! AddToInventory("Mouse", 20)
      inventoryTestActor ! AddToInventory("Chair", 5)

      inventoryTestActor ! GetItemFromInventory("Keyboard")
      expectMsg(10)

      inventoryTestActor ! GetItemFromInventory("Table")
      expectMsg(-1)
    }

    "delete product from the inventory" in {
      val inventoryTestActor: ActorRef = system.actorOf(Props[Inventory])
      val testInventory: Map[String, Int] = Map("Mouse" -> 20, "Chair" -> 5)
      inventoryTestActor ! AddToInventory("Keyboard", 10)
      inventoryTestActor ! AddToInventory("Mouse", 20)
      inventoryTestActor ! AddToInventory("Chair", 5)

      inventoryTestActor ! DeleteProduct("Keyboard")
      inventoryTestActor ! InventoryState
      expectMsg(testInventory)
    }

    "decrease product items as specified" in {
      val inventoryTestActor: ActorRef = system.actorOf(Props[Inventory])
      inventoryTestActor ! AddToInventory("Keyboard", 10)
      inventoryTestActor ! AddToInventory("Mouse", 20)
      inventoryTestActor ! AddToInventory("Chair", 5)

      inventoryTestActor ! DecreaseItemsFromInventory("Keyboard", 5)
      inventoryTestActor ! GetItemFromInventory("Keyboard")
      expectMsg(5)

      inventoryTestActor ! DecreaseItemsFromInventory("Keyboard", 10)
      inventoryTestActor ! GetItemFromInventory("Keyboard")
      expectMsg(5)
    }

    "check if a product is present in inventory" in {
      val inventoryTestActor: ActorRef = system.actorOf(Props[Inventory])
      inventoryTestActor ! AddToInventory("Keyboard", 10)
      inventoryTestActor ! AddToInventory("Mouse", 20)
      inventoryTestActor ! AddToInventory("Chair", 5)

      inventoryTestActor ! DoesExistsInInventory("Keyboard")
      expectMsg(true)

      inventoryTestActor ! DoesExistsInInventory("Headphones")
      expectMsg(false)
    }
  }

}
