package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.data.database.AppDatabase
import com.example.data.repository.ShopRepository
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ShopViewModel
import com.example.ui.viewmodel.ShopViewModelFactory

class AdminMainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // MVVM Room database initialisation (shared instantly on device)
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
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val isEnglish by viewModel.isEnglish.collectAsState()
                    AdminDashboardScreen(
                        viewModel = viewModel,
                        isEnglish = isEnglish,
                        onNavigateBack = {
                            finish() // Exit the Admin App
                        }
                    )
                }
            }
        }
    }
}
