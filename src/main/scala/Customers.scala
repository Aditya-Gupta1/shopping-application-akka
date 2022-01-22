import AppConstants.{AddCustomer, Customer, CustomerDoesNotExists, CustomerEmailAlreadyExists, CustomerExists, CustomerState, DeleteCustomer, UpdateCustomerDetails}
import akka.actor.{Actor, ActorRef}
import com.typesafe.scalalogging.LazyLogging

case class Customers(inventory: ActorRef) extends Actor with LazyLogging {

  var customers: Map[String, Customer] = Map()

  override def receive: Receive = {
    case AddCustomer(customer: Customer) => {
      if(customers.contains(customer.email)) {
        logger.error(s"Email[${customer.email}] already exists!")
        sender() ! CustomerEmailAlreadyExists(s"Email[${customer.email}] already exists!")
      }
      else {
        customers = customers ++ Map(customer.email -> customer)
        logger.info(s"Customer[$customer] added successfully.")
      }
    }
    case DeleteCustomer(customer: Customer) => {
      if(customers.contains(customer.email)) {
        customers -= customer.email
        logger.info(s"Customer[$customer] deleted successfully.")
      }
      else {
        logger.error(s"Customer[$customer] does not exists!")
        sender() ! CustomerDoesNotExists(s"Customer[$customer] does not exists!")
      }
    }
    case UpdateCustomerDetails(email: String, modifiedCustomer: Customer) => {
      if(customers.contains(email)) {
        customers -= email
        customers = customers ++ Map(email -> modifiedCustomer)
        logger.info(s"Customer[$modifiedCustomer] updated successfully.")
      }
      else {
        logger.error(s"Customer[$modifiedCustomer] does not exists!")
        sender() ! CustomerDoesNotExists(s"Customer[$modifiedCustomer] does not exists!")
      }
    }
    case CustomerExists(customerEmail: String) => sender() ! customers.contains(customerEmail)
    case CustomerState => sender() ! customers
  }
}
