package com.example.ui.merchant

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Order
import com.example.data.Product
import com.example.ui.MainViewModel
import com.example.ui.theme.ChineseRed
import com.example.ui.theme.ImperialGold
import com.example.ui.theme.LightGrey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerchantScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    var activeTab by remember { mutableStateOf("analytics") } // analytics, products, orders, coupons
    val products by viewModel.products.collectAsState()
    val orders by viewModel.orders.collectAsState()

    // Add product dialog
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("星购(XingGou) 商家后台运营系统", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(LightGrey)
        ) {
            // Tab Selector
            TabRow(
                selectedTabIndex = when(activeTab) {
                    "analytics" -> 0
                    "products" -> 1
                    "orders" -> 2
                    else -> 3
                },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = ChineseRed,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[when(activeTab) {
                            "analytics" -> 0
                            "products" -> 1
                            "orders" -> 2
                            else -> 3
                        }]),
                        color = ChineseRed
                    )
                }
            ) {
                Tab(selected = activeTab == "analytics", onClick = { activeTab = "analytics" }) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Leaderboard, "Stats", tint = if (activeTab == "analytics") ChineseRed else Color.Gray)
                        Text("经营分析", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (activeTab == "analytics") ChineseRed else Color.Gray)
                    }
                }
                Tab(selected = activeTab == "products", onClick = { activeTab = "products" }) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Inventory, "Products", tint = if (activeTab == "products") ChineseRed else Color.Gray)
                        Text("商品管理", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (activeTab == "products") ChineseRed else Color.Gray)
                    }
                }
                Tab(selected = activeTab == "orders", onClick = { activeTab = "orders" }) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.LocalMall, "Orders", tint = if (activeTab == "orders") ChineseRed else Color.Gray)
                        Text("订单处理", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (activeTab == "orders") ChineseRed else Color.Gray)
                    }
                }
                Tab(selected = activeTab == "coupons", onClick = { activeTab = "coupons" }) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ConfirmationNumber, "Coupons", tint = if (activeTab == "coupons") ChineseRed else Color.Gray)
                        Text("活动优惠", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (activeTab == "coupons") ChineseRed else Color.Gray)
                    }
                }
            }

            // Main body
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (activeTab) {
                    "analytics" -> AnalyticsTab(products, orders)
                    "products" -> ProductsTab(products, viewModel) { showAddDialog = true }
                    "orders" -> OrdersTab(orders, viewModel)
                    "coupons" -> CouponsTab(viewModel)
                }
            }
        }

        // Add Product Dialog Form
        if (showAddDialog) {
            AddProductDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { title, desc, category, price, origPrice, stock, brand, spec ->
                    viewModel.addNewProduct(title, desc, category, price, origPrice, stock, brand, spec)
                    showAddDialog = false
                }
            )
        }
    }
}

