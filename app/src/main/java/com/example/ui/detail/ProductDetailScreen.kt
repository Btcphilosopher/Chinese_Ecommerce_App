package com.example.ui.detail

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Product
import com.example.ui.MainViewModel
import com.example.ui.theme.ChineseRed
import com.example.ui.theme.ImperialGold
import com.example.ui.theme.LightGrey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    viewModel: MainViewModel,
    productId: Int,
    onNavigateToCart: () -> Unit,
    onNavigateToCheckoutSingle: (Int) -> Unit,
    onBack: () -> Unit
) {
    val products by viewModel.products.collectAsState()
    val favorites by viewModel.favorites.collectAsState()

    val product = products.find { it.id == productId }

    if (product == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("该商品已被下架或不存在")
        }
        return
    }

    val isFavorite = favorites.any { it.productId == productId }
    var buyQty by remember { mutableStateOf(1) }
    var snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("商品详情", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToCart) {
                        Icon(Icons.Outlined.ShoppingCart, "Cart", tint = Color.DarkGray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 80.dp) // Leave space for bottom action bar
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Product Image Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(LightGrey),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = when (product.category) {
                                "手机数码" -> Icons.Default.PhoneAndroid
                                "家用电器" -> Icons.Default.Tv
                                "电脑办公" -> Icons.Default.Laptop
                                "时尚服饰" -> Icons.Default.Checkroom
                                "美妆护肤" -> Icons.Default.Face
                                "食品饮料" -> Icons.Default.Restaurant
                                "家居生活" -> Icons.Default.Bed
                                "运动户外" -> Icons.Default.DirectionsRun
                                else -> Icons.Default.LocalMall
                            },
                            contentDescription = product.title,
                            tint = ChineseRed.copy(alpha = 0.5f),
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = product.brand,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.DarkGray
                        )
                    }

                    // Floating Video Play Indicator
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .clickable { }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, "Video", tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("商品视频", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Pricing & Title Panel
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "￥",
                                    color = ChineseRed,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${product.price}",
                                    color = ChineseRed,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "￥${product.originalPrice}",
                                    color = Color.LightGray,
                                    fontSize = 14.sp,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                    )
                                )
                            }
                            
                            // Sales count bubble
                            Text(
                                text = "月售 ${product.salesCount} 件",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = product.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 24.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = product.description,
                            fontSize = 13.sp,
                            color = Color.Gray,
                            lineHeight = 18.sp
                        )
                    }
                }

                // Shipping & Guarantee Info
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // shipping
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.LocalShipping, "Shipping", tint = ChineseRed, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("顺丰极速达 (24小时内送达)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("自营仓直发，极速闪电配，支持送货上门", fontSize = 11.sp, color = Color.Gray)
                            }
                        }

                        // security
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Security, "Guarantee", tint = ImperialGold, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("星购官方保真 • 7天无理由退货", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("100% 正品官方授权，支持终身闪电售后保障", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                }

                // Product Specifications (技术参数)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("技术参数与规格", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        SpecificationRow("产品品牌", product.brand)
                        HorizontalDivider(color = LightGrey)
                        SpecificationRow("产品分类", product.category)
                        HorizontalDivider(color = LightGrey)
                        SpecificationRow("产品型号", "星购专属旗舰定制型")
                        HorizontalDivider(color = LightGrey)
                        SpecificationRow("配送渠道", "顺丰生鲜及航空冷链极速派送")
                        HorizontalDivider(color = LightGrey)
                        SpecificationRow("商家名称", product.merchantName)
                        HorizontalDivider(color = LightGrey)
                        SpecificationRow("规格参数", product.specifications)
                        HorizontalDivider(color = LightGrey)
                        SpecificationRow("库存状态", if (product.stock > 0) "顺丰自营北京仓 现货充足 (${product.stock}件)" else "临时缺货")
                    }
                }

                // Customer reviews (用户评价)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("用户综合评价 (100% 好评)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, "Rating", tint = ImperialGold, modifier = Modifier.size(16.dp))
                                Text("${product.rating}", fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(start = 2.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Reviewer 1
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(ChineseRed.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("张", color = ChineseRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("张***军 (黑金卡五星会员)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(
                                text = "真的非常好，顺丰送货极速！下午买的晚上就到了，商品包装极为高级，做工扎实，不愧是星购优选的产品。推荐购买！",
                                fontSize = 12.sp,
                                color = Color.DarkGray,
                                modifier = Modifier.padding(top = 6.dp, start = 32.dp)
                            )
                        }
                    }
                }
            }

            // Bottom Action Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .height(68.dp),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Favorite & Quantity Selector
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        IconButton(onClick = { viewModel.toggleFavorite(productId, isFavorite) }) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) ChineseRed else Color.Gray,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        // Qty selector
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(LightGrey)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            IconButton(
                                onClick = { if (buyQty > 1) buyQty-- },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Remove, "-", modifier = Modifier.size(14.dp))
                            }
                            Text(
                                text = "$buyQty",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            IconButton(
                                onClick = { if (buyQty < product.stock) buyQty++ },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Add, "+", modifier = Modifier.size(14.dp))
                            }
                        }
                    }

                    // Cart Action Button
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                viewModel.addToCart(productId, buyQty)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text("加入购物车", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                viewModel.addToCart(productId, buyQty)
                                onNavigateToCart()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ChineseRed),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text("立即购买", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SpecificationRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.Gray, fontSize = 13.sp)
        Text(text = value, fontWeight = FontWeight.Bold, color = Color.DarkGray, fontSize = 13.sp)
    }
}
