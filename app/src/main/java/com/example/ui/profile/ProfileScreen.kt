package com.example.ui.profile

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Address
import com.example.data.Order
import com.example.ui.MainViewModel
import com.example.ui.theme.ChineseRed
import com.example.ui.theme.ImperialGold
import com.example.ui.theme.LightGrey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    onNavigateToMerchant: () -> Unit
) {
    var activeSubSection by remember { mutableStateOf("profile_home") } // profile_home, orders, addresses, messages, support, tech_docs
    
    val userName by viewModel.userName.collectAsState()
    val userVipLevel by viewModel.userVipLevel.collectAsState()
    val userPoints by viewModel.userPoints.collectAsState()
    val userBalance by viewModel.userBalance.collectAsState()
    
    val orders by viewModel.orders.collectAsState()
    val addresses by viewModel.addresses.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val messages by viewModel.supportMessages.collectAsState()

    // Add address form state
    var showAddressDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = when(activeSubSection) {
                            "profile_home" -> "会员个人中心"
                            "orders" -> "我的订单中心"
                            "addresses" -> "我的收货地址管理"
                            "messages" -> "星购消息推送中心"
                            "support" -> "1对1 智能AI客服助理"
                            else -> "星购技术架构与部署文档"
                        }, 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 16.sp
                    ) 
                },
                navigationIcon = {
                    if (activeSubSection != "profile_home") {
                        IconButton(onClick = { activeSubSection = "profile_home" }) {
                            Icon(Icons.Default.ArrowBack, "Back")
                        }
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
            when (activeSubSection) {
                "profile_home" -> ProfileHomeView(
                    name = userName,
                    vip = userVipLevel,
                    points = userPoints,
                    balance = userBalance,
                    onSubSectionClick = { activeSubSection = it },
                    onNavigateToMerchant = onNavigateToMerchant
                )
                "orders" -> OrdersView(orders, viewModel)
                "addresses" -> AddressesView(addresses, viewModel) { showAddressDialog = true }
                "messages" -> MessagesView(notifications)
                "support" -> SupportView(messages, viewModel)
                "tech_docs" -> TechDocsView()
            }
        }

        // Add Address Dialog
        if (showAddressDialog) {
            AddAddressDialog(
                onDismiss = { showAddressDialog = false },
                onConfirm = { rec, ph, prov, city, dist, det, def ->
                    viewModel.addShippingAddress(rec, ph, prov, city, dist, det, def)
                    showAddressDialog = false
                }
            )
        }
    }
}

// 1. PROFILE HOME / DASHBOARD VIEW
@Composable
fun ProfileHomeView(
    name: String,
    vip: String,
    points: Int,
    balance: Double,
    onSubSectionClick: (String) -> Unit,
    onNavigateToMerchant: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // User Info Golden Banner (High-end theme)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(ImperialGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, "Avatar", tint = ImperialGold, modifier = Modifier.size(32.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(ImperialGold)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(vip, color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    
                    Icon(Icons.Default.QrCode, "QR Code", tint = ImperialGold, modifier = Modifier.size(24.dp))
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("电子钱包余额 (￥)", color = Color.Gray, fontSize = 11.sp)
                        Text("￥${String.format("%,.2f", balance)}", color = ImperialGold, fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("当前积分", color = Color.Gray, fontSize = 11.sp)
                        Text("$points", color = ImperialGold, fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }

        // Nav Links Grid List (Material 3 style list items with spacing)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column {
                ProfileMenuItem(Icons.Default.LocalMall, "我的订单中心", "查看顺丰发货订单及物流轨迹") { onSubSectionClick("orders") }
                HorizontalDivider(color = LightGrey)
                ProfileMenuItem(Icons.Outlined.LocationOn, "收货地址簿", "管理我的多个家庭与工作收货地址") { onSubSectionClick("addresses") }
                HorizontalDivider(color = LightGrey)
                ProfileMenuItem(Icons.Outlined.Notifications, "消息推送中心", "系统通知、发货消息、营销券折扣提示") { onSubSectionClick("messages") }
                HorizontalDivider(color = LightGrey)
                ProfileMenuItem(Icons.Outlined.Chat, "智能 AI 1对1 客服", "7x24小时全自动解答您的售后、真伪问题") { onSubSectionClick("support") }
            }
        }

        // Technical Architecture Viewer Menu Item
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            ProfileMenuItem(
                icon = Icons.Outlined.Info,
                title = "星购技术架构与部署文档",
                subtitle = "PostgreSQL设计、Spring Boot接口、K8s部署配置",
                onClick = { onSubSectionClick("tech_docs") }
            )
        }

        // Switch to Merchant Backoffice Button (China digital economy style)
        Button(
            onClick = onNavigateToMerchant,
            colors = ButtonDefaults.buttonColors(containerColor = ChineseRed),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(Icons.Default.Storefront, "Merchant")
            Spacer(modifier = Modifier.width(8.dp))
            Text("切换至星购商家后台(运营管理端)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(ChineseRed.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, title, tint = ChineseRed, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(subtitle, fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(top = 2.dp))
            }
        }
        Icon(Icons.Default.ChevronRight, "More", tint = Color.LightGray)
    }
}

