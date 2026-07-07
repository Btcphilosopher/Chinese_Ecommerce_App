package com.example.ui.home

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.Product
import com.example.ui.MainViewModel
import com.example.ui.theme.ChineseRed
import com.example.ui.theme.LightGrey
import com.example.ui.theme.ImperialGold
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToProduct: (Int) -> Unit,
    onNavigateToSearch: (String) -> Unit,
    onNavigateToCategory: (String) -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val products by viewModel.products.collectAsState()
    val searchVal = remember { mutableStateOf("") }
    
    // Simulate countdown timer
    var timerText by remember { mutableStateOf("01:45:23") }
    LaunchedEffect(Unit) {
        var seconds = 6323
        while (seconds > 0) {
            delay(1000)
            seconds--
            val h = seconds / 3600
            val m = (seconds % 3600) / 60
            val s = seconds % 60
            timerText = String.format("%02d:%02d:%02d", h, m, s)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { onNavigateToSearch("") }
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "星耀 90 Pro 至臻版 / 故宫口红",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToCart) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingCart,
                            contentDescription = "Cart",
                            tint = ChineseRed
                        )
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.DarkGray
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // Sliding Hero Banner using local generated image with 24dp bento corners
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(24.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_banner_1783421460919),
                    contentDescription = "Hero Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Linear gradient fade over banner
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f)),
                                startY = 100f
                            )
                        )
                )
                Text(
                    text = "星购科技生活节 • 狂狂惠开启",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                )
            }

            // Quick Category Navigator styled with elegant Bento background styles
            val categories = listOf(
                CategoryItem("手机数码", Icons.Default.PhoneAndroid, "手机数码"),
                CategoryItem("家用电器", Icons.Default.Tv, "家用电器"),
                CategoryItem("电脑办公", Icons.Default.Laptop, "电脑办公"),
                CategoryItem("时尚服饰", Icons.Default.Checkroom, "时尚服饰"),
                CategoryItem("美妆护肤", Icons.Default.Face, "美妆护肤"),
                CategoryItem("食品饮料", Icons.Default.Restaurant, "食品饮料"),
                CategoryItem("家居生活", Icons.Default.Bed, "家居生活"),
                CategoryItem("运动户外", Icons.Default.DirectionsRun, "运动户外")
            )
            
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                items(categories) { item ->
                    val (bgColor, iconTint) = when (item.label) {
                        "手机数码" -> Pair(com.example.ui.theme.BentoRed50, Color(0xFFE60012))
                        "家用电器" -> Pair(com.example.ui.theme.BentoOrange50, Color(0xFFEA580C))
                        "电脑办公" -> Pair(com.example.ui.theme.BentoBlue50, Color(0xFF2563EB))
                        "时尚服饰" -> Pair(com.example.ui.theme.BentoPink50, Color(0xFFDB2777))
                        "美妆护肤" -> Pair(com.example.ui.theme.BentoPurple50, Color(0xFF9333EA))
                        "食品饮料" -> Pair(com.example.ui.theme.BentoOrange50, Color(0xFFD97706))
                        "家居生活" -> Pair(com.example.ui.theme.BentoBlue50, Color(0xFF0D9488))
                        "运动户外" -> Pair(com.example.ui.theme.BentoRed50, Color(0xFFDC2626))
                        else -> Pair(com.example.ui.theme.BentoSlate100, Color(0xFF475569))
                    }
                    Column(
                        modifier = Modifier
                            .clickable { onNavigateToCategory(item.route) }
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(bgColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = iconTint,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = item.label.takeLast(2),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = com.example.ui.theme.BentoSlate800
                        )
                    }
                }
            }

            // Beautifully integrated Bento Grid Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val flashSaleItems = products.filter { it.isFlashSale }
                val topFlashSale = flashSaleItems.firstOrNull()
                
                // Left tall card: Flash Sale (Tall)
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(280.dp)
                        .clickable { 
                            if (topFlashSale != null) onNavigateToProduct(topFlashSale.id) 
                            else onNavigateToCategory("手机数码")
                        },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFFE60012), Color(0xFFFF4D4D))
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.FlashOn,
                                            contentDescription = "Flash",
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "限时秒杀",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "超级补贴\n低至5折",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 22.sp
                                )
                            }
                            
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.White.copy(alpha = 0.15f))
                                        .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)), RoundedCornerShape(16.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (topFlashSale?.category) {
                                            "手机数码" -> Icons.Default.PhoneAndroid
                                            "家用电器" -> Icons.Default.Tv
                                            "电脑办公" -> Icons.Default.Laptop
                                            "时尚服饰" -> Icons.Default.Checkroom
                                            "美妆护肤" -> Icons.Default.Face
                                            "食品饮料" -> Icons.Default.Restaurant
                                            "家居生活" -> Icons.Default.Bed
                                            "运动户外" -> Icons.Default.DirectionsRun
                                            else -> Icons.Default.Headphones
                                        },
                                        contentDescription = "Featured Product",
                                        tint = Color.White,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "¥${topFlashSale?.flashSalePrice ?: 1299}",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "¥${topFlashSale?.originalPrice ?: 2499}",
                                        color = Color.White.copy(alpha = 0.6f),
                                        fontSize = 10.sp,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Right Column: New Arrivals (Wide) + VIP Member (Wide)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(280.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val recommendedItems = products.filter { it.isTodayRecommended }
                    val topNewArrival = recommendedItems.firstOrNull { !it.isFlashSale } ?: products.firstOrNull()
                    
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clickable { 
                                if (topNewArrival != null) onNavigateToProduct(topNewArrival.id)
                                else onNavigateToCategory("手机数码")
                            },
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.BentoSlate900)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "新品上市",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "NEW",
                                        color = Color.White,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Watch,
                                        contentDescription = "Watch",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = topNewArrival?.title?.take(5) ?: "智能系列",
                                        color = com.example.ui.theme.BentoSlate400,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "立即抢购",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                    
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clickable { onNavigateToProfile() },
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, com.example.ui.theme.BentoVipGold.copy(alpha = 0.2f)),
                        colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.BentoVipBg)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "星购会员",
                                    color = com.example.ui.theme.BentoVipText,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(RoundedCornerShape(9.dp))
                                        .background(com.example.ui.theme.BentoVipGold),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "★",
                                        color = Color.White,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            
                            Text(
                                text = "尊享10大权益 & 顺丰包邮",
                                color = com.example.ui.theme.BentoVipText.copy(alpha = 0.8f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }

            // Recommendations (Bottom Long)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { onNavigateToSearch("") },
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, com.example.ui.theme.BentoSlate100),
                colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.BentoSlate50)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "猜你喜欢",
                            color = com.example.ui.theme.BentoSlate900,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "根据您的搜索和浏览为您推荐",
                            color = com.example.ui.theme.BentoSlate500,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.White)
                                    .border(BorderStroke(1.dp, com.example.ui.theme.BentoSlate200), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "徕卡镜头",
                                    color = com.example.ui.theme.BentoSlate800,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.White)
                                    .border(BorderStroke(1.dp, com.example.ui.theme.BentoSlate200), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "智能主动降噪",
                                    color = com.example.ui.theme.BentoSlate800,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .border(BorderStroke(1.dp, com.example.ui.theme.BentoSlate100), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "Camera",
                            tint = com.example.ui.theme.BentoSlate400,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            // Flash Sale Section (秒杀专区) styled in alignment with the Bento Grid theme
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "限时秒杀",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChineseRed
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ChineseRed)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = timerText,
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = "爆款直降 >",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.clickable { onNavigateToCategory("手机数码") }
                    )
                }

                val flashSaleItems = products.filter { it.isFlashSale }
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(flashSaleItems) { prod ->
                        Card(
                            modifier = Modifier
                                .width(130.dp)
                                .clickable { onNavigateToProduct(prod.id) },
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, com.example.ui.theme.BentoSlate100),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(com.example.ui.theme.BentoSlate50),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (prod.category) {
                                            "手机数码" -> Icons.Default.PhoneAndroid
                                            "家用电器" -> Icons.Default.Tv
                                            "电脑办公" -> Icons.Default.Laptop
                                            "时尚服饰" -> Icons.Default.Checkroom
                                            "美妆护肤" -> Icons.Default.Face
                                            "食品饮料" -> Icons.Default.Restaurant
                                            "家居生活" -> Icons.Default.Bed
                                            "运动户外" -> Icons.Default.DirectionsRun
                                            else -> Icons.Default.FlashOn
                                        },
                                        contentDescription = "Flash",
                                        tint = ChineseRed,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = prod.title,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontWeight = FontWeight.Bold,
                                    color = com.example.ui.theme.BentoSlate900
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "¥${prod.flashSalePrice ?: prod.price}",
                                        color = ChineseRed,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "¥${prod.originalPrice}",
                                        color = com.example.ui.theme.BentoSlate400,
                                        fontSize = 9.sp,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Today's Recommended (今日推荐 - Grid layout)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "今日推荐 • 猜你喜欢",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = com.example.ui.theme.BentoSlate900,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Render products as grid items (2 items per row)
                val recommended = products.filter { it.isTodayRecommended }
                val chunkedList = recommended.chunked(2)

                chunkedList.forEach { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { prod ->
                            Box(modifier = Modifier.weight(1f)) {
                                ProductGridCard(
                                    product = prod,
                                    onProductClick = { onNavigateToProduct(prod.id) },
                                    onAddToCart = { viewModel.addToCart(prod.id, 1) }
                                )
                            }
                        }
                        if (rowItems.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductGridCard(
    product: Product,
    onProductClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onProductClick),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, com.example.ui.theme.BentoSlate100),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(com.example.ui.theme.BentoSlate50),
                contentAlignment = Alignment.Center
            ) {
                // Since actual network/loaded image sizes may vary, we display an icon placeholder that fits beautifully
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
                        tint = ChineseRed.copy(alpha = 0.6f),
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = product.brand,
                        fontSize = 10.sp,
                        color = com.example.ui.theme.BentoSlate500,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // Overlay brand name sticker
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ChineseRed)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (product.isFlashSale) "秒杀爆款" else "星购优选",
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = product.title,
                    fontSize = 13.sp,
                    maxLines = 2,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    color = com.example.ui.theme.BentoSlate900
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = ImperialGold,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "${product.rating}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = com.example.ui.theme.BentoSlate800,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "已售${product.salesCount}",
                        fontSize = 10.sp,
                        color = com.example.ui.theme.BentoSlate500
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "￥${product.price}",
                        color = ChineseRed,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    IconButton(
                        onClick = onAddToCart,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(ChineseRed)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add to Cart",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

private data class CategoryItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
)
