import AppConstants.{AddCustomer, AddOrder, AddToInventory, Customer, CustomerAdded, CustomerDoesNotExists, GetItemFromInventory, Order, OrderItem, OrderProcessingOutput, OrdersState, ProductAdded, ProductPriceUpdated, UpdateCost}
import akka.actor.{ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration.DurationInt

class OrdersSpec extends BasicTestSpec {
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val timeout: Timeout = Timeout(5.seconds)

  "An orders actor" must {
    "Add a valid order successfully" in {
      val inventoryTestActor: ActorRef = system.actorOf(Props[Inventory])
      val customerTestActor: ActorRef = system.actorOf(Props(Customers(inventoryTestActor)))
      val productCostTestActor: ActorRef = system.actorOf(Props(ProductCost(inventoryTestActor)))
      val ordersTestActor: ActorRef = system.actorOf(Props(Orders(customerTestActor,
        inventoryTestActor, productCostTestActor)))

      inventoryTestActor ! AddToInventory("Keyboard", 10)
      expectMsg(ProductAdded)
      inventoryTestActor ! AddToInventory("Mouse", 20)
      expectMsg(ProductAdded)
      inventoryTestActor ! AddToInventory("Chair", 5)
      expectMsg(ProductAdded)

      val testCustomer: Customer = Customer("Aditya Gupta",
        "address 123", "123456789", "aditya.gupta@gmail.com")
      customerTestActor ! AddCustomer(testCustomer)
      expectMsg(CustomerAdded)

      productCostTestActor ! UpdateCost("Keyboard", 100.0)
      expectMsg(ProductPriceUpdated)
      productCostTestActor ! UpdateCost("Mouse", 50.0)
      expectMsg(ProductPriceUpdated)
      productCostTestActor ! UpdateCost("Chair", 500.0)
      expectMsg(ProductPriceUpdated)

      val orderItems: Set[OrderItem] = Set(
        OrderItem("Keyboard", 5),
        OrderItem("Mouse", 10),
        OrderItem("Chair", 1)
      )
      ordersTestActor ! AddOrder(testCustomer.email, orderItems)
      expectMsg(OrderProcessingOutput(processed = true)) // Order Successful

      val orders: Map[String, Order] = Await.result(ordersTestActor ? OrdersState,
        timeout.duration).asInstanceOf[Map[String, Order]]
      assert(orders.valuesIterator.next.total == 1500.0
      ) // Total verified
    }

    "handle when the customer placing order is not valid" in {
      val inventoryTestActor: ActorRef = system.actorOf(Props[Inventory])
      val customerTestActor: ActorRef = system.actorOf(Props(Customers(inventoryTestActor)))
      val productCostTestActor: ActorRef = system.actorOf(Props(ProductCost(inventoryTestActor)))
      val ordersTestActor: ActorRef = system.actorOf(Props(Orders(customerTestActor,
        inventoryTestActor, productCostTestActor)))

      inventoryTestActor ! AddToInventory("Keyboard", 10)
      expectMsg(ProductAdded)
      inventoryTestActor ! AddToInventory("Mouse", 20)
      expectMsg(ProductAdded)
      inventoryTestActor ! AddToInventory("Chair", 5)
      expectMsg(ProductAdded)

      val testCustomer: Customer = Customer("Aditya Gupta",
        "address 123", "123456789", "aditya.gupta@gmail.com")
      customerTestActor ! AddCustomer(testCustomer)
      expectMsg(CustomerAdded)

      productCostTestActor ! UpdateCost("Keyboard", 100.0)
      expectMsg(ProductPriceUpdated)
      productCostTestActor ! UpdateCost("Mouse", 50.0)
      expectMsg(ProductPriceUpdated)
      productCostTestActor ! UpdateCost("Chair", 500.0)
      expectMsg(ProductPriceUpdated)

      val orderItems: Set[OrderItem] = Set(
        OrderItem("Keyboard", 5),
        OrderItem("Mouse", 10),
        OrderItem("Chair", 1)
      )
      val wrongCustomerEmail: String = "dummy@gmail.com"
      ordersTestActor ! AddOrder(wrongCustomerEmail, orderItems) // customer does not exists
      expectMsg(CustomerDoesNotExists(s"Customer[$wrongCustomerEmail] does not exists!"))
    }

    "handle when items in order is not in inventory" in {
      val inventoryTestActor: ActorRef = system.actorOf(Props[Inventory])
      val customerTestActor: ActorRef = system.actorOf(Props(Customers(inventoryTestActor)))
      val productCostTestActor: ActorRef = system.actorOf(Props(ProductCost(inventoryTestActor)))
      val ordersTestActor: ActorRef = system.actorOf(Props(Orders(customerTestActor,
        inventoryTestActor, productCostTestActor)))

      inventoryTestActor ! AddToInventory("Keyboard", 10)
      expectMsg(ProductAdded)
      inventoryTestActor ! AddToInventory("Mouse", 20)
      expectMsg(ProductAdded)
      inventoryTestActor ! AddToInventory("Chair", 5)
      expectMsg(ProductAdded)

      val testCustomer: Customer = Customer("Aditya Gupta",
        "address 123", "123456789", "aditya.gupta@gmail.com")
      customerTestActor ! AddCustomer(testCustomer)
      expectMsg(CustomerAdded)

      productCostTestActor ! UpdateCost("Keyboard", 100.0)
      expectMsg(ProductPriceUpdated)
      productCostTestActor ! UpdateCost("Mouse", 50.0)
      expectMsg(ProductPriceUpdated)
      productCostTestActor ! UpdateCost("Chair", 500.0)
      expectMsg(ProductPriceUpdated)

      val wrongOrderItems: Set[OrderItem] = Set(
        OrderItem("Table", 5),
        OrderItem("Mouse", 10),
        OrderItem("Chair", 1)
      )
      ordersTestActor ! AddOrder(testCustomer.email, wrongOrderItems)
      expectMsg(OrderProcessingOutput(processed = false, "One/more items product does not exists."))
    }

    "handle when items don't have a price" in {
      val inventoryTestActor: ActorRef = system.actorOf(Props[Inventory])
      val customerTestActor: ActorRef = system.actorOf(Props(Customers(inventoryTestActor)))
      val productCostTestActor: ActorRef = system.actorOf(Props(ProductCost(inventoryTestActor)))
      val ordersTestActor: ActorRef = system.actorOf(Props(Orders(customerTestActor,
        inventoryTestActor, productCostTestActor)))

      inventoryTestActor ! AddToInventory("Keyboard", 10)
      expectMsg(ProductAdded)
      inventoryTestActor ! AddToInventory("Mouse", 20)
      expectMsg(ProductAdded)
      inventoryTestActor ! AddToInventory("Chair", 5)
      expectMsg(ProductAdded)

      val testCustomer: Customer = Customer("Aditya Gupta",
        "address 123", "123456789", "aditya.gupta@gmail.com")
      customerTestActor ! AddCustomer(testCustomer)
      expectMsg(CustomerAdded)

      productCostTestActor ! UpdateCost("Keyboard", 100.0)
      expectMsg(ProductPriceUpdated)
      productCostTestActor ! UpdateCost("Mouse", 50.0)
      expectMsg(ProductPriceUpdated)
      productCostTestActor ! UpdateCost("Chair", 500.0)
      expectMsg(ProductPriceUpdated)

      val wrongOrderItems: Set[OrderItem] = Set(
        OrderItem("Table", 5),
        OrderItem("Mouse", 10),
        OrderItem("Chair", 1)
      )
      inventoryTestActor ! AddToInventory("Table", 7)
      expectMsg(ProductAdded)
      ordersTestActor ! AddOrder(testCustomer.email, wrongOrderItems)
      expectMsg(OrderProcessingOutput(processed = false, "One or more item's cost invalid."))
    }

    "handler if not enough quantity of items are there in inventory" in {
      val inventoryTestActor: ActorRef = system.actorOf(Props[Inventory])
      val customerTestActor: ActorRef = system.actorOf(Props(Customers(inventoryTestActor)))
      val productCostTestActor: ActorRef = system.actorOf(Props(ProductCost(inventoryTestActor)))
      val ordersTestActor: ActorRef = system.actorOf(Props(Orders(customerTestActor,
        inventoryTestActor, productCostTestActor)))

      inventoryTestActor ! AddToInventory("Keyboard", 10)
      expectMsg(ProductAdded)
      inventoryTestActor ! AddToInventory("Mouse", 20)
      expectMsg(ProductAdded)
      inventoryTestActor ! AddToInventory("Chair", 5)
      expectMsg(ProductAdded)

      val testCustomer: Customer = Customer("Aditya Gupta",
        "address 123", "123456789", "aditya.gupta@gmail.com")
      customerTestActor ! AddCustomer(testCustomer)
      expectMsg(CustomerAdded)

      productCostTestActor ! UpdateCost("Keyboard", 100.0)
      expectMsg(ProductPriceUpdated)
      productCostTestActor ! UpdateCost("Mouse", 50.0)
      expectMsg(ProductPriceUpdated)
      productCostTestActor ! UpdateCost("Chair", 500.0)
      expectMsg(ProductPriceUpdated)

      val orderItems: Set[OrderItem] = Set(
        OrderItem("Keyboard", 5),
        OrderItem("Mouse", 10),
        OrderItem("Chair", 10)
      )
      ordersTestActor ! AddOrder(testCustomer.email, orderItems)
      expectMsg(OrderProcessingOutput(processed = false,
        "Not enough quantity of items for one or more products."))
    }

    "deduct items in inventory if order is valid" in {
      val inventoryTestActor: ActorRef = system.actorOf(Props[Inventory])
      val customerTestActor: ActorRef = system.actorOf(Props(Customers(inventoryTestActor)))
      val productCostTestActor: ActorRef = system.actorOf(Props(ProductCost(inventoryTestActor)))
      val ordersTestActor: ActorRef = system.actorOf(Props(Orders(customerTestActor,
        inventoryTestActor, productCostTestActor)))

      inventoryTestActor ! AddToInventory("Keyboard", 10)
      expectMsg(ProductAdded)
      inventoryTestActor ! AddToInventory("Mouse", 20)
      expectMsg(ProductAdded)
      inventoryTestActor ! AddToInventory("Chair", 5)
      expectMsg(ProductAdded)

      val testCustomer: Customer = Customer("Aditya Gupta",
        "address 123", "123456789", "aditya.gupta@gmail.com")
      customerTestActor ! AddCustomer(testCustomer)
      expectMsg(CustomerAdded)

      productCostTestActor ! UpdateCost("Keyboard", 100.0)
      expectMsg(ProductPriceUpdated)
      productCostTestActor ! UpdateCost("Mouse", 50.0)
      expectMsg(ProductPriceUpdated)
      productCostTestActor ! UpdateCost("Chair", 500.0)
      expectMsg(ProductPriceUpdated)

      val orderItems: Set[OrderItem] = Set(
        OrderItem("Keyboard", 5),
        OrderItem("Mouse", 10),
        OrderItem("Chair", 1)
      )
      ordersTestActor ! AddOrder(testCustomer.email, orderItems)
      expectMsg(OrderProcessingOutput(processed = true)) // Order Successful

      val orders: Map[String, Order] = Await.result(ordersTestActor ? OrdersState,
        timeout.duration).asInstanceOf[Map[String, Order]]
      assert(orders.valuesIterator.next.total == 1500.0
      ) // Total verified

      inventoryTestActor ! GetItemFromInventory("Keyboard")
      expectMsg(5)
      inventoryTestActor ! GetItemFromInventory("Mouse")
      expectMsg(10)
      inventoryTestActor ! GetItemFromInventory("Chair")
      expectMsg(4)
    }

    "not deduct items from inventory if invalid order" in {
      val inventoryTestActor: ActorRef = system.actorOf(Props[Inventory])
      val customerTestActor: ActorRef = system.actorOf(Props(Customers(inventoryTestActor)))
      val productCostTestActor: ActorRef = system.actorOf(Props(ProductCost(inventoryTestActor)))
      val ordersTestActor: ActorRef = system.actorOf(Props(Orders(customerTestActor,
        inventoryTestActor, productCostTestActor)))

      inventoryTestActor ! AddToInventory("Keyboard", 10)
      expectMsg(ProductAdded)
      inventoryTestActor ! AddToInventory("Mouse", 20)
      expectMsg(ProductAdded)
      inventoryTestActor ! AddToInventory("Chair", 5)
      expectMsg(ProductAdded)

      val testCustomer: Customer = Customer("Aditya Gupta",
        "address 123", "123456789", "aditya.gupta@gmail.com")
      customerTestActor ! AddCustomer(testCustomer)
      expectMsg(CustomerAdded)

      productCostTestActor ! UpdateCost("Keyboard", 100.0)
      expectMsg(ProductPriceUpdated)
      productCostTestActor ! UpdateCost("Mouse", 50.0)
      expectMsg(ProductPriceUpdated)
      productCostTestActor ! UpdateCost("Chair", 500.0)
      expectMsg(ProductPriceUpdated)

      val wrongOrderItems: Set[OrderItem] = Set(
        OrderItem("Table", 5),
        OrderItem("Mouse", 10),
        OrderItem("Chair", 1)
      )
      ordersTestActor ! AddOrder(testCustomer.email, wrongOrderItems)
      expectMsg(OrderProcessingOutput(processed = false, "One/more items product does not exists."))

      inventoryTestActor ! GetItemFromInventory("Keyboard")
      expectMsg(10)
      inventoryTestActor ! GetItemFromInventory("Mouse")
      expectMsg(20)
      inventoryTestActor ! GetItemFromInventory("Chair")
      expectMsg(5)
    }
  }
}
