package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val titleBn: String,
    val description: String,
    val descriptionBn: String,
    val price: Double,
    val category: String,
    val categoryBn: String,
    val imageUrl: String, // Resource name or URL
    val isPremium: Boolean = false,
    val rating: Double = 4.5,
    val stock: Int = 15,
    val sizeAvailable: String = "M,L,XL,XXL" // Comma-separated sizes
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int,
    val title: String,
    val titleBn: String,
    val price: Double,
    val sizeSelected: String,
    val quantity: Int,
    val imageUrl: String
)

@Entity(tableName = "wishlist_items")
data class WishlistItemEntity(
    @PrimaryKey val productId: Int,
    val title: String,
    val titleBn: String,
    val price: Double,
    val imageUrl: String
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val orderNumber: String,
    val itemsSummary: String, // String representation or summary of ordered items
    val totalPrice: Double,
    val orderDate: Long = System.currentTimeMillis(),
    val status: String, // Pending, Paid, Shipped, Delivered, Cancelled
    val customerName: String,
    val customerPhone: String,
    val deliveryAddress: String,
    val paymentMethod: String, // bKash, Nagad, Rocket, COD
    val paymentStatus: String // Unpaid, Paid
)

@Entity(tableName = "legal_pages")
data class LegalPageEntity(
    @PrimaryKey val id: String, // privacy, terms, refund
    val titleEn: String,
    val titleBn: String,
    val contentEn: String,
    val contentBn: String
)
