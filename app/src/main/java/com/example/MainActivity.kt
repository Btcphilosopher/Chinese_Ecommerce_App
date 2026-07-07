package com.example

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.MainViewModel
import com.example.ui.cart.CartScreen
import com.example.ui.category.CategoryScreen
import com.example.ui.checkout.CheckoutScreen
import com.example.ui.detail.ProductDetailScreen
import com.example.ui.home.HomeScreen
import com.example.ui.merchant.MerchantScreen
import com.example.ui.profile.ProfileScreen
import com.example.ui.search.SearchScreen
import com.example.ui.theme.XingGouTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      XingGouTheme {
        XingGouApp()
      }
    }
  }
}

@Composable
fun XingGouApp() {
  val navController = rememberNavController()
  val context = LocalContext.current
  
  // Custom ViewModel factory mapping to local app Context
  val mainViewModel: MainViewModel = viewModel(
    factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(
      context.applicationContext as Application
    )
  )

  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route

  // Core navigation tabs that show the main bottom bar
  val mainDestinations = listOf("home", "category", "cart", "profile")

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    bottomBar = {
      if (currentRoute in mainDestinations) {
        NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
          NavigationBarItem(
            selected = currentRoute == "home",
            onClick = { navController.navigate("home") { popUpTo("home") { saveState = true }; launchSingleTop = true; restoreState = true } },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("首页", style = MaterialTheme.typography.labelSmall) }
          )
          NavigationBarItem(
            selected = currentRoute == "category",
            onClick = { navController.navigate("category") { popUpTo("home") { saveState = true }; launchSingleTop = true; restoreState = true } },
            icon = { Icon(Icons.Default.Category, contentDescription = "Category") },
            label = { Text("分类", style = MaterialTheme.typography.labelSmall) }
          )
          NavigationBarItem(
            selected = currentRoute == "cart",
            onClick = { navController.navigate("cart") { popUpTo("home") { saveState = true }; launchSingleTop = true; restoreState = true } },
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Cart") },
            label = { Text("购物车", style = MaterialTheme.typography.labelSmall) }
          )
          NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = { navController.navigate("profile") { popUpTo("home") { saveState = true }; launchSingleTop = true; restoreState = true } },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("我", style = MaterialTheme.typography.labelSmall) }
          )
        }
      }
    }
  ) { innerPadding ->
    NavHost(
      navController = navController,
      startDestination = "home",
      modifier = Modifier.padding(innerPadding)
    ) {
      // 1. Home Screen
      composable("home") {
        HomeScreen(
          viewModel = mainViewModel,
          onNavigateToProduct = { id -> navController.navigate("product_detail/$id") },
          onNavigateToSearch = { query -> navController.navigate("search?query=$query") },
          onNavigateToCategory = { cat -> navController.navigate("category?initial=$cat") },
          onNavigateToCart = { navController.navigate("cart") },
          onNavigateToProfile = { navController.navigate("profile") }
        )
      }

      // 2. Category Screen
      composable(
        route = "category?initial={initial}",
        arguments = listOf(navArgument("initial") { type = NavType.StringType; nullable = true; defaultValue = null })
      ) { backStackEntry ->
        val initial = backStackEntry.arguments?.getString("initial")
        CategoryScreen(
          viewModel = mainViewModel,
          initialCategory = initial,
          onNavigateToProduct = { id -> navController.navigate("product_detail/$id") },
          onNavigateToSearch = { query -> navController.navigate("search?query=$query") }
        )
      }

      // 3. Search Screen
      composable(
        route = "search?query={query}",
        arguments = listOf(navArgument("query") { type = NavType.StringType; nullable = true; defaultValue = null })
      ) { backStackEntry ->
        val query = backStackEntry.arguments?.getString("query")
        SearchScreen(
          viewModel = mainViewModel,
          initialQuery = query,
          onNavigateToProduct = { id -> navController.navigate("product_detail/$id") },
          onBack = { navController.popBackStack() }
        )
      }

      // 4. Product Details
      composable(
        route = "product_detail/{productId}",
        arguments = listOf(navArgument("productId") { type = NavType.IntType })
      ) { backStackEntry ->
        val productId = backStackEntry.arguments?.getInt("productId") ?: 1
        ProductDetailScreen(
          viewModel = mainViewModel,
          productId = productId,
          onNavigateToCart = { navController.navigate("cart") },
          onNavigateToCheckoutSingle = { _ -> navController.navigate("checkout") },
          onBack = { navController.popBackStack() }
        )
      }

      // 5. Cart
      composable("cart") {
        CartScreen(
          viewModel = mainViewModel,
          onNavigateToCheckout = { navController.navigate("checkout") },
          onNavigateToHome = { navController.navigate("home") }
        )
      }

      // 6. Checkout
      composable("checkout") {
        CheckoutScreen(
          viewModel = mainViewModel,
          onNavigateToOrders = { 
            navController.navigate("profile") {
              popUpTo("home")
            }
          },
          onBack = { navController.popBackStack() }
        )
      }

      // 7. Profile / User Center
      composable("profile") {
        ProfileScreen(
          viewModel = mainViewModel,
          onNavigateToMerchant = { navController.navigate("merchant") }
        )
      }

      // 8. Merchant Console
      composable("merchant") {
        MerchantScreen(
          viewModel = mainViewModel,
          onBack = { navController.popBackStack() }
        )
      }
    }
  }
}
