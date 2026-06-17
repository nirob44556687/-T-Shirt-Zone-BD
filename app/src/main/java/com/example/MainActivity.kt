package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.data.database.AppDatabase
import com.example.data.repository.ShopRepository
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ShopViewModel
import com.example.ui.viewmodel.ShopViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Core MVVM Room configuration
        val database = AppDatabase.getDatabase(applicationContext, lifecycleScope)
        val repository = ShopRepository(
            productDao = database.productDao(),
            cartDao = database.cartDao(),
            wishlistDao = database.wishlistDao(),
            orderDao = database.orderDao(),
            legalPageDao = database.legalPageDao()
        )
        val viewModel = ViewModelProvider(this, ShopViewModelFactory(repository))[ShopViewModel::class.java]

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val isEnglish by viewModel.isEnglish.collectAsState()
                val cartItems by viewModel.cartItems.collectAsState()

                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = currentBackStackEntry?.destination?.route ?: "home"

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        // Brand responsive banner layout
                        if (currentRoute != "admin" && !currentRoute.startsWith("policy/") && !currentRoute.startsWith("details/")) {
                            AppHeader(
                                viewModel = viewModel,
                                isEnglish = isEnglish,
                                onNavigateToPolicy = { policy ->
                                    navController.navigate("policy/$policy")
                                },
                                onNavigateToAdmin = {
                                    navController.navigate("admin")
                                }
                            )
                        }
                    },
                    bottomBar = {
                        // Elegant custom bottom navigation bar
                        if (currentRoute != "admin" && !currentRoute.startsWith("policy/") && !currentRoute.startsWith("details/")) {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.background,
                                tonalElevation = 8.dp,
                                modifier = Modifier
                                    .testTag("app_bottom_navigator")
                                    .windowInsetsPadding(WindowInsets.navigationBars)
                            ) {
                                NavigationBarItem(
                                    selected = currentRoute == "home",
                                    onClick = { navController.navigate("home") },
                                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") },
                                    label = { Text(text = txt("Home", "হোম", isEnglish)) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.Black,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        indicatorColor = MaterialTheme.colorScheme.primary,
                                        unselectedIconColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                        unselectedTextColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                    ),
                                    modifier = Modifier.testTag("nav_item_home")
                                )

                                NavigationBarItem(
                                    selected = currentRoute == "wishlist",
                                    onClick = { navController.navigate("wishlist") },
                                    icon = { Icon(imageVector = Icons.Default.Favorite, contentDescription = "Saved") },
                                    label = { Text(text = txt("Wishlist", "পছন্দ", isEnglish)) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.Black,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        indicatorColor = MaterialTheme.colorScheme.primary,
                                        unselectedIconColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                        unselectedTextColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                    ),
                                    modifier = Modifier.testTag("nav_item_wish")
                                )

                                NavigationBarItem(
                                    selected = currentRoute == "cart",
                                    onClick = { navController.navigate("cart") },
                                    icon = {
                                        BadgedBox(
                                            badge = {
                                                if (cartItems.isNotEmpty()) {
                                                    Badge(containerColor = Color.Red, contentColor = Color.White) {
                                                        Text(text = cartItems.sumOf { it.quantity }.toString())
                                                    }
                                                }
                                            }
                                        ) {
                                            Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Cart")
                                        }
                                    },
                                    label = { Text(text = txt("Cart", "কার্ট", isEnglish)) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.Black,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        indicatorColor = MaterialTheme.colorScheme.primary,
                                        unselectedIconColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                        unselectedTextColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                    ),
                                    modifier = Modifier.testTag("nav_item_cart")
                                )

                                NavigationBarItem(
                                    selected = currentRoute == "profile",
                                    onClick = { navController.navigate("profile") },
                                    icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Profile") },
                                    label = { Text(text = txt("Profile", "প্রোফাইল", isEnglish)) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.Black,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        indicatorColor = MaterialTheme.colorScheme.primary,
                                        unselectedIconColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                        unselectedTextColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                    ),
                                    modifier = Modifier.testTag("nav_item_profile")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        composable("home") {
                            HomeScreen(
                                viewModel = viewModel,
                                isEnglish = isEnglish,
                                onNavigateToDetails = { pid ->
                                    navController.navigate("details/$pid")
                                },
                                onNavigateToPolicy = { policy ->
                                    navController.navigate("policy/$policy")
                                },
                                onNavigateToAdmin = {
                                    navController.navigate("admin")
                                }
                            )
                        }

                        composable(
                            route = "details/{productId}",
                            arguments = listOf(navArgument("productId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val pid = backStackEntry.arguments?.getInt("productId") ?: 0
                            ProductDetailScreen(
                                productId = pid,
                                viewModel = viewModel,
                                isEnglish = isEnglish,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("cart") {
                            CartScreen(
                                viewModel = viewModel,
                                isEnglish = isEnglish,
                                onNavigateHome = { navController.navigate("home") }
                            )
                        }

                        composable("wishlist") {
                            WishlistScreen(
                                viewModel = viewModel,
                                isEnglish = isEnglish,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("profile") {
                            ProfileScreen(
                                viewModel = viewModel,
                                isEnglish = isEnglish,
                                onNavigateToPolicy = { policy ->
                                    navController.navigate("policy/$policy")
                                }
                            )
                        }

                        composable("admin") {
                            AdminDashboardScreen(
                                viewModel = viewModel,
                                isEnglish = isEnglish,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable(
                            route = "policy/{policyId}",
                            arguments = listOf(navArgument("policyId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val pId = backStackEntry.arguments?.getString("policyId") ?: "privacy"
                            PolicyViewerScreen(
                                policyId = pId,
                                viewModel = viewModel,
                                isEnglish = isEnglish,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
