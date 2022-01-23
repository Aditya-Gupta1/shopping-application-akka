import AppConstants.{AddCustomer, AddOrderToCustomer, Customer, CustomerDoesNotExists, CustomerEmailAlreadyExists, CustomerExists, CustomerState, DeleteCustomer, GetCustomerDetails, Order, OrderItem, UpdateCustomerDetails}
import akka.actor.{ActorRef, Props}

class CustomersSpec extends BasicTestSpec {

  "A customers actor" must {

    "add a new customer" in {
      val inventoryTestActor: ActorRef = system.actorOf(Props[Inventory])
      val customerTestActor: ActorRef = system.actorOf(Props(Customers(inventoryTestActor)))
      val testCustomer: Customer = Customer("Aditya Gupta",
        "address 123", "123456789", "aditya.gupta@gmail.com")

      customerTestActor ! AddCustomer(testCustomer)

      customerTestActor ! CustomerState
      expectMsg(Map(testCustomer.email -> testCustomer))
    }

    "check for existing customer when adding a new customer" in {
      val inventoryTestActor: ActorRef = system.actorOf(Props[Inventory])
      val customerTestActor: ActorRef = system.actorOf(Props(Customers(inventoryTestActor)))
      val testCustomer: Customer = Customer("Aditya Gupta",
        "address 123", "123456789", "aditya.gupta@gmail.com")

      customerTestActor ! AddCustomer(testCustomer)
      customerTestActor ! AddCustomer(testCustomer)
      expectMsg(CustomerEmailAlreadyExists(s"Email[${testCustomer.email}] already exists!"))
    }

    "get customers state" in {
      val inventoryTestActor: ActorRef = system.actorOf(Props[Inventory])
      val customerTestActor: ActorRef = system.actorOf(Props(Customers(inventoryTestActor)))
      val testCustomer: Customer = Customer("Aditya Gupta",
        "address 123", "123456789", "aditya.gupta@gmail.com")

      customerTestActor ! AddCustomer(testCustomer)
      customerTestActor ! CustomerState
      expectMsg(Map(testCustomer.email -> testCustomer))
    }

    "delete a customer" in {
      val inventoryTestActor: ActorRef = system.actorOf(Props[Inventory])
      val customerTestActor: ActorRef = system.actorOf(Props(Customers(inventoryTestActor)))
      val testCustomer: Customer = Customer("Aditya Gupta",
        "address 123", "123456789", "aditya.gupta@gmail.com")

      customerTestActor ! AddCustomer(testCustomer)
      customerTestActor ! CustomerState
      expectMsg(Map(testCustomer.email -> testCustomer))

      customerTestActor ! DeleteCustomer(testCustomer)
      customerTestActor ! CustomerState
      expectMsg(Map())
    }

    "handle when deleting a customer which does not exists" in {
      val inventoryTestActor: ActorRef = system.actorOf(Props[Inventory])
      val customerTestActor: ActorRef = system.actorOf(Props(Customers(inventoryTestActor)))
      val testCustomer: Customer = Customer("Aditya Gupta",
        "address 123", "123456789", "aditya.gupta@gmail.com")

      customerTestActor ! AddCustomer(testCustomer)
      customerTestActor ! CustomerState
      expectMsg(Map(testCustomer.email -> testCustomer))

      customerTestActor ! DeleteCustomer(testCustomer)
      customerTestActor ! CustomerState
      expectMsg(Map())

      customerTestActor ! DeleteCustomer(testCustomer)
      expectMsg(CustomerDoesNotExists(s"Customer[$testCustomer] does not exists!"))
    }

    "update customer details" in {
      val inventoryTestActor: ActorRef = system.actorOf(Props[Inventory])
      val customerTestActor: ActorRef = system.actorOf(Props(Customers(inventoryTestActor)))
      val testCustomer: Customer = Customer("Aditya Gupta",
        "address 123", "123456789", "aditya.gupta@gmail.com")
      val testModifiedCustomer: Customer = Customer("Aditya Gupta", "new address", "123456789", "aditya.gupta@gmail.com")

      customerTestActor ! AddCustomer(testCustomer)
      customerTestActor ! UpdateCustomerDetails(testCustomer.email, testModifiedCustomer)
      customerTestActor ! CustomerState

      val testCustomers = Map(testCustomer.email -> testModifiedCustomer)
      expectMsg(testCustomers)
    }

    "checks for an existing customer" in {
      val inventoryTestActor: ActorRef = system.actorOf(Props[Inventory])
      val customerTestActor: ActorRef = system.actorOf(Props(Customers(inventoryTestActor)))
      val testCustomer: Customer = Customer("Aditya Gupta",
        "address 123", "123456789", "aditya.gupta@gmail.com")

      customerTestActor ! AddCustomer(testCustomer)
      customerTestActor ! CustomerExists(testCustomer.email)
      expectMsg(true)

      customerTestActor ! CustomerExists("random@gmail.com")
      expectMsg(false)
    }

    "get customer details" in {
      val inventoryTestActor: ActorRef = system.actorOf(Props[Inventory])
      val customerTestActor: ActorRef = system.actorOf(Props(Customers(inventoryTestActor)))
      val testCustomer: Customer = Customer("Aditya Gupta",
        "address 123", "123456789", "aditya.gupta@gmail.com")
      val wrongEmail: String = "dummy@gmail.com"

      customerTestActor ! AddCustomer(testCustomer)
      customerTestActor ! GetCustomerDetails(testCustomer.email)
      expectMsg(testCustomer)

      customerTestActor ! GetCustomerDetails(wrongEmail)
      expectMsg(CustomerDoesNotExists(s"Customer[$wrongEmail] does not exists!"))
    }

    "Add an order to a customer" in {
      val inventoryTestActor: ActorRef = system.actorOf(Props[Inventory])
      val customerTestActor: ActorRef = system.actorOf(Props(Customers(inventoryTestActor)))
      val testCustomer: Customer = Customer("Aditya Gupta",
        "address 123", "123456789", "aditya.gupta@gmail.com")
      val testOrder: Order = Order("1234",
        Set(OrderItem("Table", 5), OrderItem("Mouse", 10), OrderItem("Chair", 1)),
        1500.0, "aditya.gupta@gmail.com")
      val wrongEmail: String = "dummy@gmail.com"

      customerTestActor ! AddCustomer(testCustomer)
      customerTestActor ! AddOrderToCustomer(testCustomer.email, testOrder)
      customerTestActor ! GetCustomerDetails(testCustomer.email)
      expectMsg(testCustomer.copy(orders = Set(testOrder)))

      customerTestActor ! AddOrderToCustomer(wrongEmail, testOrder)
      expectMsg(CustomerDoesNotExists(s"Customer[$wrongEmail] does not exists!"))
    }
  }
}
