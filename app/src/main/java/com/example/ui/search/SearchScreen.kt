package com.example.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
fun SearchScreen(
    viewModel: MainViewModel,
    initialQuery: String?,
    onNavigateToProduct: (Int) -> Unit,
    onBack: () -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()
    val products by viewModel.products.collectAsState()

    // Filters state
    val selectedBrand by viewModel.filterBrand.collectAsState()
    val minPrice by viewModel.filterMinPrice.collectAsState()
    val maxPrice by viewModel.filterMaxPrice.collectAsState()
    val onlyInStock by viewModel.filterOnlyInStock.collectAsState()
    val sortBy by viewModel.filterSortBy.collectAsState()

    var showFilterSheet by remember { mutableStateOf(false) }
    var searchActive by remember { mutableStateOf(initialQuery.isNullOrEmpty()) }

    // Init query
    LaunchedEffect(initialQuery) {
        if (!initialQuery.isNullOrEmpty()) {
            viewModel.addSearchQuery(initialQuery)
            searchActive = false
        }
    }

    // Filter results
    val filteredProducts = products.filter { product ->
        val matchesQuery = product.title.contains(searchQuery, ignoreCase = true) ||
                product.description.contains(searchQuery, ignoreCase = true) ||
                product.brand.contains(searchQuery, ignoreCase = true)

        val matchesBrand = selectedBrand == null || product.brand.contains(selectedBrand!!)
        val matchesMinPrice = minPrice == null || product.price >= minPrice!!
        val matchesMaxPrice = maxPrice == null || product.price <= maxPrice!!
        val matchesStock = !onlyInStock || product.stock > 0

        matchesQuery && matchesBrand && matchesMinPrice && matchesMaxPrice && matchesStock
    }.sortedWith { a, b ->
        when (sortBy) {
            "price_asc" -> a.price.compareTo(b.price)
            "price_desc" -> b.price.compareTo(a.price)
            "rating" -> b.rating.compareTo(a.rating)
            else -> b.salesCount.compareTo(a.salesCount) // default sales sort
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.searchQuery.value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        placeholder = { Text("智能搜索商品、品牌", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, "Search") },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                    Icon(Icons.Default.Clear, "Clear")
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ChineseRed,
                            unfocusedBorderColor = Color.LightGray,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(26.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            viewModel.addSearchQuery(searchQuery)
                            searchActive = false
                        })
                    )
                },
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
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (searchActive) {
                // Display search recommendations & history
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    // Hot searches (热门推荐)
                    item {
                        Text(
                            text = "热门搜索",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        FlowRow(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            viewModel.hotSearches.forEach { hot ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(LightGrey)
                                        .clickable {
                                            viewModel.addSearchQuery(hot)
                                            searchActive = false
                                        }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(hot, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    // Search history (历史搜索)
                    if (searchHistory.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("搜索历史", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                IconButton(onClick = { viewModel.clearSearchHistory() }) {
                                    Icon(Icons.Default.Delete, "Delete History", tint = Color.Gray, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                        items(searchHistory) { hist ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.addSearchQuery(hist)
                                        searchActive = false
                                    }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.History, "History", tint = Color.LightGray, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(hist, fontSize = 14.sp, color = Color.DarkGray)
                            }
                            HorizontalDivider(color = LightGrey)
                        }
                    }
                }
            } else {
                // Sorting & Filter action row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        SortTab("销量", sortBy == "sales", onClick = { viewModel.filterSortBy.value = "sales" })
                        SortTab("价格低-高", sortBy == "price_asc", onClick = { viewModel.filterSortBy.value = "price_asc" })
                        SortTab("价格高-低", sortBy == "price_desc", onClick = { viewModel.filterSortBy.value = "price_desc" })
                        SortTab("评分", sortBy == "rating", onClick = { viewModel.filterSortBy.value = "rating" })
                    }
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter",
                            tint = if (selectedBrand != null || minPrice != null || maxPrice != null || onlyInStock) ChineseRed else Color.Gray
                        )
                    }
                }

                HorizontalDivider(color = LightGrey)

                // Results list
                if (filteredProducts.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.SentimentDissatisfied, "No Results", tint = Color.LightGray, modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("未找到相关商品，请尝试换个关键词", color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredProducts) { product ->
                            SearchProductItemRow(product = product, onClick = { onNavigateToProduct(product.id) })
                        }
                    }
                }
            }
        }

        // Filter Dialog (Simulated as a Dialog since BottomSheet can be more verbose)
        if (showFilterSheet) {
            AlertDialog(
                onDismissRequest = { showFilterSheet = false },
                title = { Text("筛选条件", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Brand filter
                        Text("品牌筛选", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("星耀", "星刃", "星视", "国风").forEach { b ->
                                val isSelected = selectedBrand == b
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.filterBrand.value = if (isSelected) null else b },
                                    label = { Text(b) }
                                )
                            }
                        }

                        // Price range
                        Text("价格区间 (￥)", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = minPrice?.toString() ?: "",
                                onValueChange = { viewModel.filterMinPrice.value = it.toDoubleOrNull() },
                                modifier = Modifier.weight(1f).height(48.dp),
                                placeholder = { Text("最低价", fontSize = 12.sp) },
                                singleLine = true
                            )
                            Text("-", color = Color.Gray)
                            OutlinedTextField(
                                value = maxPrice?.toString() ?: "",
                                onValueChange = { viewModel.filterMaxPrice.value = it.toDoubleOrNull() },
                                modifier = Modifier.weight(1f).height(48.dp),
                                placeholder = { Text("最高价", fontSize = 12.sp) },
                                singleLine = true
                            )
                        }

                        // In stock only
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().clickable { viewModel.filterOnlyInStock.value = !onlyInStock }
                        ) {
                            Checkbox(checked = onlyInStock, onCheckedChange = { viewModel.filterOnlyInStock.value = it })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("仅看有货商品", fontSize = 14.sp)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showFilterSheet = false },
                        colors = ButtonDefaults.buttonColors(containerColor = ChineseRed)
                    ) {
                        Text("确定")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        // Reset filters
                        viewModel.filterBrand.value = null
                        viewModel.filterMinPrice.value = null
                        viewModel.filterMaxPrice.value = null
                        viewModel.filterOnlyInStock.value = false
                        showFilterSheet = false
                    }) {
                        Text("重置", color = Color.Gray)
                    }
                }
            )
        }
    }
}

@Composable
fun SortTab(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) ChineseRed else Color.DarkGray
        )
    }
}

@Composable
fun SearchProductItemRow(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(LightGrey),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalMall,
                    contentDescription = product.title,
                    tint = ChineseRed.copy(alpha = 0.5f),
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = product.brand,
                    color = Color.Gray,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "￥${product.price}",
                        color = ChineseRed,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "已售${product.salesCount}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        content = { content() }
    )
}
