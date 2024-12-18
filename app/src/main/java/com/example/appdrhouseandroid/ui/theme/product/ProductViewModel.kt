package com.example.appdrhouseandroid.ui.theme.product

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appdrhouseandroid.data.repositories.ProductRepository
import com.example.appdrhouseandroid.data.network.ProductResponse
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.UUID
import kotlin.random.Random

// Data Classes
data class CartItemUI(
    val product: ProductResponse,
    val quantity: Int = 1
)

data class OrderProduct(
    @SerializedName("productId")
    val productId: String,
    @SerializedName("quantity")
    val quantity: Int
)

data class OrderRequest(
    @SerializedName("id")
    val id: String,
    @SerializedName("products")
    val products: List<OrderProduct>,
    @SerializedName("totalAmount")
    val totalAmount: Double,
    @SerializedName("status")
    val status: String
)


data class OrderResponse(
    val id: String,
    val products: List<OrderProduct>,
    val totalAmount: Double,
    val status: String,
    @SerializedName("createdAt")
    val createdAt: String
)



// State Classes
sealed class OrderCreationState {
    object Initial : OrderCreationState()
    object Creating : OrderCreationState()
    data class Success(val order: OrderResponse) : OrderCreationState()
    data class Error(val message: String) : OrderCreationState()
}

data class ErrorResponse(
    val message: String,
    val error: String,
    val statusCode: Int
)

class ProductViewModel(private val productRepository: ProductRepository) : ViewModel() {

    private val _products = MutableStateFlow<List<ProductResponse>>(emptyList())
    val products: StateFlow<List<ProductResponse>> = _products

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _cartItems = mutableStateListOf<CartItemUI>()
    val cartItems: SnapshotStateList<CartItemUI> get() = _cartItems

    private val _orders = MutableStateFlow<List<OrderResponse>>(emptyList())
    val orders: StateFlow<List<OrderResponse>> = _orders.asStateFlow()

    private val _orderCreationState = MutableStateFlow<OrderCreationState>(OrderCreationState.Initial)
    val orderCreationState = _orderCreationState.asStateFlow()

    // Cart Management Functions
    fun addToCart(product: ProductResponse) {
        val existingItem = _cartItems.find { it.product._id == product._id }
        if (existingItem != null) {
            val index = _cartItems.indexOf(existingItem)
            _cartItems[index] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            _cartItems.add(CartItemUI(product))
            Log.d("CartViewModel", "Added to cart: ${product.name}, Cart size: ${_cartItems.size}")
            Log.d("ProductView", "Adding product to cart: ${product._id} - ${product.name}")

        }
    }

    fun removeFromCart(cartItem: CartItemUI) {
        _cartItems.remove(cartItem)
    }

