package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = Repository(db)

    // User profile state
    var userName = MutableStateFlow("张小星")
    var userVipLevel = MutableStateFlow("黑金会员 VIP 5")
    var userPoints = MutableStateFlow(10500)
    var userBalance = MutableStateFlow(5888.00)
    var userAvatar = MutableStateFlow("ic_launcher_foreground")

    // Search History and Hot searches
    val hotSearches = listOf("星耀90 Pro", "8K超高画质电视", "国潮卫衣", "故宫口红", "乳胶枕", "汉服", "i9电竞本")
    val searchHistory = MutableStateFlow(mutableListOf("星耀手机", "龙井茶", "跑步鞋"))

    // Selected Cart Items for Checkout
    val selectedCartItemIds = MutableStateFlow<Set<Int>>(emptySet())

    // Coupons
    val availableCoupons = listOf(
        Coupon("XG618_CON", "星购新店专享券", 50.0, 300.0),
        Coupon("XG_VIP_BLACK", "尊享黑金专享特权券", 500.0, 5000.0),
        Coupon("XG_FREE_SHIPPING", "全额免邮券", 15.0, 0.0)
    )
    val selectedCoupon = MutableStateFlow<Coupon?>(null)

    init {
        viewModelScope.launch {
            repository.seedDatabaseIfEmpty()
        }
    }

    // Products Flow
    val products: StateFlow<List<Product>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart Items Flow
    val cartItems: StateFlow<List<CartItem>> = repository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Orders Flow
    val orders: StateFlow<List<Order>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Addresses Flow
    val addresses: StateFlow<List<Address>> = repository.allAddresses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Favorites Flow
    val favorites: StateFlow<List<Favorite>> = repository.allFavorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Notifications Flow
    val notifications: StateFlow<List<Notification>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Support Chat Flow
    val supportMessages: StateFlow<List<SupportMessage>> = repository.supportMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search query
    val searchQuery = MutableStateFlow("")

    // Active Search Filters
    val filterBrand = MutableStateFlow<String?>(null)
    val filterMinPrice = MutableStateFlow<Double?>(null)
    val filterMaxPrice = MutableStateFlow<Double?>(null)
    val filterOnlyInStock = MutableStateFlow(false)
    val filterSortBy = MutableStateFlow("sales") // sales, price_asc, price_desc, rating

    // Core Actions
    fun addToCart(productId: Int, qty: Int = 1) {
        viewModelScope.launch {
            repository.addCartItem(productId, qty)
            addNotification("加入购物车成功", "您已成功将商品加入购物车，请前往购物车结算。")
        }
    }

    fun updateCartQuantity(cartItem: CartItem, newQty: Int) {
        viewModelScope.launch {
            if (newQty <= 0) {
                repository.deleteCartItem(cartItem)
            } else {
                repository.updateCartItem(cartItem.copy(quantity = newQty))
            }
        }
    }

    fun deleteCartItem(cartItem: CartItem) {
        viewModelScope.launch {
            repository.deleteCartItem(cartItem)
        }
    }

    fun toggleCartSelection(cartItemId: Int) {
        val current = selectedCartItemIds.value
        selectedCartItemIds.value = if (current.contains(cartItemId)) {
            current - cartItemId
        } else {
            current + cartItemId
        }
    }

    fun toggleAllCartSelection(cartList: List<CartItem>) {
        val current = selectedCartItemIds.value
        if (current.size == cartList.size) {
            selectedCartItemIds.value = emptySet()
        } else {
            selectedCartItemIds.value = cartList.map { it.id }.toSet()
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
            selectedCartItemIds.value = emptySet()
        }
    }

    fun toggleFavorite(productId: Int, isFav: Boolean) {
        viewModelScope.launch {
            if (isFav) {
                repository.removeFavorite(productId)
            } else {
                repository.addFavorite(productId)
            }
        }
    }

    fun addNotification(title: String, content: String, type: String = "SYSTEM") {
        viewModelScope.launch {
            repository.addNotification(Notification(title = title, content = content, type = type))
        }
    }

    fun markNotificationRead(id: Int) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    // Submit Support message
    fun sendUserMessage(text: String) {
        if (text.trim().isEmpty()) return
        viewModelScope.launch {
            repository.sendSupportMessage(SupportMessage(sender = "USER", message = text))
            
            // Generate auto-bot reply
            delay(1000)
            val reply = generateBotReply(text)
            repository.sendSupportMessage(SupportMessage(sender = "AI_BOT", message = reply))
        }
    }

    private fun generateBotReply(query: String): String {
        val q = query.lowercase()
        return when {
            q.contains("正品") || q.contains("假") || q.contains("质量") -> {
                "【星购正品承诺】星购商城所售商品均为100%正品官方授权，假一赔十。平台已接入国家数字商品溯源系统，您可以在订单中心直接下载并查看电子防伪标签和交易凭证。"
            }
            q.contains("配送") || q.contains("顺丰") || q.contains("包邮") || q.contains("快递") -> {
                "【顺丰极速配送】星购已建立全国五大智能自营仓（北京、上海、广州、成都、武汉）。全国一二线城市支持「上午下单下午送达，下午下单明早送达」的极速顺丰配送服务，满99元即可免运费。"
            }
            q.contains("会员") || q.contains("黑金") || q.contains("特权") -> {
                "【星购会员体系】星购尊享黑金会员可享受多重特权：\n1. 消费享200%积分增幅\n2. 终身免费退换货及极速闪电退款\n3. 每月专属大额优惠券礼包\n4. 优先购买全球独家首发新品。\n今日您已是尊贵的「VIP 5 黑金会员」！"
            }
            q.contains("售后") || q.contains("退货") || q.contains("退款") -> {
                "【极速售后流程】星购支持「7天无理由退货（拆封且无质量问题部分品类除外）」。您可以在订单中心找到相关订单，点击「申请退款」或「极速退货」，顺丰小哥将在一小时内上门取件，无需支付任何快递费用，退款秒级原路返还。"
            }
            q.contains("发票") || q.contains("电子发票") -> {
                "【绿色电子发票】星购倡导绿色环保，默认提供电子增值税普通发票或专用发票。您可以在订单详情页面，下拉到底部找到「发票下载」功能，一键下载 PDF 格式的电子发票。"
            }
            else -> {
                "【智能回复】收到您的关于「$query」的问题啦。星购云端助手正在为您查询，如果是紧急订单问题，您也可以直接回复「人工」转接到我们的星购 1对1 终身黑金专属管家为您提供极致专席服务。"
            }
        }
    }

    // Checkout Order logic
    fun placeOrder(
        items: List<Pair<Product, Int>>,
        address: Address,
        deliveryMethod: String,
        coupon: Coupon?,
        onSuccess: (String) -> Unit
    ) {
        viewModelScope.launch {
            val orderId = "XG" + SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date()) + (100..999).random()
            val subtotal = items.sumOf { it.first.price * it.second }
            val discount = if (coupon != null && subtotal >= coupon.minAmount) coupon.discount else 0.0
            val total = (subtotal - discount).coerceAtLeast(0.0)

            // Deduct from balance
            if (userBalance.value >= total) {
                userBalance.value -= total
                // Reward points
                val earnedPoints = (total / 10).toInt()
                userPoints.value += earnedPoints

                val itemsJson = items.map { 
                    "{\"productId\":${it.first.id},\"title\":\"${it.first.title}\",\"price\":${it.first.price},\"quantity\":${it.second},\"imageUrl\":\"${it.first.imageUrl}\"}"
                }.toString()

                val logisticsTimeline = listOf(
                    LogisticsPoint("10:00", "您的订单已支付成功，由星购自营仓智能分拣完成"),
                    LogisticsPoint("10:30", "包裹已被顺丰速运小哥揽收，开启飞速配送之旅"),
                    LogisticsPoint("14:00", "包裹已到达目的地分拨中心，正在装车派送"),
                    LogisticsPoint("16:00", "预计今日送达，顺丰派送员：刘师傅（138-1234-5678）")
                )

                val timelineJson = logisticsTimeline.map { "{\"time\":\"${it.time}\",\"content\":\"${it.content}\"}" }.toString()

                val newOrder = Order(
                    id = orderId,
                    itemsJson = itemsJson,
                    status = "PAID",
                    totalPrice = total,
                    addressDetail = "${address.province}${address.city}${address.district}${address.detail}",
                    recipient = address.recipient,
                    phone = address.phone,
                    deliveryMethod = deliveryMethod,
                    usedCoupon = coupon?.title,
                    discountAmount = discount,
                    logisticsTimelineJson = timelineJson
                )

                repository.createOrder(newOrder)
                // Deduct stock
                items.forEach { (prod, qty) ->
                    repository.updateProduct(prod.copy(stock = (prod.stock - qty).coerceAtLeast(0)))
                }
                
                // Add notifications
                addNotification("订单支付成功", "您的订单 $orderId 已成功支付 ￥$total，我们将为您极速配送。")
                addNotification("积分到账", "成功获取 $earnedPoints 积分！当前积分累计：${userPoints.value}")

                // Clear checked cart items
                val checkedIds = selectedCartItemIds.value
                val cartList = repository.cartItems.first()
                cartList.forEach { cartItem ->
                    if (checkedIds.contains(cartItem.id)) {
                        repository.deleteCartItem(cartItem)
                    }
                }
                selectedCartItemIds.value = emptySet()

                onSuccess(orderId)
            } else {
                // insufficient balance notify
                addNotification("支付失败", "您的星购电子账户余额不足（当前：￥${userBalance.value}），请先前往个人中心充值。", "SYSTEM")
            }
        }
    }

    // Cancel order
    fun cancelOrder(order: Order) {
        viewModelScope.launch {
            repository.updateOrder(order.copy(status = "CANCELLED"))
            // return balance
            userBalance.value += order.totalPrice
            addNotification("订单已取消", "您的订单 ${order.id} 已取消，退款 ￥${order.totalPrice} 已秒级返还至您的余额中。")
        }
    }

    // Update shipping address on existing unpaid order
    fun updateOrderAddress(order: Order, newAddress: Address) {
        viewModelScope.launch {
            repository.updateOrder(order.copy(
                addressDetail = "${newAddress.province}${newAddress.city}${newAddress.district}${newAddress.detail}",
                recipient = newAddress.recipient,
                phone = newAddress.phone
            ))
            addNotification("收货地址更新", "您的订单 ${order.id} 的配送地址已成功更新。")
        }
    }

    // Confirm receipt
    fun confirmReceipt(order: Order) {
        viewModelScope.launch {
            repository.updateOrder(order.copy(status = "DELIVERED"))
            addNotification("订单已收货", "感谢您的确认！订单 ${order.id} 交易已成功。希望您对我们的服务感到满意。")
        }
    }

    // Request Refund
    fun requestRefund(order: Order) {
        viewModelScope.launch {
            repository.updateOrder(order.copy(status = "REFUND_PENDING"))
            addNotification("退款申请受理中", "订单 ${order.id} 的退款申请已被商家受理，请等待客服进一步确认。")
            // simulate fast refund response
            delay(2000)
            repository.updateOrder(order.copy(status = "REFUNDED"))
            userBalance.value += order.totalPrice
            addNotification("极速闪电退款成功", "尊贵黑金会员触发闪电退款！订单 ${order.id} 的 ￥${order.totalPrice} 已全额秒级退回到您的余额。")
        }
    }

    // Address Management
    fun updateOrderStatus(order: Order, status: String) {
        viewModelScope.launch {
            repository.updateOrder(order.copy(status = status))
        }
    }

    fun addShippingAddress(recipient: String, phone: String, province: String, city: String, dist: String, det: String, isDef: Boolean) {
        viewModelScope.launch {
            val newAddress = Address(recipient = recipient, phone = phone, province = province, city = city, district = dist, detail = det, isDefault = isDef)
            repository.addAddress(newAddress)
        }
    }

    fun removeAddress(address: Address) {
        viewModelScope.launch {
            repository.deleteAddress(address)
        }
    }

    // Add search to history
    fun addSearchQuery(query: String) {
        if (query.trim().isEmpty()) return
        val current = searchHistory.value.toMutableList()
        current.remove(query)
        current.add(0, query)
        searchHistory.value = current.take(10).toMutableList()
        searchQuery.value = query
    }

    fun clearSearchHistory() {
        searchHistory.value = mutableListOf()
    }

    // Merchant management
    fun addNewProduct(
        title: String,
        desc: String,
        category: String,
        price: Double,
        origPrice: Double,
        stock: Int,
        brand: String,
        specifications: String
    ) {
        viewModelScope.launch {
            val newProd = Product(
                title = title,
                description = desc,
                category = category,
                price = price,
                originalPrice = origPrice,
                rating = 5.0f,
                salesCount = 0,
                stock = stock,
                imageUrl = "ic_launcher_foreground",
                brand = brand,
                specifications = "规格: $specifications"
            )
            repository.insertProduct(newProd)
            addNotification("新品上架成功", "您已成功在星购平台发布新商品「$title」，商品编号：#${(20..100).random()}。")
        }
    }

    // Edit Product stock / price
    fun updateProductStockPrice(product: Product, newStock: Int, newPrice: Double) {
        viewModelScope.launch {
            repository.updateProduct(product.copy(stock = newStock, price = newPrice))
            addNotification("商品信息修改成功", "您已成功修改「${product.title}」的库存至 $newStock，价格至 ￥$newPrice。")
        }
    }

    fun deleteProductByMerchant(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
            addNotification("商品下架通知", "您已将「${product.title}」成功下架并撤回。")
        }
    }
}

// Support data classes
data class Coupon(
    val code: String,
    val title: String,
    val discount: Double,
    val minAmount: Double
)

data class LogisticsPoint(
    val time: String,
    val content: String
)
