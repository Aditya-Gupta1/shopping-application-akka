<h1 align="center">Shopping Application</h1>

### 1. Inventory

Inventory Actor keeps an account of the products and their quantities available.

Some commands this actor handles are:
<details><summary><b>AddToInventory(productName: String, quantity: Int)</b></summary>Adds a new product to the inventory</details>
<details><summary><b>DeleteProduct(productName: String)</b></summary></details>
<details><summary><b>DecreaseItemsFromInventory(productName: String, quantity: Int)</b></summary>Decrease product quantity in inventory. If requested quantity is greater than the available quantity, an error is logged and an appropriate response is sent.</details>
<details><summary><b>GetItemFromInventory(productName: String)</b></summary>Get the counts of given product from inventory. If it doesn't exist, return -1</details>
<details><summary><b>IsEmpty(productName: String)</b></summary>Checks if the given product has no items present in inventory.</details>
<details><summary><b>DoesExistsInInventory(productName: String)</b></summary>Checks if the given product is available in the inventory</details>
<details><summary><b>InventoryState</b></summary>Returns all the products and their quantities that are in inventory.</details>

### 2. ProductCost

ProductCost actor handles the pricing of all the products in the inventory.

Some command this actor handles are:
<details><summary><b>UpdateCost(productName: String, cost: Double)</b></summary>Updates the cost of the given product. If the product does not exist, it inserts it with the given cost.</details>
<details><summary><b>DeleteProductCost(productName: String)</b></summary>Delete the cost of the given product from the record.</details>
<details><summary><b>GetProductCost(productName: String)</b></summary>Returns the cost of the given product. If it doesn't exist in the record, return -1</details>
<details><summary><b>ProductCostState</b></summary>Returns the prices of all the products currently in record</details>

### 3. Customers

Customer actor maintains the records of all the customers.

Some commands this actor handles are:
<details><summary><b>AddCustomer(customer: Customer)</b></summary>Adds a customer to the record. A unique customer is identified by their email.</details>
<details><summary><b> DeleteCustomer(customer: Customer)</b></summary>Deletes the given customer. If the customer does not exist, an appropriate response is sent.</details>
<details><summary><b>UpdateCustomerDetails(email: String, modifiedCustomer: Customer)</b></summary>Updates the customer with given email with a modified customer object having updated customer details.</details>
<details><summary><b>GetCustomerDetails(customerEmail: String)</b></summary>Fetches the customer details for the given email. If the customer does not exist, sends an appropriate response.</details>
<details><summary><b>CustomerExists(customerEmail: String)</b></summary>Returns a boolean signifying if the customer with given email exists or not.</details>
<details><summary><b>CustomerState</b></summary>Returns all the customers currently in the record.</details>
<details><summary><b>AddOrderToCustomer(customerEmail: String, order: Order)</b></summary>Adds the given order to the customer.</details>

### 4. Orders

Order actor handles the order processing. Once the order has been processed, it can't be deleted. It can only be refunded(yet to be implemented).

Some commands this actor handles are:
<details><summary><b>AddOrder(customerEmail: String, orderItems: Set[OrderItem])</b></summary>Given the customer email and the order items, this command processed the order and adds it to the given customer. Handles the cases when invalid order items are given.</details>
<details><summary><b>OrdersState</b></summary>Returns all the orders that are successfully processed by the system.</details>