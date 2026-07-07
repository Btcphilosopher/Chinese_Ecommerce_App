package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val category: String,
    val price: Double,
    val originalPrice: Double,
    val rating: Float,
    val salesCount: Int,
    val stock: Int,
    val imageUrl: String,
    val brand: String,
    val videoUrl: String? = null,
    val merchantName: String = "星购自营旗舰店",
    val specifications: String = "[]", // Comma or JSON-like specs
    val isTodayRecommended: Boolean = false,
    val isFlashSale: Boolean = false,
    val flashSalePrice: Double? = null,
    val isNewArrival: Boolean = false,
    val isLimitedOffer: Boolean = false
) : Serializable

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int,
    val quantity: Int,
    val isSelected: Boolean = true
)

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey val id: String, // e.g., XG202607071023
    val timestamp: Long = System.currentTimeMillis(),
    val itemsJson: String, // String representation of products purchased [ {productId, quantity, purchasePrice} ]
    val status: String, // PENDING_PAY, PAID, SHIPPED, DELIVERED, CANCELLED, REFUND_PENDING, REFUNDED
    val totalPrice: Double,
    val addressDetail: String,
    val recipient: String,
    val phone: String,
    val deliveryMethod: String = "极速顺丰",
    val usedCoupon: String? = null,
    val discountAmount: Double = 0.0,
    val logisticsTimelineJson: String = "[]", // e.g. [ {"time": "12:00", "content": "订单已提交"} ]
    val courierName: String = "刘师傅",
    val courierPhone: String = "138-1234-5678"
)

@Entity(tableName = "addresses")
data class Address(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val recipient: String,
    val phone: String,
    val province: String,
    val city: String,
    val district: String,
    val detail: String,
    val isDefault: Boolean = false
)

@Entity(tableName = "favorites")
data class Favorite(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int
)

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val type: String // ORDER, DISPATCH, PROMOTION, SYSTEM
)

@Entity(tableName = "support_messages")
data class SupportMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sender: String, // USER, AI_BOT, CS_AGENT
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)
