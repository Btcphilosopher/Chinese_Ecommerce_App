package com.example.ui.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Product
import com.example.ui.MainViewModel
import com.example.ui.theme.ChineseRed
import com.example.ui.theme.ImperialGold
import com.example.ui.theme.LightGrey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    viewModel: MainViewModel,
    initialCategory: String?,
    onNavigateToProduct: (Int) -> Unit,
    onNavigateToSearch: (String) -> Unit
) {
    val categories = listOf(
        "手机数码", "家用电器", "电脑办公", "时尚服饰",
        "美妆护肤", "食品饮料", "家居生活", "母婴用品",
        "图书文创", "运动户外", "汽车用品", "宠物用品"
    )

    var selectedCategory by remember { mutableStateOf(initialCategory ?: "手机数码") }
    val products by viewModel.products.collectAsState()

    val filteredProducts = products.filter { it.category == selectedCategory }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("商品分类", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Left Side bar (Vertical navigation)
            LazyColumn(
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
                    .background(com.example.ui.theme.BentoSlate50)
            ) {
                items(categories) { category ->
                    val isSelected = category == selectedCategory
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(if (isSelected) Color.White else Color.Transparent)
                            .clickable { selectedCategory = category }
                            .padding(horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Left active indicator line
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .width(4.dp)
                                    .height(24.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(ChineseRed)
                            )
                        }
                        Text(
                            text = category,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) ChineseRed else com.example.ui.theme.BentoSlate500,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Right main content area (Grid display of products)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(12.dp)
            ) {
                // Category Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            androidx.compose.ui.graphics.Brush.linearGradient(
                                colors = listOf(com.example.ui.theme.BentoRed50, com.example.ui.theme.BentoOrange50)
                            )
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Column {
                        Text(
                            text = selectedCategory,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = ChineseRed
                        )
                        Text(
                            text = "星购严选 • 100% 正品保证",
                            fontSize = 11.sp,
                            color = com.example.ui.theme.BentoSlate500,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (filteredProducts.isEmpty()) {
                    // Empty state (If no products loaded yet for this category)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.LocalMall,
                                contentDescription = "Empty Category",
                                tint = Color.LightGray,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "该分类商品正在急速上架中...",
                                fontSize = 13.sp,
                                color = com.example.ui.theme.BentoSlate500
                            )
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(filteredProducts) { product ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigateToProduct(product.id) },
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BentoSlate100),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(85.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(com.example.ui.theme.BentoSlate50),
                                        contentAlignment = Alignment.Center
                                    ) {
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
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(6.dp))
                                    
                                    Text(
                                        text = product.title,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        color = com.example.ui.theme.BentoSlate900
                                    )
                                    
                                    Spacer(modifier = Modifier.height(4.dp))
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "¥${product.price}",
                                            color = ChineseRed,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "已售${product.salesCount}",
                                            color = com.example.ui.theme.BentoSlate400,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
