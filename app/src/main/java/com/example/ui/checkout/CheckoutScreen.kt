package com.example.ui.checkout

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Address
import com.example.ui.MainViewModel
import com.example.ui.theme.ChineseRed
import com.example.ui.theme.ImperialGold
import com.example.ui.theme.LightGrey
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: MainViewModel,
    onNavigateToOrders: () -> Unit,
    onBack: () -> Unit
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val products by viewModel.products.collectAsState()
    val selectedItemIds by viewModel.selectedCartItemIds.collectAsState()
    val addresses by viewModel.addresses.collectAsState()

    // Enriched items
    val enrichedItems = cartItems.mapNotNull { item ->
        val product = products.find { it.id == item.productId }
        if (product != null) Pair(item, product) else null
    }.filter { selectedItemIds.contains(it.first.id) }

    val selectedAddress = addresses.firstOrNull { it.isDefault } ?: addresses.firstOrNull()

    // Pricing
    val subtotal = enrichedItems.sumOf { it.second.price * it.first.quantity }
    val couponList = viewModel.availableCoupons
    var activeCoupon by remember { mutableStateOf<com.example.ui.Coupon?>(null) }
    
    // Automatically apply VIP coupon if qualified
    LaunchedEffect(subtotal) {
        if (subtotal >= 5000) {
            activeCoupon = couponList.find { it.code == "XG_VIP_BLACK" }
        } else if (subtotal >= 300.0) {
            activeCoupon = couponList.find { it.code == "XG618_CON" }
        }
    }

    val discount = activeCoupon?.discount ?: 0.0
    val shippingCost = 0.0 // Free shipping for VIP / 618
    val finalTotal = (subtotal - discount).coerceAtLeast(0.0)

    // Payment Processing Sheet State
    var isPaying by remember { mutableStateOf(false) }
    var payProgress by remember { mutableStateOf(0f) }
    var payStatusText by remember { mutableStateOf("连接安全星购收银台...") }
    var paySuccess by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("确认订单", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(LightGrey)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 76.dp),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Address Section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocationOn, "Address", tint = ChineseRed, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                if (selectedAddress != null) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(selectedAddress.recipient, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(selectedAddress.phone, fontSize = 13.sp, color = Color.Gray)
                                        if (selectedAddress.isDefault) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(ChineseRed.copy(alpha = 0.1f))
                                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                            ) {
                                                Text("默认", color = ChineseRed, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                    Text(
                                        text = "${selectedAddress.province}${selectedAddress.city}${selectedAddress.district}${selectedAddress.detail}",
                                        fontSize = 12.sp,
                                        color = Color.DarkGray,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                } else {
                                    Text("无可用地址，请在个人中心添加", color = Color.Gray, fontSize = 14.sp)
                                }
                            }
                            Icon(Icons.Default.ChevronRight, "More", tint = Color.Gray)
                        }
                    }
                }

                // Delivery Options
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocalShipping, "Shipping", tint = ChineseRed)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("配送方式", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("自营顺丰极速空运（保真达）", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                            Text("￥0.00 (尊享免邮)", color = ChineseRed, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                // Ordered products confirmation
                items(enrichedItems) { (cartItem, product) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(LightGrey),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.LocalMall, "Product", tint = ChineseRed.copy(alpha = 0.5f), modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(product.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                Text("数量: ${cartItem.quantity}", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(top = 2.dp))
                            }
                            Text("￥${product.price * cartItem.quantity}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.DarkGray)
                        }
                    }
                }

                // Promo Coupon Info
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CardGiftcard, "Coupon", tint = ImperialGold)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("大额店庆优惠券", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(activeCoupon?.title ?: "无可用优惠券", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                            if (activeCoupon != null) {
                                Text("-￥${activeCoupon!!.discount}", color = ChineseRed, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            } else {
                                Text("暂无立减", color = Color.Gray, fontSize = 13.sp)
                            }
                        }
                    }
                }

                // Billing Detail panel
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("价格明细", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("商品小计", fontSize = 13.sp, color = Color.Gray)
                                Text("￥$subtotal", fontSize = 13.sp, color = Color.DarkGray)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("优惠券立减", fontSize = 13.sp, color = Color.Gray)
                                Text("-￥$discount", fontSize = 13.sp, color = ChineseRed)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("顺丰极速保真运费", fontSize = 13.sp, color = Color.Gray)
                                Text("￥$shippingCost", fontSize = 13.sp, color = Color.DarkGray)
                            }
                            
                            HorizontalDivider(color = LightGrey)

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("实付款", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("￥$finalTotal", color = ChineseRed, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                        }
                    }
                }
            }

            // Bottom action sheet
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .height(64.dp),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row {
                            Text("总额: ", fontSize = 12.sp, color = Color.Gray)
                            Text("￥$finalTotal", color = ChineseRed, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Text("顺丰包邮 • 尊享黑金通道", fontSize = 9.sp, color = Color.LightGray)
                    }

                    Button(
                        onClick = {
                            if (selectedAddress != null) {
                                // Trigger secure payment processing simulation
                                isPaying = true
                                payProgress = 0f
                                paySuccess = false
                                scope.launch {
                                    // Step 1: Connecting
                                    payStatusText = "正在唤醒数字交易证书..."
                                    delay(800)
                                    payProgress = 0.3f
                                    
                                    // Step 2: Verifying biometric / signature
                                    payStatusText = "星购盾® 安全盾安全指纹校对中..."
                                    delay(1000)
                                    payProgress = 0.6f

                                    // Step 3: Paying
                                    payStatusText = "正从银行安全数字渠道扣款..."
                                    delay(1000)
                                    payProgress = 0.9f

                                    // Finish
                                    val mappedItems = enrichedItems.map { Pair(it.second, it.first.quantity) }
                                    viewModel.placeOrder(
                                        items = mappedItems,
                                        address = selectedAddress,
                                        deliveryMethod = "极速顺丰（自营北京主仓发货）",
                                        coupon = activeCoupon,
                                        onSuccess = { _ ->
                                            paySuccess = true
                                            payProgress = 1.0f
                                            payStatusText = "支付成功！已开启极速冷链顺丰航空件"
                                        }
                                    )
                                }
                            }
                        },
                        enabled = selectedAddress != null && enrichedItems.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = ChineseRed),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text("立即安全支付", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            // Secure Cashier Simulation Dialog
            if (isPaying) {
                AlertDialog(
                    onDismissRequest = { },
                    properties = androidx.compose.ui.window.DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, "Secure", tint = ChineseRed)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("星购安全盾数字收银台", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (paySuccess) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF4CAF50)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Check, "Done", tint = Color.White, modifier = Modifier.size(36.dp))
                                }
                            } else {
                                CircularProgressIndicator(
                                    progress = { payProgress },
                                    modifier = Modifier.size(54.dp),
                                    color = ChineseRed,
                                    strokeWidth = 4.dp,
                                    trackColor = Color.LightGray,
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = payStatusText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.DarkGray
                            )
                        }
                    },
                    confirmButton = {
                        if (paySuccess) {
                            Button(
                                onClick = {
                                    isPaying = false
                                    onNavigateToOrders()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                            ) {
                                Text("进入订单中心")
                            }
                        }
                    }
                )
            }
        }
    }
}
