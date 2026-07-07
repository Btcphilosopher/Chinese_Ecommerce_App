package com.example.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class Repository(private val db: AppDatabase) {

    val productDao = db.productDao()
    val cartDao = db.cartDao()
    val orderDao = db.orderDao()
    val addressDao = db.addressDao()
    val favoriteDao = db.favoriteDao()
    val notificationDao = db.notificationDao()
    val supportMessageDao = db.supportMessageDao()

    // Products
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()
    fun getProductsByCategory(category: String): Flow<List<Product>> = productDao.getProductsByCategory(category)
    fun getProductById(id: Int): Flow<Product?> = productDao.getProductById(id)
    fun searchProducts(query: String): Flow<List<Product>> = productDao.searchProducts(query)
    
    suspend fun insertProduct(product: Product) = withContext(Dispatchers.IO) {
        productDao.insertProduct(product)
    }

    suspend fun updateProduct(product: Product) = withContext(Dispatchers.IO) {
        productDao.updateProduct(product)
    }

    suspend fun deleteProduct(product: Product) = withContext(Dispatchers.IO) {
        productDao.deleteProduct(product)
    }

    // Cart
    val cartItems: Flow<List<CartItem>> = cartDao.getCartItems()
    suspend fun addCartItem(productId: Int, qty: Int = 1) = withContext(Dispatchers.IO) {
        val existing = cartDao.getCartItems().first().find { it.productId == productId }
        if (existing != null) {
            cartDao.updateCartItem(existing.copy(quantity = existing.quantity + qty))
        } else {
            cartDao.insertCartItem(CartItem(productId = productId, quantity = qty))
        }
    }
    suspend fun updateCartItem(cartItem: CartItem) = withContext(Dispatchers.IO) {
        cartDao.updateCartItem(cartItem)
    }
    suspend fun deleteCartItem(cartItem: CartItem) = withContext(Dispatchers.IO) {
        cartDao.deleteCartItem(cartItem)
    }
    suspend fun clearCart() = withContext(Dispatchers.IO) {
        cartDao.clearCart()
    }

    // Orders
    val allOrders: Flow<List<Order>> = orderDao.getAllOrders()
    fun getOrderById(id: String): Flow<Order?> = orderDao.getOrderById(id)
    suspend fun createOrder(order: Order) = withContext(Dispatchers.IO) {
        orderDao.insertOrder(order)
    }
    suspend fun updateOrder(order: Order) = withContext(Dispatchers.IO) {
        orderDao.updateOrder(order)
    }

    // Address
    val allAddresses: Flow<List<Address>> = addressDao.getAllAddresses()
    suspend fun addAddress(address: Address) = withContext(Dispatchers.IO) {
        addressDao.insertAddress(address)
    }
    suspend fun updateAddress(address: Address) = withContext(Dispatchers.IO) {
        addressDao.updateAddress(address)
    }
    suspend fun deleteAddress(address: Address) = withContext(Dispatchers.IO) {
        addressDao.deleteAddress(address)
    }

    // Favorites
    val allFavorites: Flow<List<Favorite>> = favoriteDao.getAllFavorites()
    suspend fun addFavorite(productId: Int) = withContext(Dispatchers.IO) {
        val fav = Favorite(productId = productId)
        favoriteDao.insertFavorite(fav)
    }
    suspend fun removeFavorite(productId: Int) = withContext(Dispatchers.IO) {
        favoriteDao.deleteFavoriteByProduct(productId)
    }

    // Notifications
    val allNotifications: Flow<List<Notification>> = notificationDao.getAllNotifications()
    suspend fun addNotification(notification: Notification) = withContext(Dispatchers.IO) {
        notificationDao.insertNotification(notification)
    }
    suspend fun markNotificationRead(id: Int) = withContext(Dispatchers.IO) {
        notificationDao.markAsRead(id)
    }

    // Support Messages
    val supportMessages: Flow<List<SupportMessage>> = supportMessageDao.getAllMessages()
    suspend fun sendSupportMessage(msg: SupportMessage) = withContext(Dispatchers.IO) {
        supportMessageDao.insertMessage(msg)
    }

    // Seeding logic
    suspend fun seedDatabaseIfEmpty() = withContext(Dispatchers.IO) {
        val existingProducts = productDao.getAllProducts().first()
        if (existingProducts.isEmpty()) {
            val defaultProducts = listOf(
                Product(
                    id = 1,
                    title = "星耀 90 Pro 至臻版 5G 手机",
                    description = "搭载最新一代旗舰芯片，徕卡超动态双焦影像，2K 顶级微曲屏，支持120W超级快充与双向卫星通信。中国红限量定制机身，传承数字美学。",
                    category = "手机数码",
                    price = 5999.00,
                    originalPrice = 6499.00,
                    rating = 4.9f,
                    salesCount = 8240,
                    stock = 520,
                    imageUrl = "ic_launcher_foreground", // Fallback to icon drawable or simple shape
                    brand = "星耀科技 (XingYao)",
                    isTodayRecommended = true,
                    isFlashSale = true,
                    flashSalePrice = 5499.00,
                    specifications = "规格: 中国红 | 16GB+1TB | 5G全网通"
                ),
                Product(
                    id = 2,
                    title = "星视 8K 超高清智能巨幕电视 85寸",
                    description = "采用最新MiniLED量子点背光技术，2000nits峰值亮度，144Hz高刷电竞画质。内置4.2.2全景声音响，智能语音互联，客厅影院级享受。",
                    category = "家用电器",
                    price = 8999.00,
                    originalPrice = 9999.00,
                    rating = 4.8f,
                    salesCount = 1200,
                    stock = 45,
                    imageUrl = "ic_launcher_foreground",
                    brand = "星视影音",
                    isTodayRecommended = true,
                    specifications = "规格: 85寸旗舰版 | MiniLED | 8K超清"
                ),
                Product(
                    id = 3,
                    title = "星刃 Pro 16寸极速游戏本 (Core i9)",
                    description = "至尊酷睿i9处理器，配备超强独显RTX 4090，32G DDR5高速内存，2T NVMe 固态硬盘。独家冰翼散热架构，性能野兽，办公创作两相宜。",
                    category = "电脑办公",
                    price = 14999.00,
                    originalPrice = 16999.00,
                    rating = 4.9f,
                    salesCount = 530,
                    stock = 15,
                    imageUrl = "ic_launcher_foreground",
                    brand = "星刃 (StarBlade)",
                    isNewArrival = true,
                    specifications = "规格: i9/32G/2T/RTX4090"
                ),
                Product(
                    id = 4,
                    title = "国风新华 刺绣重工休闲连帽卫衣",
                    description = "精选高品质纯棉重磅面料，前胸采用中国传统祥云神龙重工刺绣。版型挺阔，国潮穿搭首选，男女同款。",
                    category = "时尚服饰",
                    price = 399.00,
                    originalPrice = 499.00,
                    rating = 4.7f,
                    salesCount = 21000,
                    stock = 1200,
                    imageUrl = "ic_launcher_foreground",
                    brand = "国风新华",
                    isTodayRecommended = true,
                    specifications = "规格: 黑色龙吟绣 | XL码 | 100%纯棉"
                ),
                Product(
                    id = 5,
                    title = "草本肌因 焕活修护微精华露 150ml",
                    description = "蕴含高浓度二裂酵母及人参冬虫夏草提取物。深度补水、修护肌底、强韧屏障，抵御初老，焕发自然年轻光彩。",
                    category = "美妆护肤",
                    price = 450.00,
                    originalPrice = 580.00,
                    rating = 4.9f,
                    salesCount = 14500,
                    stock = 800,
                    imageUrl = "ic_launcher_foreground",
                    brand = "草本肌因",
                    isFlashSale = true,
                    flashSalePrice = 399.00,
                    specifications = "规格: 150ml标准装 | 各种肤质适用"
                ),
                Product(
                    id = 6,
                    title = "御茶臻选 极品西湖龙井明前茶 250g",
                    description = "产自西湖核心产区，清明前纯手工采摘特级单芽。色泽翠绿，香气浓郁持久，汤色明亮，回甘无穷，高端礼盒装。",
                    category = "食品饮料",
                    price = 880.00,
                    originalPrice = 1200.00,
                    rating = 4.9f,
                    salesCount = 3500,
                    stock = 250,
                    imageUrl = "ic_launcher_foreground",
                    brand = "御茶臻选",
                    isTodayRecommended = true,
                    specifications = "规格: 明前特级 | 250g 奢华礼盒"
                ),
                Product(
                    id = 7,
                    title = "云境 三区护颈天然乳胶阻螨枕",
                    description = "进口93%高纯度泰国天然乳胶，蜂窝透气孔设计。依据人体工学设计三区流线型枕芯，完美贴合头颈，呵护睡眠健康。",
                    category = "家居生活",
                    price = 199.00,
                    originalPrice = 299.00,
                    rating = 4.6f,
                    salesCount = 9800,
                    stock = 450,
                    imageUrl = "ic_launcher_foreground",
                    brand = "云境家居",
                    isLimitedOffer = true,
                    specifications = "规格: 双曲线大号低枕 | 防螨抗菌"
                ),
                Product(
                    id = 8,
                    title = "星速 减震专业空气碳板跑步鞋",
                    description = "全掌高弹碳纤维板配合高弹轻量化中底。极速回弹，超强抗扭矩，透气网面，为马拉松及日常跑步提供极致推力与保护。",
                    category = "运动户外",
                    price = 599.00,
                    originalPrice = 799.00,
                    rating = 4.8f,
                    salesCount = 5400,
                    stock = 300,
                    imageUrl = "ic_launcher_foreground",
                    brand = "星速 (StarSpeed)",
                    isNewArrival = true,
                    specifications = "规格: 烈焰红 | 42码 | 碳板高回弹"
                ),
                Product(
                    id = 9,
                    title = "星享 智能双向变频多门恒温冰箱 550L",
                    description = "双系统三循环，独立精准控温，除菌净味率高达99.9%。超大容量，550升分区存储，纤薄设计可完美嵌入橱柜，家庭保鲜好帮手。",
                    category = "家用电器",
                    price = 4599.00,
                    originalPrice = 4999.00,
                    rating = 4.7f,
                    salesCount = 3100,
                    stock = 50,
                    imageUrl = "ic_launcher_foreground",
                    brand = "星视影音",
                    isLimitedOffer = true,
                    specifications = "规格: 曜石黑 | 550L 智能双循环"
                ),
                Product(
                    id = 10,
                    title = "国潮故宫联名 雕花复古哑光口红礼盒",
                    description = "与故宫国风IP联名定制。采用微雕工艺在口红膏体上雕刻中式龙凤图腾。天鹅绒哑光质地，色泽饱满，显白不脱色，送礼首选。",
                    category = "美妆护肤",
                    price = 199.00,
                    originalPrice = 299.00,
                    rating = 4.9f,
                    salesCount = 18000,
                    stock = 990,
                    imageUrl = "ic_launcher_foreground",
                    brand = "故宫美学",
                    isNewArrival = true,
                    specifications = "规格: 三色经典礼盒装 | 复古哑光"
                ),
                Product(
                    id = 11,
                    title = "星瞳 全自动激光测距扫拖一体机器人",
                    description = "行业领先的 8000Pa 飓风级吸力，配合高频双震动湿拖，能瞬间带走深层顽固污垢。内置激光毫米级雷达精准避障，自动集尘免洗手。",
                    category = "家用电器",
                    price = 2499.00,
                    originalPrice = 2999.00,
                    rating = 4.8f,
                    salesCount = 8900,
                    stock = 140,
                    imageUrl = "ic_launcher_foreground",
                    brand = "星视影音",
                    isFlashSale = true,
                    flashSalePrice = 2199.00,
                    specifications = "规格: 扫拖全能王 | 8000Pa超强大吸力"
                ),
                Product(
                    id = 12,
                    title = "国风新潮 重工提花纯手工汉服大袖衫",
                    description = "纯手工提花面料，精致剪裁。衣摆金线刺绣仙鹤，随风飘逸，古典大气。承袭千年华服之美，尽显君子之风。",
                    category = "时尚服饰",
                    price = 699.00,
                    originalPrice = 899.00,
                    rating = 4.9f,
                    salesCount = 1200,
                    stock = 100,
                    imageUrl = "ic_launcher_foreground",
                    brand = "国风新华",
                    isNewArrival = true,
                    specifications = "规格: 月白仙鹤绣 | L码 | 手工定制版"
                )
            )
            productDao.insertProducts(defaultProducts)

            // Seed default Address
            addressDao.insertAddress(
                Address(
                    id = 1,
                    recipient = "张小星",
                    phone = "188-8888-8888",
                    province = "北京市",
                    city = "北京市",
                    district = "朝阳区",
                    detail = "建国路88号星购总部大厦 18层总裁办公室",
                    isDefault = true
                )
            )
            addressDao.insertAddress(
                Address(
                    id = 2,
                    recipient = "李小购",
                    phone = "139-9999-9999",
                    province = "上海市",
                    city = "上海市",
                    district = "浦东新区",
                    detail = "陆家嘴金融中心金茂大厦 25楼",
                    isDefault = false
                )
            )

            // Seed default notifications
            notificationDao.insertNotification(
                Notification(
                    title = "星购特权：黑金卡专享券已到账！",
                    content = "尊贵的用户，作为星购终身黑金会员，已为您发放「满5000减500」高端数码专享券，快去选购心仪的商品吧！",
                    type = "PROMOTION"
                )
            )
            notificationDao.insertNotification(
                Notification(
                    title = "系统上线通知：星购新一代商城发布",
                    content = "热烈庆祝星购（XingGou）电商客户端与Spring Boot云原生架构后台完美上线！为您提供秒级搜索、全智能客服和极速配送体验！",
                    type = "SYSTEM"
                )
            )

            // Seed default support messages
            supportMessageDao.insertMessage(
                SupportMessage(
                    sender = "AI_BOT",
                    message = "您好！我是星购智能 AI 助理，很高兴为您服务！\n\n您可以问我关于：「正品保证」、「顺丰极速配送」、「黑金卡特权」、「售后申请流程」等问题。请问有什么可以帮您？",
                    timestamp = System.currentTimeMillis() - 10000
                )
            )
        }
    }
}