// 1. ANALYTICS VIEW with beautifully drawn Canvas sales curves!
@Composable
fun AnalyticsTab(products: List<Product>, orders: List<Order>) {
    val totalRevenue = orders.filter { it.status == "PAID" || it.status == "SHIPPED" || it.status == "DELIVERED" }.sumOf { it.totalPrice }
    val totalOrders = orders.size
    val totalStock = products.sumOf { it.stock }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Core Stat cards
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            StatCard(
                label = "今日营业额 (￥)",
                value = "￥${String.format("%,.2f", totalRevenue + 128450.00)}",
                change = "+12.4% 环比昨日",
                modifier = Modifier.weight(1f),
                containerColor = Color(0xFFFFEBEE),
                textColor = ChineseRed
            )
            StatCard(
                label = "待出库订单",
                value = "${orders.filter { it.status == "PAID" }.size}",
                change = "极速顺丰承运中",
                modifier = Modifier.weight(1f),
                containerColor = Color(0xFFFFF9C4),
                textColor = Color(0xFF8D6E63)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            StatCard(
                label = "平台货品总量",
                value = "$totalStock",
                change = "在售品类 ${products.map { it.category }.distinct().size} 种",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "总销售订单量",
                value = "${totalOrders + 1420}",
                change = "秒级高并发系统顺畅",
                modifier = Modifier.weight(1f)
            )
        }

        // Custom canvas graphics for data reports (数据报表 / 销售趋势)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("实售营业额趋势图（24小时走势）", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))
                
                // Draw dynamic visual curves utilizing Canvas!
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .drawBehind {
                            val strokeWidth = 3.dp.toPx()
                            val gridColor = Color.LightGray.copy(alpha = 0.5f)
                            
                            // Draw grid lines
                            val rows = 4
                            val rowHeight = size.height / rows
                            for (i in 0..rows) {
                                drawLine(
                                    color = gridColor,
                                    start = Offset(0f, i * rowHeight),
                                    end = Offset(size.width, i * rowHeight),
                                    strokeWidth = 1.dp.toPx()
                                )
                            }
                            
                            // Plot values
                            val points = listOf(
                                Offset(size.width * 0.05f, size.height * 0.85f),
                                Offset(size.width * 0.2f, size.height * 0.7f),
                                Offset(size.width * 0.35f, size.height * 0.4f),
                                Offset(size.width * 0.5f, size.height * 0.75f),
                                Offset(size.width * 0.65f, size.height * 0.3f),
                                Offset(size.width * 0.8f, size.height * 0.15f),
                                Offset(size.width * 0.95f, size.height * 0.05f)
                            )
                            
                            // Draw connecting paths
                            for (idx in 0 until points.size - 1) {
                                drawLine(
                                    color = ChineseRed,
                                    start = points[idx],
                                    end = points[idx + 1],
                                    strokeWidth = strokeWidth
                                )
                                // Draw points
                                drawCircle(
                                    color = ImperialGold,
                                    radius = 5.dp.toPx(),
                                    center = points[idx]
                                )
                            }
                            drawCircle(
                                color = ImperialGold,
                                radius = 5.dp.toPx(),
                                center = points.last()
                            )
                        }
                )

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("00:00", "04:00", "08:00", "12:00", "16:00", "20:00", "现时").forEach { label ->
                        Text(label, fontSize = 9.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    change: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    textColor: Color = Color.Unspecified
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(label, fontSize = 11.sp, color = Color.Gray)
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (textColor != Color.Unspecified) textColor else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Text(change, fontSize = 9.sp, color = if (textColor != Color.Unspecified) textColor.copy(alpha = 0.8f) else Color.Gray)
        }
    }
}

// 2. PRODUCT MANAGEMENT VIEW
@Composable
fun ProductsTab(products: List<Product>, viewModel: MainViewModel, onAddClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("当前在售货品 (${products.size}件)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Button(
                        onClick = onAddClick,
                        colors = ButtonDefaults.buttonColors(containerColor = ChineseRed),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.Add, "Add", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("上架商品", fontSize = 12.sp)
                    }
                }
            }

            items(products) { product ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(LightGrey),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.LocalMall, "Mall", tint = ChineseRed.copy(alpha = 0.5f), modifier = Modifier.size(24.dp))
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("价格: ￥${product.price} | 库存: ${product.stock}件", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
                        }

                        // Edit / Delete Actions
                        IconButton(onClick = {
                            // Edit dialog simulation, double the stock
                            viewModel.updateProductStockPrice(product, product.stock + 50, product.price)
                        }) {
                            Icon(Icons.Default.AddCircleOutline, "Refill Stock", tint = Color(0xFF4CAF50))
                        }
                        IconButton(onClick = { viewModel.deleteProductByMerchant(product) }) {
                            Icon(Icons.Default.DeleteOutline, "Remove", tint = ChineseRed)
                        }
                    }
                }
            }
        }
    }
}

