object AppConstants {

  case class Customer(name: String, address: String, phoneNo: String, email: String, orders: Set[Order] = Set())
  case class Order(orderNo: String , items: Set[OrderItem], total: Double, customerEmail: String, refund: Boolean = false)
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
  case class ProductDeleted()
  case class ProductAdded()
  case class NotEnoughProductQuantity()
  case class ProductQuantityDecreased()

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

  // ProductCost Commands
  case class UpdateCost(productName: String, productCost: Double)
  case class ProductCostState()
  case class GetProductCost(productName: String)
  case class DeleteProductCost(productName: String)

  // ProductCost Response
  case class ProductDoesNotExists()
  case class ProductPriceUpdated()
  case class ProductPriceDeleted()

  // Order Commands
  case class AddOrder(customerEmail: String, orderItems: Set[OrderItem], refund: Boolean = false)
  case class OrdersState()

  // Order Response
  case class OrderProcessingOutput(processed: Boolean, failedReason: String = "")

  // Exceptions
  case class CustomerEmailAlreadyExists(message: String)
  case class CustomerDoesNotExists(message: String)

}
