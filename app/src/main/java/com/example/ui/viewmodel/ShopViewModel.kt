package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.ShopRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

sealed class AuthState {
    object Unauthenticated : AuthState()
    data class Authenticated(val user: UserProfile) : AuthState()
}

data class UserProfile(
    val name: String,
    val phone: String,
    val email: String,
    val address: String,
    val rewardPoints: Int = 120
)

class ShopViewModel(private val repository: ShopRepository) : ViewModel() {

    // --- LANGUAGE STATE ---
    private val _isEnglish = MutableStateFlow(false) // Default is Bangla (primary)
    val isEnglish: StateFlow<Boolean> = _isEnglish.asStateFlow()

    fun toggleLanguage() {
        _isEnglish.value = !_isEnglish.value
    }

    // --- AUTHENTICATION STATE ---
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(name: String, phone: String, email: String, address: String) {
        _authState.value = AuthState.Authenticated(
            UserProfile(name, phone, email, address)
        )
    }

    fun logout() {
        _authState.value = AuthState.Unauthenticated
        _isAdminMode.value = false
    }

    // --- ADMIN STATE ---
    private val _isAdminMode = MutableStateFlow(false)
    val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

    fun setAdminMode(enabled: Boolean) {
        _isAdminMode.value = enabled
    }

    // --- PRODUCT STATES ---
    val productsList: StateFlow<List<ProductEntity>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val filteredProducts: StateFlow<List<ProductEntity>> = combine(
        productsList,
        _searchQuery,
        _selectedCategory,
        _isEnglish
    ) { products, query, category, isEng ->
        products.filter { product ->
            val matchesQuery = if (isEng) {
                product.title.contains(query, ignoreCase = true) || product.category.contains(query, ignoreCase = true)
            } else {
                product.titleBn.contains(query, ignoreCase = true) || product.categoryBn.contains(query, ignoreCase = true)
            }
            
            val matchesCategory = if (category == "All" || category == "সব") {
                true
            } else {
                if (isEng) {
                    product.category.equals(category, ignoreCase = true)
                } else {
                    product.categoryBn.equals(category, ignoreCase = true)
                }
            }
            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun addProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.insertProduct(product)
        }
    }

    fun editProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    // --- CART STATE ---
    val cartItems: StateFlow<List<CartItemEntity>> = repository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addToCart(product: ProductEntity, size: String) {
        viewModelScope.launch {
            val existing = cartItems.value.find { it.productId == product.id && it.sizeSelected == size }
            if (existing != null) {
                repository.updateCartQuantity(existing.id, existing.quantity + 1)
            } else {
                repository.addToCart(
                    CartItemEntity(
                        productId = product.id,
                        title = product.title,
                        titleBn = product.titleBn,
                        price = product.price,
                        sizeSelected = size,
                        quantity = 1,
                        imageUrl = product.imageUrl
                    )
                )
            }
        }
    }

    fun incrementCartItem(itemId: Int, currentQty: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(itemId, currentQty + 1)
        }
    }

    fun decrementCartItem(itemId: Int, currentQty: Int) {
        viewModelScope.launch {
            if (currentQty > 1) {
                repository.updateCartQuantity(itemId, currentQty - 1)
            } else {
                repository.deleteFromCart(itemId)
            }
        }
    }

    fun deleteFromCart(itemId: Int) {
        viewModelScope.launch {
            repository.deleteFromCart(itemId)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    // --- WISHLIST STATE ---
    val wishlistItems: StateFlow<List<WishlistItemEntity>> = repository.wishlistItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleWishlist(product: ProductEntity) {
        viewModelScope.launch {
            val exists = wishlistItems.value.any { it.productId == product.id }
            if (exists) {
                repository.deleteFromWishlist(product.id)
            } else {
                repository.addToWishlist(
                    WishlistItemEntity(
                        productId = product.id,
                        title = product.title,
                        titleBn = product.titleBn,
                        price = product.price,
                        imageUrl = product.imageUrl
                    )
                )
            }
        }
    }

    fun isProductInWishlist(productId: Int): Flow<Boolean> {
        return repository.isProductInWishlist(productId)
    }

    // --- ORDER HISTORY ---
    val allOrders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun checkout(
        customerName: String,
        customerPhone: String,
        deliveryAddress: String,
        paymentMethod: String,
        deliveryArea: String // Dhaka / Outside Dhaka
    ) {
        viewModelScope.launch {
            val items = cartItems.value
            if (items.isEmpty()) return@launch

            val deliveryFee = if (deliveryArea == "Dhaka" || deliveryArea == "ঢাকা") 60.0 else 120.0
            val subtotal = items.sumOf { it.price * it.quantity }
            val grandTotal = subtotal + deliveryFee

            val summaryBuilder = StringBuilder()
            items.forEach {
                summaryBuilder.append("${it.title} (${it.sizeSelected}) x${it.quantity}, ")
            }
            val summary = summaryBuilder.toString().removeSuffix(", ")

            val orderNumber = "TZB-" + UUID.randomUUID().toString().substring(0, 8).uppercase()

            val newOrder = OrderEntity(
                orderNumber = orderNumber,
                itemsSummary = summary,
                totalPrice = grandTotal,
                status = "Pending",
                customerName = customerName,
                customerPhone = customerPhone,
                deliveryAddress = deliveryAddress,
                paymentMethod = paymentMethod,
                paymentStatus = if (paymentMethod == "Cash on Delivery" || paymentMethod == "ক্যাশ অন ডেলিভারি") "Unpaid" else "Paid"
            )

            repository.placeOrder(newOrder)
            repository.clearCart()
        }
    }

    fun updateOrderStatus(orderId: Int, status: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status)
        }
    }

    fun updateOrderPaymentStatus(orderId: Int, paymentStatus: String) {
        viewModelScope.launch {
            repository.updateOrderPaymentStatus(orderId, paymentStatus)
        }
    }

    // --- LEGAL PAGES ---
    fun observeLegalPage(id: String): Flow<LegalPageEntity?> {
        return repository.observeLegalPage(id)
    }

    fun saveLegalPage(id: String, titleEn: String, titleBn: String, contentEn: String, contentBn: String) {
        viewModelScope.launch {
            repository.updateLegalPage(
                LegalPageEntity(id, titleEn, titleBn, contentEn, contentBn)
            )
        }
    }
}

class ShopViewModelFactory(private val repository: ShopRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShopViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShopViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
