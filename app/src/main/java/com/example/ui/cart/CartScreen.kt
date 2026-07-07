package com.example.ui.cart

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CartItem
import com.example.ui.MainViewModel
import com.example.ui.theme.ChineseRed
import com.example.ui.theme.LightGrey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: MainViewModel,
    onNavigateToCheckout: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val products by viewModel.products.collectAsState()
    val selectedItemIds by viewModel.selectedCartItemIds.collectAsState()

    // Map cart items to actual product references
    val enrichedCartItems = cartItems.mapNotNull { item ->
        val product = products.find { it.id == item.productId }
        if (product != null) Pair(item, product) else null
    }

    val selectedItems = enrichedCartItems.filter { selectedItemIds.contains(it.first.id) }
    val totalPrice = selectedItems.sumOf { it.second.price * it.first.quantity }
    val totalCount = selectedItems.sumOf { it.first.quantity }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("我的购物车", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                actions = {
                    if (cartItems.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearCart() }) {
                            Icon(Icons.Outlined.Delete, "Delete All", tint = Color.Gray)
                        }
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
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (enrichedCartItems.isEmpty()) {
                // Empty Cart State
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Empty",
                            tint = Color.LightGray,
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("购物车空空如也，快去挑一些宝贝吧", color = Color.Gray, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = onNavigateToHome,
                            colors = ButtonDefaults.buttonColors(containerColor = ChineseRed),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("去挑选宝贝", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize().padding(bottom = 76.dp)) {
                    // Promotional Banner (满减活动)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = ChineseRed.copy(alpha = 0.05f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(ChineseRed)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("满减活动", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "星购周年庆：自营商品满5000元，立减500元，结账自动叠加",
                                fontSize = 11.sp,
                                color = ChineseRed,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Cart item list
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().weight(1f),
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(enrichedCartItems) { (cartItem, product) ->
                            val isSelected = selectedItemIds.contains(cartItem.id)
                            
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Select checkbox
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = { viewModel.toggleCartSelection(cartItem.id) }
                                    )

                                    // Product icon
                                    Box(
                                        modifier = Modifier
                                            .size(70.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(LightGrey),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocalMall,
                                            contentDescription = product.title,
                                            tint = ChineseRed.copy(alpha = 0.5f),
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = product.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "品牌: ${product.brand}",
                                            color = Color.Gray,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "￥${product.price}",
                                                color = ChineseRed,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )

                                            // Qty Controller
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(LightGrey)
                                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                                            ) {
                                                IconButton(
                                                    onClick = { viewModel.updateCartQuantity(cartItem, cartItem.quantity - 1) },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(Icons.Default.Remove, "-", modifier = Modifier.size(12.dp))
                                                }
                                                Text(
                                                    text = "${cartItem.quantity}",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp)
                                                )
                                                IconButton(
                                                    onClick = { viewModel.updateCartQuantity(cartItem, cartItem.quantity + 1) },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(Icons.Default.Add, "+", modifier = Modifier.size(12.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Bottom Checkout bar
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = selectedItemIds.size == enrichedCartItems.size,
                                onCheckedChange = { viewModel.toggleAllCartSelection(cartItems) }
                            )
                            Text("全选", fontSize = 13.sp)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(end = 12.dp)) {
                                Row {
                                    Text("合计: ", fontSize = 12.sp, color = Color.Gray)
                                    Text("￥$totalPrice", color = ChineseRed, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                                Text("不含运费 • 顺丰极速", fontSize = 9.sp, color = Color.LightGray)
                            }

                            Button(
                                onClick = onNavigateToCheckout,
                                enabled = selectedItems.isNotEmpty(),
                                colors = ButtonDefaults.buttonColors(containerColor = ChineseRed),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.height(40.dp)
                            ) {
                                Text("结算 ($totalCount)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