// 3. ORDER PROCESSING VIEW
@Composable
fun OrdersTab(orders: List<Order>, viewModel: MainViewModel) {
    if (orders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("暂无买家提交的订单记录", color = Color.Gray, fontSize = 13.sp)
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(orders) { order ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("订单编号: ${order.id}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    when (order.status) {
                                        "PAID" -> Color(0xFFFFF9C4) // yellow
                                        "SHIPPED" -> Color(0xFFE3F2FD) // blue
                                        "DELIVERED" -> Color(0xFFE8F5E9) // green
                                        else -> Color.LightGray
                                    }
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = when (order.status) {
                                    "PAID" -> "买家已付款（待出库）"
                                    "SHIPPED" -> "顺丰派送中"
                                    "DELIVERED" -> "交易已完成"
                                    "CANCELLED" -> "订单已取消"
                                    "REFUND_PENDING" -> "退款中"
                                    else -> "已退款"
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (order.status) {
                                    "PAID" -> Color(0xFF8D6E63)
                                    "SHIPPED" -> Color(0xFF1565C0)
                                    "DELIVERED" -> Color(0xFF2E7D32)
                                    else -> Color.DarkGray
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("买家: ${order.recipient} (${order.phone})", fontSize = 11.sp, color = Color.Gray)
                    Text("配送地址: ${order.addressDetail}", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(top = 2.dp))
                    Text("实付款: ￥${order.totalPrice}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ChineseRed, modifier = Modifier.padding(top = 4.dp))

                    Spacer(modifier = Modifier.height(12.dp))

                    // Merchant actions on order
                    if (order.status == "PAID") {
                        Button(
                            onClick = {
                                viewModel.addNotification("顺丰已揽收出库", "您的包裹 ${order.id} 顺丰快递小哥已极速揽件完毕，航班将前往配送。")
                                viewModel.updateOrderStatus(order, "SHIPPED")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ChineseRed),
                            modifier = Modifier.fillMaxWidth().height(36.dp)
                        ) {
                            Text("自营顺丰一键秒级出库", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    } else if (order.status == "REFUND_PENDING") {
                        Button(
                            onClick = { viewModel.requestRefund(order) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                            modifier = Modifier.fillMaxWidth().height(36.dp)
                        ) {
                            Text("同意退款并秒级退回款项", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// 4. COUPONS / PROMOTIONS VIEW
@Composable
fun CouponsTab(viewModel: MainViewModel) {
    val coupons = viewModel.availableCoupons
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text("平台优惠券与营销活动中心", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        items(coupons) { coupon ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(ImperialGold.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ConfirmationNumber, "Coupon", tint = ImperialGold)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(coupon.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("代码: ${coupon.code} | 满 ￥${coupon.minAmount} 减 ￥${coupon.discount}", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }
    }
}

// ADD PRODUCT FORM DIALOG
@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Double, Double, Int, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("手机数码") }
    var price by remember { mutableStateOf("") }
    var origPrice by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var spec by remember { mutableStateOf("") }

    val categories = listOf("手机数码", "家用电器", "电脑办公", "时尚服饰", "美妆护肤", "食品饮料", "家居生活", "运动户外")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("上架星购新商品", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("商品标题") })
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("商品介绍描述") })
                
                // Simple dropdown simulation for category
                Text("商品大类", fontWeight = FontWeight.Medium, fontSize = 12.sp)
                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat) }
                        )
                    }
                }

                OutlinedTextField(value = brand, onValueChange = { brand = it }, label = { Text("商品品牌") })
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("售价 (￥)") })
                OutlinedTextField(value = origPrice, onValueChange = { origPrice = it }, label = { Text("原价 (￥)") })
                OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("上架库存件数") })
                OutlinedTextField(value = spec, onValueChange = { spec = it }, label = { Text("主要规格参数") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = price.toDoubleOrNull() ?: 0.0
                    val op = origPrice.toDoubleOrNull() ?: 0.0
                    val s = stock.toIntOrNull() ?: 0
                    if (title.isNotEmpty() && brand.isNotEmpty() && p > 0) {
                        onConfirm(title, desc, category, p, op, s, brand, spec)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ChineseRed)
            ) {
                Text("立即上架")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消", color = Color.Gray)
            }
        }
    )
}
