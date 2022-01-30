object AppConstants {

  case class Customer(name: String, address: String, phoneNo: String, email: String, orders: Set[Order] = Set())
  case class Order(orderNo: String , items: Set[OrderItem], total: Double, customerEmail: String)
  case class OrderItem(productName: String, quantity: Int)

  // Inventory Commands
  case class IsEmpty(productName: String)
  case class GetItemFromInventory(productName: String)
  case class InventoryState()
  case class AddToInventory(productName: String, quantity: Int)
  case class DecreaseItemsFromInventory(productName: String, quantity: Int)
  case class DeleteProduct(productName: String)
  case class DoesExistsInInventory(productName: String)

  // Inventory Response
  case class ProductDoesNotExists()

  // Customer Commands
  case class AddCustomer(customer: Customer)
  case class DeleteCustomer(customer: Customer)
  case class UpdateCustomerDetails(customerEmail: String, modifiedCustomer: Customer)
  case class CustomerState()
  case class GetCustomerDetails(customerEmail: String)
  case class CustomerExists(customerEmail: String)
  case class AddOrderToCustomer(customerEmail: String, order: Order)

  // Customer Response
  case class CustomerAdded()
  case class CustomerDeleted()
  case class CustomerUpdated()
  case class OrderAddedToCustomer()

  // Prices Commands
  case class UpdateCost(productName: String, productCost: Double)
  case class ProductCostState()
  case class GetProductCost(productName: String)

  // Order Commands
  case class AddOrder(customerEmail: String, orderItems: Set[OrderItem])
  case class OrdersState()

  // Order Response
  case class IsOrderProcessed(processed: Boolean, failedReason: String = "")

  // Exceptions
  case class CustomerEmailAlreadyExists(message: String)
  case class CustomerDoesNotExists(message: String)

}
