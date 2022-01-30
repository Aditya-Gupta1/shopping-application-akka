import AppConstants.{AddCustomer, AddOrderToCustomer, Customer, CustomerAdded, CustomerDeleted, CustomerDoesNotExists, CustomerEmailAlreadyExists, CustomerExists, CustomerState, CustomerUpdated, DeleteCustomer, GetCustomerDetails, Order, OrderAddedToCustomer, UpdateCustomerDetails}
import akka.actor.{Actor, ActorRef}
import com.typesafe.scalalogging.LazyLogging

case class Customers(inventory: ActorRef) extends Actor with LazyLogging {

  var customers: Map[String, Customer] = Map()

  override def receive: Receive = {
    case AddCustomer(customer: Customer) =>
      if(customers.contains(customer.email)) {
        logger.error(s"Email[${customer.email}] already exists!")
        sender() ! CustomerEmailAlreadyExists(s"Email[${customer.email}] already exists!")
      }
      else {
        customers = customers ++ Map(customer.email -> customer)
        logger.info(s"Customer[$customer] added successfully.")
        sender() ! CustomerAdded
      }
    case DeleteCustomer(customer: Customer) =>
      if(customers.contains(customer.email)) {
        customers -= customer.email
        logger.info(s"Customer[$customer] deleted successfully.")
        sender() ! CustomerDeleted
      }
      else {
        logger.error(s"Customer[$customer] does not exists!")
        sender() ! CustomerDoesNotExists(s"Customer[$customer] does not exists!")
      }
    case UpdateCustomerDetails(email: String, modifiedCustomer: Customer) =>
      if(customers.contains(email)) {
        customers -= email
        customers = customers ++ Map(email -> modifiedCustomer)
        logger.info(s"Customer[$modifiedCustomer] updated successfully.")
        sender() ! CustomerUpdated
      }
      else {
        logger.error(s"Customer[$modifiedCustomer] does not exists!")
        sender() ! CustomerDoesNotExists(s"Customer[$modifiedCustomer] does not exists!")
      }
    case AddOrderToCustomer(customerEmail: String, order: Order) =>
      if(customers.contains(customerEmail)) {
        var orders = customers(customerEmail).orders
        orders += order
        customers = customers ++ Map(customerEmail ->
          customers(customerEmail).copy(orders = orders))
        logger.info(s"Order[$order] added to customer[$customerEmail]")
        sender() ! OrderAddedToCustomer
      }
      else {
        logger.error(s"Customer[$customerEmail] does not exists!")
        sender() ! CustomerDoesNotExists(s"Customer[$customerEmail] does not exists!")
      }
    case GetCustomerDetails(customerEmail: String) =>
      if(customers.contains(customerEmail))
        sender() ! customers(customerEmail)
      else {
        logger.error(s"Customer[$customerEmail] does not exists!")
        sender() ! CustomerDoesNotExists(s"Customer[$customerEmail] does not exists!")
      }
    case CustomerExists(customerEmail: String) => sender() ! customers.contains(customerEmail)
    case CustomerState => sender() ! customers
  }
}