    fun updateCartItemQuantity(cartItem: CartItemUI, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeFromCart(cartItem)
        } else {
            val index = _cartItems.indexOf(cartItem)
            if (index != -1) {
                _cartItems[index] = cartItem.copy(quantity = newQuantity)
            }
        }
    }

    fun clearCart() {
        _cartItems.clear()
        Log.d("CartViewModel", "Cart cleared")
    }

    fun calculateTotalPrice(): Double {
        return _cartItems.sumOf { it.product.price * it.quantity }
    }

    // Order Management Functions
    fun createOrder() {
        viewModelScope.launch {
            try {
                _orderCreationState.value = OrderCreationState.Creating

                // Create simple product list without extra fields
                val orderProducts = _cartItems.map { cartItem ->
                    OrderProduct(
                        productId = cartItem.product._id ?: throw IllegalStateException("Product ID cannot be null"),
                        quantity = cartItem.quantity
                    )
                }

                // Create order with exact same format as successful requests
                val orderRequest = OrderRequest(
                    id = "ORD" + Random.nextInt(10000, 99999),
                    products = orderProducts,
                    totalAmount = calculateTotalPrice(),
                    status = "completed"  // Match the successful request
                )

                Log.d("ProductViewModel", "Creating order with request: ${Gson().toJson(orderRequest)}")

                val response = productRepository.createOrder(orderRequest)

                if (response.isSuccessful) {
                    response.body()?.let { orderResponse ->
                        _orderCreationState.value = OrderCreationState.Success(orderResponse)
                        clearCart()
                    } ?: run {
                        _orderCreationState.value = OrderCreationState.Error("Empty response received")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ProductViewModel", "Error response: ${response.code()} - $errorBody")

                    val errorMessage = try {
                        Gson().fromJson(errorBody, ErrorResponse::class.java).message
                    } catch (e: Exception) {
                        errorBody ?: "Failed to create order"
                    }
                    _orderCreationState.value = OrderCreationState.Error(errorMessage)
                }
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error creating order", e)
                _orderCreationState.value = OrderCreationState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun getOrders() {
        viewModelScope.launch {
            try {
                val response = productRepository.getOrders()
                if (response.isSuccessful) {
                    response.body()?.let { orderList ->
                        _orders.value = orderList
                        Log.d("CartViewModel", "Fetched ${orderList.size} orders")
                    } ?: run {
                        Log.e("CartViewModel", "Empty response body when fetching orders")
                    }
                } else {
                    Log.e("CartViewModel", "Error fetching orders: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("CartViewModel", "Error fetching orders", e)
            }
        }
    }

    fun resetOrderCreationState() {
        _orderCreationState.value = OrderCreationState.Initial
    }

    // Product Loading Functions
    fun loadAllProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            val response = productRepository.getAllProducts()
            handleResponse(response)
        }
    }

    fun loadByCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val response = productRepository.getProductByCategory(category)
            handleResponse(response)
        }
    }

    private fun handleResponse(response: Response<out Any>) {
        if (response.isSuccessful) {
            when (response.body()) {
                is List<*> -> _products.value = response.body() as List<ProductResponse>
                is ProductResponse -> _products.value = listOf(response.body() as ProductResponse)
            }
            _error.value = null
        } else {
            _error.value = response.message()
        }
        _isLoading.value = false
    }
}

//class ProductViewModel(private val productRepository: ProductRepository) : ViewModel() {
//
//    private val _products = MutableStateFlow<List<ProductResponse>>(emptyList())
//    val products: StateFlow<List<ProductResponse>> = _products
//
//    private val _isLoading = MutableStateFlow(false)
//    val isLoading: StateFlow<Boolean> = _isLoading
//
//    private val _error = MutableStateFlow<String?>(null)
//    val error: StateFlow<String?> = _error
//
//    private val _cartItems = mutableStateListOf<CartItemUI>()
//    val cartItems: SnapshotStateList<CartItemUI> get() = _cartItems
//
//    // State for orders
//    private val _orders = MutableStateFlow<List<OrderResponse>>(emptyList())
//    val orders: StateFlow<List<OrderResponse>> = _orders.asStateFlow()
//
//    // State for order operations
//    sealed class OrderState {
//        object Idle : OrderState()
//        object Loading : OrderState()
//        data class Success(val message: String) : OrderState()
//        data class Error(val message: String) : OrderState()
//    }
//
//    private val _orderState = MutableStateFlow<OrderState>(OrderState.Idle)
//    val orderState: StateFlow<OrderState> = _orderState.asStateFlow()
//
//    // Function to create an order
//    fun createOrder() {
//        viewModelScope.launch {
//            try {
//                _orderState.value = OrderState.Loading
//
//                // Validate cart
//                if (_cartItems.isEmpty()) {
//                    _orderState.value = OrderState.Error("Cart is empty")
//                    return@launch
//                }
//
//                // Convert cart items to order products, ensuring we have valid MongoDB ObjectIds
//                val orderProducts = _cartItems.mapNotNull { cartItem ->
//                    cartItem.product.id?.let { productId ->
//                        try {
//                            OrderProduct(
//                                productId = productId, // This should be the MongoDB ObjectId string
//                                quantity = cartItem.quantity
//                            )
//                        } catch (e: Exception) {
//                            Log.e("CartViewModel", "Invalid product ID: $productId", e)
//                            null
//                        }
//                    }
//                }
//
//                if (orderProducts.isEmpty()) {
//                    _orderState.value = OrderState.Error("No valid products in cart")
//                    return@launch
//                }
//
//                val totalAmount = calculateTotalPrice()
//
//                val orderRequest = OrderRequest(
//                    products = orderProducts,
//                    totalAmount = totalAmount
//                )
//
//                // Log request for debugging
//                Log.d("CartViewModel", "Creating order: $orderRequest")
//
//                val response = productRepository.createOrder(orderRequest)
//
//                if (response.isSuccessful) {
//                    _orderState.value = OrderState.Success("Order created successfully")
//                    clearCart()
//                } else {
//                    val errorBody = response.errorBody()?.string()
//                    Log.e("CartViewModel", "Error creating order: $errorBody")
//                    _orderState.value = OrderState.Error(
//                        errorBody?.let {
//                            try {
//                                Gson().fromJson(it, ErrorResponse::class.java).message
//                            } catch (e: Exception) {
//                                it
//                            }
//                        } ?: "Failed to create order"
//                    )
//                }
//            } catch (e: Exception) {
//                Log.e("CartViewModel", "Error creating order", e)
//                _orderState.value = OrderState.Error(e.message ?: "Unknown error occurred")
//            }
//        }
//    }
//    // Add an ErrorResponse data class to parse error messages
//    data class ErrorResponse(
//        val message: String,
//        val error: String,
//        val statusCode: Int
//    )
//
//    // Function to get all orders
//    fun getOrders() {
//        viewModelScope.launch {
//            try {
//                val response = productRepository.getOrders()
//                if (response.isSuccessful) {
//                    response.body()?.let { orderList ->
//                        _orders.value = orderList
//                        Log.d("CartViewModel", "Fetched ${orderList.size} orders")
//                    } ?: run {
//                        Log.e("CartViewModel", "Empty response body when fetching orders")
//                    }
//                } else {
//                    Log.e("CartViewModel", "Error fetching orders: ${response.errorBody()?.string()}")
//                }
//            } catch (e: Exception) {
//                Log.e("CartViewModel", "Error fetching orders", e)
//            }
//        }
//    }
//
//
//    fun addToCart(product: ProductResponse) {
//        val existingItem = _cartItems.find { it.product.id == product.id }
//        if (existingItem != null) {
//            val index = _cartItems.indexOf(existingItem)
//            _cartItems[index] = existingItem.copy(quantity = existingItem.quantity + 1)
//        } else {
//            _cartItems.add(CartItemUI(product))
//            Log.d("CartViewModel", "Added to cart: ${product.name}, Cart size: ${_cartItems.size}")
//            Log.d("CartViewModel", "Cart contents: $_cartItems")
//        }
//    }
//
//    fun clearCart() {
//        _cartItems.clear()
//        Log.d("CartViewModel", "Cart cleared")
//    }
//    // Reset order state
//    fun resetOrderState() {
//        _orderState.value = OrderState.Idle
//    }
//
//    fun removeFromCart(cartItem: CartItemUI) {
//        _cartItems.remove(cartItem)
//    }
//
//    fun calculateTotalPrice(): Double {
//        return _cartItems.sumOf { it.product.price * it.quantity }
//    }
//
//    fun loadAllProducts() {
//        viewModelScope.launch {
//            _isLoading.value = true
//            val response = productRepository.getAllProducts()
//            handleResponse(response)
//        }
//    }
//
//    fun loadByCategory(category: String) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            val response = productRepository.getProductByCategory(category)
//            handleResponse(response)
//        }
//    }
//
//    private fun handleResponse(response: Response<out Any>) {
//        if (response.isSuccessful) {
//            when (response.body()) {
//                is List<*> -> _products.value = response.body() as List<ProductResponse>
//                is ProductResponse -> _products.value = listOf(response.body() as ProductResponse)
//            }
//            _error.value = null
//        } else {
//            _error.value = response.message()
//        }
//        _isLoading.value = false
//    }
//
//
//}