// 2. ORDERS VIEW (Contains Refund, Logistics Timeline, and Delivery status details)
@Composable
fun OrdersView(orders: List<Order>, viewModel: MainViewModel) {
    if (orders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("您还没有在星购下过单哦，去商城看看吧", color = Color.Gray, fontSize = 13.sp)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(orders) { order ->
            var showLogisticsTimeline by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Order header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("订单号: ${order.id}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.DarkGray)
                        Text(
                            text = when (order.status) {
                                "PAID" -> "待出库"
                                "SHIPPED" -> "派送中 (顺丰速运)"
                                "DELIVERED" -> "已收货完成"
                                "CANCELLED" -> "订单已取消"
                                "REFUND_PENDING" -> "退款审核中"
                                else -> "已全额退款"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = when (order.status) {
                                "PAID" -> Color(0xFFFF9800)
                                "SHIPPED" -> Color(0xFF1E88E5)
                                "DELIVERED" -> Color(0xFF4CAF50)
                                else -> Color.Gray
                            }
                        )
                    }

                    HorizontalDivider(color = LightGrey, modifier = Modifier.padding(vertical = 10.dp))

                    Text("实付款: ￥${order.totalPrice}", fontWeight = FontWeight.Bold, color = ChineseRed, fontSize = 14.sp)
                    Text("配送地址: ${order.addressDetail}", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))

                    Spacer(modifier = Modifier.height(12.dp))

                    // Buttons/Actions
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (order.status == "SHIPPED") {
                            Button(
                                onClick = { showLogisticsTimeline = !showLogisticsTimeline },
                                colors = ButtonDefaults.buttonColors(containerColor = ImperialGold),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.weight(1f).height(34.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("顺丰物流追踪", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { viewModel.confirmReceipt(order) },
                                colors = ButtonDefaults.buttonColors(containerColor = ChineseRed),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.weight(1f).height(34.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("确认收货", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (order.status == "DELIVERED" || order.status == "PAID" || order.status == "SHIPPED") {
                            Button(
                                onClick = { viewModel.requestRefund(order) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.height(34.dp).weight(1f),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("申请极速退款", fontSize = 11.sp, color = Color.DarkGray, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Interactive Logistics timeline drawer (物流追踪 timeline)
                    AnimatedVisibility(visible = showLogisticsTimeline) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                                .background(LightGrey)
                                .clip(RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("自营顺丰极速专线 物流轨迹：", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ChineseRed)
                            
                            // Checkpoints timeline
                            LogisticsPointItem("16:00", "预计今日送达。派送员：刘师傅（138-1234-5678）已开启一对一黑金专配。")
                            LogisticsPointItem("14:00", "包裹已飞抵北京市核心转运中心，并完成智能数字化分流。")
                            LogisticsPointItem("10:30", "包裹已被顺丰极速自营专递员揽件完毕，航班已备舱。")
                            LogisticsPointItem("10:00", "买家下单成功，星购智能自营北京主仓已完成分拣及数字打包。")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LogisticsPointItem(time: String, content: String) {
    Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
        Text(time, fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.DarkGray, modifier = Modifier.width(44.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(ChineseRed)
                .padding(top = 4.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(content, fontSize = 11.sp, color = Color.Gray, lineHeight = 14.sp)
    }
}

// 3. ADDRESSES VIEW (Addresses list with adding)
@Composable
fun AddressesView(addresses: List<Address>, viewModel: MainViewModel, onAddAddressClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize().padding(bottom = 60.dp)
        ) {
            item {
                Text("已保存收货地址 (${addresses.size}个)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            items(addresses) { address ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, "Location", tint = ChineseRed, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(address.recipient, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(address.phone, fontSize = 12.sp, color = Color.Gray)
                            }
                            Text(
                                text = "${address.province}${address.city}${address.district}${address.detail}",
                                fontSize = 11.sp,
                                color = Color.DarkGray,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        IconButton(onClick = { viewModel.removeAddress(address) }) {
                            Icon(Icons.Default.DeleteOutline, "Remove", tint = ChineseRed)
                        }
                    }
                }
            }
        }

        Button(
            onClick = onAddAddressClick,
            colors = ButtonDefaults.buttonColors(containerColor = ChineseRed),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
                .height(44.dp)
        ) {
            Text("新增收货地址", fontWeight = FontWeight.Bold)
        }
    }
}

// 4. MESSAGES / NOTIFICATIONS VIEW (System and order notification notifications)
@Composable
fun MessagesView(notifications: List<com.example.data.Notification>) {
    if (notifications.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("暂无系统消息通知", color = Color.Gray, fontSize = 13.sp)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(notifications) { notif ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    when (notif.type) {
                                        "PROMOTION" -> ImperialGold.copy(alpha = 0.15f)
                                        else -> ChineseRed.copy(alpha = 0.08f)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (notif.type) {
                                    "PROMOTION" -> Icons.Default.ConfirmationNumber
                                    else -> Icons.Default.Notifications
                                },
                                contentDescription = notif.type,
                                tint = if (notif.type == "PROMOTION") ImperialGold else ChineseRed,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(notif.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Text(
                        text = notif.content,
                        fontSize = 12.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(top = 10.dp, start = 44.dp),
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

// 5. CHAT SUPPORT VIEW (Includes user input + intelligent AI support response!)
@Composable
fun SupportView(messages: List<com.example.data.SupportMessage>, viewModel: MainViewModel) {
    var textInput by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(LightGrey)) {
        // Chat area
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { msg ->
                val isUser = msg.sender == "USER"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    if (!isUser) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(ChineseRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("AI", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Card(
                        modifier = Modifier.widthIn(max = 260.dp),
                        shape = RoundedCornerShape(
                            topStart = 12.dp,
                            topEnd = 12.dp,
                            bottomStart = if (isUser) 12.dp else 0.dp,
                            bottomEnd = if (isUser) 0.dp else 12.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUser) ChineseRed else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Text(
                            text = msg.message,
                            fontSize = 13.sp,
                            color = if (isUser) Color.White else Color.DarkGray,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    if (isUser) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.Gray.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("我", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Action input bar
        Surface(
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shadowElevation = 4.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    modifier = Modifier.weight(1f).height(44.dp),
                    placeholder = { Text("输入您的疑问，如「顺丰极速」", fontSize = 12.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (textInput.isNotEmpty()) {
                            viewModel.sendUserMessage(textInput)
                            textInput = ""
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ChineseRed)
                ) {
                    Icon(Icons.Default.Send, "Send", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

// 6. ARCHITECTURE & CODE DOCUMENTATION VIEWER
@Composable
fun TechDocsView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("星购（XingGou）云原生系统架构指标", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ChineseRed)

        Text(
            text = "为了满足高并发、海量货品及高可用要求，星购后端基于 Spring Boot 3.x 及 K8s 微服务开发，以下为系统提供的 PostgreSQL 表设计和部署规范：",
            fontSize = 12.sp,
            color = Color.DarkGray,
            lineHeight = 16.sp
        )

        // DB Design
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Storage, "Database", tint = ChineseRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("PostgreSQL 核心关系型数据表设计", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))
                
                TechDocCodeBlock(
                    "CREATE TABLE users (\n" +
                    "    id SERIAL PRIMARY KEY,\n" +
                    "    username VARCHAR(50) UNIQUE NOT NULL,\n" +
                    "    password_hash VARCHAR(255) NOT NULL,\n" +
                    "    vip_level INT DEFAULT 0,\n" +
                    "    points INT DEFAULT 0,\n" +
                    "    balance DECIMAL(12, 2) DEFAULT 0.00\n" +
                    ");\n\n" +
                    "CREATE TABLE products (\n" +
                    "    id SERIAL PRIMARY KEY,\n" +
                    "    title VARCHAR(255) NOT NULL,\n" +
                    "    price DECIMAL(10, 2) NOT NULL,\n" +
                    "    stock INT NOT NULL DEFAULT 0,\n" +
                    "    category_name VARCHAR(50)\n" +
                    ");\n\n" +
                    "CREATE TABLE orders (\n" +
                    "    order_id VARCHAR(50) PRIMARY KEY,\n" +
                    "    user_id INT REFERENCES users(id),\n" +
                    "    total_price DECIMAL(12, 2),\n" +
                    "    status VARCHAR(20) -- PAID, SHIPPED, DELIVERED\n" +
                    ");"
                )
            }
        }

        // Docker & K8s deployment
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Cloud, "Kubernetes", tint = ImperialGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Docker 与 Kubernetes (K8s) 云部署", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                TechDocCodeBlock(
                    "apiVersion: apps/v1\n" +
                    "kind: Deployment\n" +
                    "metadata:\n" +
                    "  name: xinggou-backend\n" +
                    "spec:\n" +
                    "  replicas: 3\n" +
                    "  selector:\n" +
                    "    matchLabels:\n" +
                    "      app: xinggou-backend\n" +
                    "  template:\n" +
                    "    metadata:\n" +
                    "      labels:\n" +
                    "        app: xinggou-backend\n" +
                    "    spec:\n" +
                    "      containers:\n" +
                    "      - name: backend\n" +
                    "        image: xinggou-api:latest\n" +
                    "        ports:\n" +
                    "        - containerPort: 8080"
                )
            }
        }

        // Spring Boot REST APIs
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Code, "REST API", tint = Color.Blue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Spring Boot REST 核心控制器", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                TechDocCodeBlock(
                    "@RestController\n" +
                    "@RequestMapping(\"/api/v1/products\")\n" +
                    "public class ProductController {\n" +
                    "    @Autowired\n" +
                    "    private ProductRepository productRepo;\n\n" +
                    "    @GetMapping\n" +
                    "    public List<Product> getAll() {\n" +
                    "        return productRepo.findAll();\n" +
                    "    }\n" +
                    "}"
                )
            }
        }
    }
}

@Composable
fun TechDocCodeBlock(code: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color.Black)
            .padding(12.dp)
    ) {
        Text(
            text = code,
            color = Color(0xFF4CAF50),
            fontSize = 11.sp,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
        )
    }
}

// Dialog Forms
@Composable
fun AddAddressDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String, String, Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var province by remember { mutableStateOf("北京市") }
    var city by remember { mutableStateOf("北京市") }
    var dist by remember { mutableStateOf("朝阳区") }
    var detail by remember { mutableStateOf("") }
    var default by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("新增收货地址", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("收货人姓名") })
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("联系电话") })
                OutlinedTextField(value = province, onValueChange = { province = it }, label = { Text("省份") })
                OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("城市") })
                OutlinedTextField(value = dist, onValueChange = { dist = it }, label = { Text("区县") })
                OutlinedTextField(value = detail, onValueChange = { detail = it }, label = { Text("详细收货地址") })
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = default, onCheckedChange = { default = it })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("设为默认收货地址", fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotEmpty() && phone.isNotEmpty() && detail.isNotEmpty()) {
                        onConfirm(name, phone, province, city, dist, detail, default)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ChineseRed)
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消", color = Color.Gray)
            }
        }
    )
}
