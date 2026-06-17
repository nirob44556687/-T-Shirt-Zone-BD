package com.example.data.repository

import com.example.data.dao.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class ShopRepository(
    private val productDao: ProductDao,
    private val cartDao: CartDao,
    private val wishlistDao: WishlistDao,
    private val orderDao: OrderDao,
    private val legalPageDao: LegalPageDao
) {
    // PRODUCTS
    val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()

    suspend fun getProductById(id: Int): ProductEntity? = productDao.getProductById(id)

    suspend fun insertProduct(product: ProductEntity) = productDao.insertProduct(product)

    suspend fun updateProduct(product: ProductEntity) = productDao.updateProduct(product)

    suspend fun deleteProduct(product: ProductEntity) = productDao.deleteProduct(product)


    // CART
    val cartItems: Flow<List<CartItemEntity>> = cartDao.getCartItems()

    suspend fun addToCart(item: CartItemEntity) = cartDao.insertCartItem(item)

    suspend fun updateCartQuantity(id: Int, quantity: Int) = cartDao.updateQuantity(id, quantity)

    suspend fun deleteFromCart(id: Int) = cartDao.deleteFromCart(id)

    suspend fun clearCart() = cartDao.clearCart()


    // WISHLIST
    val wishlistItems: Flow<List<WishlistItemEntity>> = wishlistDao.getWishlistItems()

    suspend fun addToWishlist(item: WishlistItemEntity) = wishlistDao.insertWishlistItem(item)

    suspend fun deleteFromWishlist(productId: Int) = wishlistDao.deleteWishlistItem(productId)

    fun isProductInWishlist(productId: Int): Flow<Boolean> = wishlistDao.isProductInWishlist(productId)


    // ORDERS
    val allOrders: Flow<List<OrderEntity>> = orderDao.getAllOrders()

    fun getOrdersByPhone(phone: String): Flow<List<OrderEntity>> = orderDao.getOrdersByPhone(phone)

    suspend fun placeOrder(order: OrderEntity): Long = orderDao.insertOrder(order)

    suspend fun updateOrderStatus(id: Int, status: String) = orderDao.updateOrderStatus(id, status)

    suspend fun updateOrderPaymentStatus(id: Int, paymentStatus: String) = orderDao.updatePaymentStatus(id, paymentStatus)


    // LEGAL PAGES
    suspend fun getLegalPage(id: String): LegalPageEntity? = legalPageDao.getLegalPage(id)

    fun observeLegalPage(id: String): Flow<LegalPageEntity?> = legalPageDao.observeLegalPage(id)

    suspend fun updateLegalPage(page: LegalPageEntity) = legalPageDao.insertLegalPage(page)
}
