package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.model.*
import com.example.ui.viewmodel.AuthState
import com.example.ui.viewmodel.ShopViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Dynamic localization helper
fun txt(en: String, bn: String, isEnglish: Boolean): String {
    return if (isEnglish) en else bn
}

@Composable
fun AppHeader(
    viewModel: ShopViewModel,
    isEnglish: Boolean,
    onNavigateToPolicy: (String) -> Unit,
    onNavigateToAdmin: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    val isAdmin by viewModel.isAdminMode.collectAsState()

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        shape = RoundedCornerShape(0.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Brand name with custom glowing Gold styling
                Column {
                    Text(
                        text = "T-Shirt Zone BD",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier.testTag("app_brand_title")
                    )
                    Text(
                        text = txt("Premium & Elite Streetwear", "প্রিমিয়াম ও এলিট স্ট্রিটওয়্যার", isEnglish),
                        color = MaterialTheme.colorScheme.tertiary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Language Switcher Badge
                    IconButton(
                        onClick = { viewModel.toggleLanguage() },
                        modifier = Modifier
                            .testTag("language_toggle_btn")
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .size(38.dp)
                    ) {
                        Text(
                            text = if (isEnglish) "বাংলা" else "ENG",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Admin shortcut icon always accessible to everyone
                    IconButton(
                        onClick = onNavigateToAdmin,
                        modifier = Modifier
                            .testTag("admin_redirect_btn")
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin Area",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerLegalFooter(
    isEnglish: Boolean,
    onNavigateToPolicy: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = txt("Privacy", "গোপনীয়তা", isEnglish),
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable { onNavigateToPolicy("privacy") }
                .padding(6.dp)
        )
        Text(
            text = "•",
            color = MaterialTheme.colorScheme.outline,
            fontSize = 11.sp
        )
        Text(
            text = txt("Refund", "রিফান্ড পলিসি", isEnglish),
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable { onNavigateToPolicy("refund") }
                .padding(6.dp)
        )
        Text(
            text = "•",
            color = MaterialTheme.colorScheme.outline,
            fontSize = 11.sp
        )
        Text(
            text = txt("Terms", "শর্তাবলী", isEnglish),
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable { onNavigateToPolicy("terms") }
                .padding(6.dp)
        )
    }
}

@Composable
fun DynamicResourceImage(
    imageUrl: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current
    val resourceId = remember(imageUrl) {
        val id = context.resources.getIdentifier(imageUrl, "drawable", context.packageName)
        if (id != 0) id else R.drawable.ic_launcher_background
    }

    Image(
        painter = painterResource(id = resourceId),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale
    )
}

// ------ SCREEN 1: HOME CATALOG WITH BANNER & ADVANCED SEARCH ------
@Composable
fun HomeScreen(
    viewModel: ShopViewModel,
    isEnglish: Boolean,
    onNavigateToDetails: (Int) -> Unit,
    onNavigateToPolicy: (String) -> Unit,
    onNavigateToAdmin: () -> Unit
) {
    val products by viewModel.filteredProducts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    val categories = listOf(
        txt("All", "সব", isEnglish),
        txt("Premium Embroidery", "প্রিমিয়াম এমব্রয়ডারি", isEnglish),
        txt("Streetwear Collection", "স্ট্রিটওয়্যার কালেকশন", isEnglish),
        txt("Cyberpunk Series", "সাইবারপাঙ্ক সিরিজ", isEnglish),
        txt("Calligraphy Gold", "ক্যালিগ্রাফি গোল্ড", isEnglish)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_layout"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // --- Premium Banner ---
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                DynamicResourceImage(
                    imageUrl = "img_tshirt_hero",
                    contentDescription = "T-Shirt Zone BD Premium Store Showcase",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Golden glassmorphic overlay banner text
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.85f)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "New Arrival",
                            tint = Color.Black,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = txt("FEAST OF GOLD", "স্বর্ণালী উৎসব", isEnglish),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = txt("Gold Foil Exclusive Drops", "মেটালিক গোল্ড ফয়েল কালেকশন", isEnglish),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = txt("Use code TZB10 to get 10% instant discount", "TZB10 ব্যবহারে ১০% ইন্সট্যান্ট ডিসকাউন্ট পাবেন", isEnglish),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // --- Advanced Animated Search Bar ---
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = {
                        Text(
                            text = txt("Search premium t-shirts...", "প্রিমিয়াম টি-শার্ট খুঁজুন...", isEnglish),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear Test",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("catalog_search_input")
                )
            }
        }

        // --- Quick Prominent Dedicated Admin Portal Access Banner ---
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clickable { onNavigateToAdmin() }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin Portal",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = txt("DEDICATED ADMIN PORTAL PANEL", "আলাদা অ্যাডমিন কন্ট্রোল প্যানেল", isEnglish),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = txt(
                                "Add new premium T-shits, update price/stock, manage order pipelining and custom legal pages. Updates instantly!",
                                "সহজে নতুন টি-শার্ট কালেকশন যুক্ত করুন ও অর্ডার প্রসেস করুন। যুক্ত করা ক্যাটাগরি ও পণ্য সাথে সাথে অ্যাপে শো করবে!",
                                isEnglish
                            ),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                            lineHeight = 15.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Go",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // --- Categories row filter ---
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setSelectedCategory(category) },
                        label = {
                            Text(
                                text = category,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.Black,
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.primary
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    )
                }
            }
        }

        // --- Custom double-column responsive list ---
        if (products.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Inbox,
                        contentDescription = "No Products",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = txt("No items match your search", "কোনো পণ্য খুঁজে পাওয়া যায়নি", isEnglish),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            // Display products in chunks of 2 (Grid simulator for LazyColumn)
            val chunkedProducts = products.chunked(2)
            items(chunkedProducts) { rowItems ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (product in rowItems) {
                        ProductCard(
                            product = product,
                            isEnglish = isEnglish,
                            viewModel = viewModel,
                            onClick = { onNavigateToDetails(product.id) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowItems.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Footer Legal Links
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp, bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = txt("Registered Premium Retailer | BD Hot: 01615327169", "নিবন্ধিত প্রিমিয়াম শপ | হটলাইন: ০১৬১৫৩২৭১৬৯", isEnglish),
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                DrawerLegalFooter(isEnglish, onNavigateToPolicy)
            }
        }
    }
}

@Composable
fun ProductCard(
    product: ProductEntity,
    isEnglish: Boolean,
    viewModel: ShopViewModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val wishlistItems by viewModel.wishlistItems.collectAsState()
    val isFavorite = wishlistItems.any { it.productId == product.id }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        modifier = modifier
            .testTag("product_card_${product.id}")
            .clickable(onClick = onClick)
    ) {
        Column {
            Box {
                DynamicResourceImage(
                    imageUrl = product.imageUrl,
                    contentDescription = product.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentScale = ContentScale.Crop
                )

                // Optional Premium Tag
                if (product.isPremium) {
                    Box(
                        modifier = Modifier
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary
                                    )
                                ),
                                shape = RoundedCornerShape(bottomEnd = 12.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = txt("EXCLUSIVE", "প্রিমিয়াম", isEnglish),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                }

                // Wishlist Floating Button
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.6f),
                            shape = CircleShape
                        )
                        .clickable { viewModel.toggleWishlist(product) }
                        .padding(6.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Rating overlay
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                        .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = product.rating.toString(),
                        fontSize = 9.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = if (isEnglish) product.title else product.titleBn,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = if (isEnglish) product.category else product.categoryBn,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Currency formatting
                    Text(
                        text = "৳ ${product.price.toInt()}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Quick Add Icon Button
                    IconButton(
                        onClick = {
                            viewModel.addToCart(product, "L")
                        },
                        modifier = Modifier
                            .testTag("quick_add_cart_${product.id}")
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = "Add to Cart",
                            tint = Color.Black,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}


// ------ SCREEN 2: PRODUCT DETAIL VIEW WITH OPTIONAL REVIEW SUBMISSION ------
@Composable
fun ProductDetailScreen(
    productId: Int,
    viewModel: ShopViewModel,
    isEnglish: Boolean,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val products by viewModel.productsList.collectAsState()
    val product = products.find { it.id == productId }

    val sizes = listOf("M", "L", "XL", "XXL")
    var selectedSize by remember { mutableStateOf("L") }
    var quantity by remember { mutableStateOf(1) }

    // Simulated Product reviews list
    var reviewsList by remember {
        mutableStateOf(
            mutableListOf(
                Pair("Liyon Mirob", "Outstanding luxury fabric. The gold metallic print really pops out and looks beautiful! Recommended."),
                Pair("Sadik Al-Amin", "খুবই সুন্দর ডিজাইন এবং কাপড়ের কোয়ালিটি অনেক জোস ছিল। ডেলিভারি ২ দিনের মধ্যে পেয়েছি। ধন্যবাদ!")
            )
        )
    }
    var reviewerName by remember { mutableStateOf("") }
    var reviewComment by remember { mutableStateOf("") }

    if (product == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Product not found", color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    Scaffold(
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(8.dp)
            ) {
                IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("detail_back_btn")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = txt("Exclusive Product details", "এক্সক্লুসিভ পণ্যের বিবরণ", isEnglish),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 90.dp)
        ) {
            // Product image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                DynamicResourceImage(
                    imageUrl = product.imageUrl,
                    contentDescription = product.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Category
                Text(
                    text = if (isEnglish) product.category.uppercase() else product.categoryBn,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Title
                Text(
                    text = if (isEnglish) product.title else product.titleBn,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Price Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "৳ ${product.price.toInt()}",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = txt("Stock: ${product.stock} items remaining", "স্টক: মাত্র ${product.stock} টি বাঁকি", isEnglish),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }

                // Description
                Text(
                    text = txt("Description", "পণ্যের বৈশিষ্ট্য সমূহ", isEnglish),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                )
                Text(
                    text = if (isEnglish) product.description else product.descriptionBn,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    lineHeight = 20.sp
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.outline)

                // SIZE SELECTOR
                Text(
                    text = txt("Select Premium Size", "আকার বেছে নিন (প্রিমিয়াম ফিটিং)", isEnglish),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    sizes.forEach { s ->
                        val isSelected = selectedSize == s
                        Box(
                            modifier = Modifier
                                .testTag("size_option_$s")
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                )
                                .clickable { selectedSize = s },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = s,
                                color = if (isSelected) Color.Black else MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // QUANTITY CONTROLLER
                Text(
                    text = txt("Quantity", "পরিমাণ", isEnglish),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    IconButton(
                        onClick = { if (quantity > 1) quantity-- },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                            .size(34.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", tint = MaterialTheme.colorScheme.primary)
                    }

                    Text(
                        text = quantity.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )

                    IconButton(
                        onClick = { if (quantity < product.stock) quantity++ },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                            .size(34.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", tint = MaterialTheme.colorScheme.primary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // BUY / ADD TO CART ACTION BUTTONS
                Button(
                    onClick = {
                        repeat(quantity) {
                            viewModel.addToCart(product, selectedSize)
                        }
                        Toast.makeText(context, txt("Added to Shopping Cart!", "শপিং কার্টে যুক্ত হয়েছে!", isEnglish), Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("add_to_cart_detail_btn")
                ) {
                    Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Cart", tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = txt("Add to Cart - ৳ ${(product.price * quantity).toInt()}", "কার্টে যোগ করুন - ৳ ${(product.price * quantity).toInt()}", isEnglish),
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp), color = MaterialTheme.colorScheme.outline)

                // CUSTOMER RATINGS & REVIEWS INTERACTIVE FIELD
                Text(
                    text = txt("Verified Reviews (${reviewsList.size})", "ভেরিফাইড ক্রেতাদের মতামত (${reviewsList.size})", isEnglish),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    reviewsList.forEach { (reviewer, comment) ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(text = reviewer, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                    Row {
                                        repeat(5) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(10.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = comment,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }

                // Write review form block
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = txt("Share your valuable review", "আপনার নিজস্ব মতামত এখানে লিখুন", isEnglish),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = reviewerName,
                            onValueChange = { reviewerName = it },
                            placeholder = { Text(text = txt("Your Name", "আপনার নাম", isEnglish), fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = reviewComment,
                            onValueChange = { reviewComment = it },
                            placeholder = { Text(text = txt("Your feedback comment...", "রিভিউ এর বিস্তারিত...", isEnglish), fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                if (reviewerName.trim().isNotEmpty() && reviewComment.trim().isNotEmpty()) {
                                    val updated = reviewsList.toMutableList()
                                    updated.add(Pair(reviewerName, reviewComment))
                                    reviewsList = updated
                                    reviewerName = ""
                                    reviewComment = ""
                                    Toast.makeText(context, txt("Review posted, thanks!", "রিভিউ সফলভাবে পোস্ট হয়েছে!", isEnglish), Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, txt("Fill details please", "অনুগ্রহ করে সব তথ্য দিন", isEnglish), Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.outline),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(text = txt("Submit Review", "রিভিউ দিন", isEnglish), fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}


// ------ SCREEN 3: CART VIEW ------
@Composable
fun CartScreen(
    viewModel: ShopViewModel,
    isEnglish: Boolean,
    onNavigateHome: () -> Unit
) {
    val items by viewModel.cartItems.collectAsState()
    val subtotal = items.sumOf { it.price * it.quantity }

    var selectedDeliveryArea by remember { mutableStateOf("Dhaka") } // Dhaka / Outside Dhaka
    val deliveryFee = if (selectedDeliveryArea == "Dhaka") 60.0 else 120.0
    val grandTotal = subtotal + deliveryFee

    var checkoutDialogOpen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                Text(
                    text = txt("Your Shopping Cart", "আপনার শপিং কার্ট", isEnglish),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

                if (items.isNotEmpty()) {
                    TextButton(onClick = { viewModel.clearCart() }) {
                        Text(
                            text = txt("Clear All", "সব মুছুন", isEnglish),
                            color = Color.Red,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Empty",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = txt("Your cart is empty", "শপিং কার্ট খালি", isEnglish),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Text(
                        text = txt("Browse products and purchase gold fashion", "প্রিমিয়াম গোল্ড কালেকশন দেখতে হোম ভিজিট করুন", isEnglish),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onNavigateHome,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(text = txt("Shop Now", "শপ করুন", isEnglish), color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(bottom = 80.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    items(items) { item ->
                        CartItemRow(item, isEnglish, viewModel)
                    }

                    // Summary details Card
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = txt("Pricing Summary", "বিলিং সারাংশ", isEnglish),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 10.dp)
                                )

                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(text = txt("Subtotal", "পোশাকের মূল্য মোট", isEnglish), fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground.copy(0.7f))
                                    Text(text = "৳ ${subtotal.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Delivery zone options
                                Text(
                                    text = txt("Delivery Area", "ডেলিভারি এলাকা নির্ধারণ করুন", isEnglish),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Row(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .testTag("delivery_inside_dhaka")
                                            .background(
                                                color = if (selectedDeliveryArea == "Dhaka") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .border(1.dp, MaterialTheme.colorScheme.outline)
                                            .clickable { selectedDeliveryArea = "Dhaka" }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = txt("Inside Dhaka (+৳60)", "ঢাকার ভিতরে (৳৬০)", isEnglish),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selectedDeliveryArea == "Dhaka") Color.Black else MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .testTag("delivery_outside_dhaka")
                                            .background(
                                                color = if (selectedDeliveryArea == "Outside") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .border(1.dp, MaterialTheme.colorScheme.outline)
                                            .clickable { selectedDeliveryArea = "Outside" }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = txt("Outside Dhaka (+৳120)", "ঢাকার বাইরে (৳১২০)", isEnglish),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selectedDeliveryArea == "Outside") Color.Black else MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(text = txt("Grand Total", "সর্বমোট প্রদেয় বিল", isEnglish), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text(text = "৳ ${grandTotal.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = { checkoutDialogOpen = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(50.dp)
                        .testTag("proceed_checkout_btn")
                ) {
                    Text(
                        text = txt("Proceed to Checkout", "চেকআউট করতে এগিয়ে যান", isEnglish),
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }

    // Interactive simulated checkout flow modal!
    if (checkoutDialogOpen) {
        CheckoutDialog(
            viewModel = viewModel,
            isEnglish = isEnglish,
            deliveryArea = selectedDeliveryArea,
            grandTotal = grandTotal,
            onDismiss = { checkoutDialogOpen = false }
        )
    }
}

@Composable
fun CartItemRow(
    item: CartItemEntity,
    isEnglish: Boolean,
    viewModel: ShopViewModel
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DynamicResourceImage(
                imageUrl = item.imageUrl,
                contentDescription = item.title,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isEnglish) item.title else item.titleBn,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = txt("Size: ${item.sizeSelected}", "সাইজ: ${item.sizeSelected}", isEnglish),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "৳ ${item.price.toInt()}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Quantity buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { viewModel.decrementCartItem(item.id, item.quantity) },
                    modifier = Modifier
                        .testTag("decrement_cart_item_${item.id}")
                        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(4.dp))
                        .size(24.dp)
                ) {
                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(10.dp))
                }

                Text(
                    text = item.quantity.toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                IconButton(
                    onClick = { viewModel.incrementCartItem(item.id, item.quantity) },
                    modifier = Modifier
                        .testTag("increment_cart_item_${item.id}")
                        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(4.dp))
                        .size(24.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(10.dp))
                }

                IconButton(onClick = { viewModel.deleteFromCart(item.id) }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}


// ------ CHECKOUT DIALOG WITH AMAZING MOBILE WALLET OVERLAYS ------
@Composable
fun CheckoutDialog(
    viewModel: ShopViewModel,
    isEnglish: Boolean,
    deliveryArea: String,
    grandTotal: Double,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsState()

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    // Prefill auth info if available
    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            val user = (authState as AuthState.Authenticated).user
            name = user.name
            phone = user.phone
            address = user.address
        }
    }

    // Payment method selected (bKash, Nagad, Rocket, COD)
    var selectedMethod by remember { mutableStateOf("bKash") }

    // Simulated mobile wallet overlay triggers
    var isWalletOverlayActive by remember { mutableStateOf(false) }
    var walletMobileNumber by remember { mutableStateOf("") }
    var walletVerificationCode by remember { mutableStateOf("") }
    var walletPinNumber by remember { mutableStateOf("") }
    var walletStatusMsg by remember { mutableStateOf("") }
    var walletStep by remember { mutableStateOf(1) } // 1: Number, 2: OTP, 3: PIN, 4: Success

    Dialog(
        onDismissRequest = { if (!isWalletOverlayActive) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(0.7f)),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = txt("Checkout - Verify Order Address", "চেকআউট - ঠিকানা ও পেমেন্ট বিবরণী", isEnglish),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.primary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Name Input
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(text = txt("Consignee Full Name", "নাম", isEnglish)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("checkout_name_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Mobile phone
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(text = txt("Mobile Phone (Bangladesh)", "মোবাইল নম্বর", isEnglish)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("checkout_phone_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Full delivery address
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text(text = txt("Detailed Shipping Address", "পূর্ণাঙ্গ ডেলিভারি ঠিকানা", isEnglish)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("checkout_address_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )

                // Select Payment Method
                Text(
                    text = txt("PAYMENT GATEWAY METHOD (BANGLADESH)", "পেমেন্ট মাধ্যম বেছে নিন", isEnglish),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 18.dp, bottom = 4.dp)
                )

                val paymentMethods = listOf("bKash", "Nagad", "Rocket", "Cash on Delivery")
                val paymentIcons = listOf(R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_foreground)

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    paymentMethods.forEach { method ->
                        val isSelected = selectedMethod == method
                        val displayText = when (method) {
                            "bKash" -> txt("bKash Wallet (bkash gateway)", "বিকাশ ওয়ালেট (স্বয়ংক্রিয় গেটওয়ে)", isEnglish)
                            "Nagad" -> txt("Nagad Merchant e-Pay", "নগদ পে", isEnglish)
                            "Rocket" -> txt("Rocket Instant Payment", "রকেট ইনস্ট্যান্ট", isEnglish)
                            else -> txt("Cash on Delivery (COD)", "ক্যাশ অন ডেলিভারি (সিওডি)", isEnglish)
                        }

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(0.12f) else MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("pay_method_$method")
                                .clickable { selectedMethod = method }
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(vertical = 12.dp, horizontal = 12.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedMethod = method },
                                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = displayText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (name.trim().isEmpty() || phone.trim().length < 11 || address.trim().isEmpty()) {
                            Toast.makeText(context, txt("Please complete the required details correctly", "অনুগ্রহ করে নাম, ১১ ডিজিটের মোবাইল নম্বর ও ঠিকানা পূরণ করুন", isEnglish), Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (selectedMethod == "Cash on Delivery") {
                            // Instant COD order processing
                            viewModel.checkout(name, phone, address, selectedMethod, deliveryArea)
                            Toast.makeText(context, txt("Order created successfully!", "আপনার অর্ডার সফলভাবে বুকিং করা হয়েছে!", isEnglish), Toast.LENGTH_LONG).show()
                            onDismiss()
                        } else {
                            // Launch simulated mobile wallet gateway overlay!
                            walletMobileNumber = phone
                            walletStep = 1
                            isWalletOverlayActive = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("checkout_confirm_btn")
                ) {
                    Text(
                        text = txt("Proceed to Pay ৳ ${grandTotal.toInt()}", "৳ ${grandTotal.toInt()} পেমেন্ট করতে এগিয়ে যান", isEnglish),
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

    // --- IMMERSIVE BKASH / NAGAD / ROCKET SIMULATOR GATEWAY DIALOG OVERLAY ---
    if (isWalletOverlayActive) {
        val walletColor = when (selectedMethod) {
            "bKash" -> Color(0xFFE2125B) // Pink bKash Theme color
            "Nagad" -> Color(0xFFF15A22) // Orange Nagad Theme color
            else -> Color(0xFF8C3494) // Purple Rocket Theme color
        }

        val scope = rememberCoroutineScope()

        Dialog(
            onDismissRequest = { isWalletOverlayActive = false },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = walletColor),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Logo text representing standard gateway
                    Text(
                        text = selectedMethod.uppercase() + " Merchant Payment",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "T-Shirt Zone BD Store",
                        color = Color.White.copy(0.8f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (walletStep == 1) {
                        Text(
                            text = txt("Enter your Mobile Account Number", "আপনার একাউন্ট মোবাইল নম্বর দিন", isEnglish),
                            fontSize = 13.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = walletMobileNumber,
                            onValueChange = { walletMobileNumber = it },
                            placeholder = { Text("e.g. 017XXXXXXXX") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White.copy(0.6f)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth().testTag("wallet_account_input")
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                if (walletMobileNumber.length >= 11) {
                                    walletVerificationCode = ""
                                    // Simulate sending OTP SMS
                                    scope.launch {
                                        walletStatusMsg = txt("Sending SMS Security Code...", "নিরাপত্তা কোড এসএমএস করা হচ্ছে...", isEnglish)
                                        delay(1500)
                                        walletVerificationCode = "529140" // Preset simulation OTP
                                        walletStatusMsg = ""
                                        walletStep = 2
                                    }
                                } else {
                                    Toast.makeText(context, "Enter correct number", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = txt("NEXT", "স্বীকার করুন", isEnglish), color = walletColor, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (walletStep == 2) {
                        Text(
                            text = txt("Enter 6-Digit SMS Verification OTP code", "এসএমএস এ পাঠানো যাচাইকরণ কোডটি লিখুন", isEnglish),
                            fontSize = 13.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = walletVerificationCode,
                            onValueChange = { walletVerificationCode = it },
                            placeholder = { Text("6-Digit OTP Code") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White.copy(0.6f)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().testTag("wallet_otp_input")
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                if (walletVerificationCode.isNotEmpty()) {
                                    walletStep = 3
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = txt("VERIFY", "যাচাই করুন", isEnglish), color = walletColor, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (walletStep == 3) {
                        Text(
                            text = txt("Enter secure 4/5-Digit Account PIN", "আপনার ব্যক্তিগত পিন কোড গোপন রাখুন", isEnglish),
                            fontSize = 13.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = walletPinNumber,
                            onValueChange = { walletPinNumber = it },
                            placeholder = { Text("Account PIN") },
                            visualTransformation = PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White.copy(0.6f)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            modifier = Modifier.fillMaxWidth().testTag("wallet_pin_input")
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                if (walletPinNumber.isNotEmpty()) {
                                    scope.launch {
                                        walletStatusMsg = txt("Verifying payment security gateway...", "গেটওয়েতে পেমেন্ট ট্রান্সফার হচ্ছে...", isEnglish)
                                        delay(2000)
                                        walletStatusMsg = ""
                                        // Save order to DB!
                                        viewModel.checkout(name, phone, address, selectedMethod, deliveryArea)
                                        walletStep = 4
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = txt("CONFIRM AND PAY NOW", "পেমেন্ট নিশ্চিত করুন", isEnglish), color = walletColor, fontWeight = FontWeight.Black)
                        }
                    }

                    if (walletStep == 4) {
                        Text(
                            text = txt("✔ Payment of ৳ ${grandTotal.toInt()} successful!", "✔ ৳ ${grandTotal.toInt()} পেমেন্ট সফল হয়েছে!", isEnglish),
                            fontSize = 15.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = txt("Order created successfully. Thank you for shopping with T-Shirt Zone BD!", "অর্ডার বুকিং সম্পন্ন হয়েছে। আমাদের সাথে শপিং করার জন্য ধন্যবাদ!", isEnglish),
                            fontSize = 11.sp,
                            color = Color.White.copy(0.9f),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                isWalletOverlayActive = false
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                        ) {
                            Text(text = txt("CLOSE", "বন্ধ করুন", isEnglish), color = walletColor, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (walletStatusMsg.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = walletStatusMsg, color = Color.White, fontSize = 11.sp)
                    }

                    if (walletStep < 4) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = txt("Cancel Gateway Session", "পেমেন্ট বাতিল করুন", isEnglish),
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { isWalletOverlayActive = false }
                                .padding(6.dp)
                        )
                    }
                }
            }
        }
    }
}


// ------ SCREEN 4: WISHLIST VIEW ------
@Composable
fun WishlistScreen(
    viewModel: ShopViewModel,
    isEnglish: Boolean,
    onNavigateBack: () -> Unit
) {
    val items by viewModel.wishlistItems.collectAsState()

    Scaffold(
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                Text(
                    text = txt("Your Saved Apparel", "আপনার প্রিয় কালেকশন", isEnglish),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    ) { innerPadding ->
        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Empty",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = txt("Your wishlist is empty", "প্রিয় তালিকাটি খালি", isEnglish),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(bottom = 80.dp)
            ) {
                items(items) { item ->
                    // Convert Wishlist item style representation to Card format
                    val product = ProductEntity(
                        id = item.productId,
                        title = item.title,
                        titleBn = item.titleBn,
                        price = item.price,
                        category = "Premium Design",
                        categoryBn = "প্রিমিয়াম ডিজাইন",
                        imageUrl = item.imageUrl,
                        description = "",
                        descriptionBn = ""
                    )

                    ProductCard(
                        product = product,
                        isEnglish = isEnglish,
                        viewModel = viewModel,
                        onClick = { /* Detail redirect */ }
                    )
                }
            }
        }
    }
}


// ------ SCREEN 5: USER REGISTER / LOGIN / PROFILE CONTROLS ------
@Composable
fun ProfileScreen(
    viewModel: ShopViewModel,
    isEnglish: Boolean,
    onNavigateToPolicy: (String) -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current

    // Register Inputs state
    var regName by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regAddress by remember { mutableStateOf("") }

    val adminModeEnabled by viewModel.isAdminMode.collectAsState()
    var adminPassword by remember { mutableStateOf("") }
    var showAdminLogin by remember { mutableStateOf(false) }

    val orders by viewModel.allOrders.collectAsState()

    // Filter orders by phone if user logged in
    val myOrders = remember(orders, authState) {
        if (authState is AuthState.Authenticated) {
            val phone = (authState as AuthState.Authenticated).user.phone
            orders.filter { it.customerPhone == phone }
        } else {
            emptyList()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("profile_screen_layout"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            Text(
                text = txt("My Account Profile", "আমার অ্যাকাউন্ট প্রোফাইল", isEnglish),
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        if (authState is AuthState.Unauthenticated) {
            // Profile Authentication / Register screen representation
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = txt("Simple Fast Registration", "সহজ কাস্টমার রেজিস্ট্রেশন", isEnglish),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = txt("Create account to keep track of premium deliveries in Bangladesh", "বাংলাদেশে টি-শার্ট অর্ডার ট্র্যাক করতে অ্যাকাউন্ট তৈরি করুন", isEnglish),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = regName,
                            onValueChange = { regName = it },
                            placeholder = { Text(text = txt("Your Name", "আপনার নাম", isEnglish), fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("reg_name_input")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = regPhone,
                            onValueChange = { regPhone = it },
                            placeholder = { Text(text = txt("Bangladeshi Mobile Phone", "মোবাইল নম্বর", isEnglish), fontSize = 12.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("reg_phone_input")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = regEmail,
                            onValueChange = { regEmail = it },
                            placeholder = { Text(text = txt("Email Address", "ইমেইল ঠিকানা", isEnglish), fontSize = 12.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("reg_email_input")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = regAddress,
                            onValueChange = { regAddress = it },
                            placeholder = { Text(text = txt("Exact delivery Address (Dhaka/Outside)", "ঠিকানা (ঢাকা বা ঢাকার বাইরে বিস্তারিত)", isEnglish), fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth().testTag("reg_address_input")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (regName.trim().isEmpty() || regPhone.trim().length < 11 || regAddress.trim().isEmpty()) {
                                    Toast.makeText(context, txt("Please fill valid registration details", "অনুগ্রহ করে নাম, সঠিক মোবাইল ও ঠিকানা পূরণ করুন", isEnglish), Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                viewModel.login(regName, regPhone, regEmail, regAddress)
                                Toast.makeText(context, txt("Registered Successfully!", "রেজিস্ট্রেশন সফল হয়েছে!", isEnglish), Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(46.dp).testTag("register_btn")
                        ) {
                            Text(text = txt("SUBMIT AND REGISTER", "সাবমিট ও রেজিস্টার করুন", isEnglish), color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // Logged in User Profile controls
            val user = (authState as AuthState.Authenticated).user

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user.name.take(1).uppercase(),
                                    color = Color.Black,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 24.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = user.name,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = user.phone,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline)

                        Text(
                            text = txt("Saved Shipping Details:", "সংরক্ষিত ডেলিভারি ঠিকানা:", isEnglish),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = user.address,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = txt("Valuable TZB Loyalty Reward Points: ${user.rewardPoints} points", "টিজেডবি লয়ালটি রিওয়ার্ড পয়েন্টস: ${user.rewardPoints} পয়েন্ট", isEnglish),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Become admin toggle
                            Button(
                                onClick = { showAdminLogin = !showAdminLogin },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = txt("Switch role", "অ্যাডমিন প্যানেল", isEnglish), color = MaterialTheme.colorScheme.primary, fontSize = 11.sp)
                            }

                            Button(
                                onClick = { viewModel.logout() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = txt("LOGOUT", "লগআউট", isEnglish), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Simulated admin password entry overlay
            if (showAdminLogin) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = txt("Enter Store Admin Passcode", "স্টোর অ্যাডমিন পাসকোড দিন", isEnglish),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = adminPassword,
                                onValueChange = { adminPassword = it },
                                placeholder = { Text("App Admin Passcode") },
                                visualTransformation = PasswordVisualTransformation(),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_passcode_input")
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = { showAdminLogin = false }) {
                                    Text(text = "Cancel", fontSize = 11.sp)
                                }
                                Button(
                                    onClick = {
                                        if (adminPassword == "admin" || adminPassword == "১৮২৮" || adminPassword == "1234") {
                                            viewModel.setAdminMode(true)
                                            showAdminLogin = false
                                            Toast.makeText(context, "Welcome Admin Manager!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Wrong passcode!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                ) {
                                    Text(text = "Unlock", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            // ORDER HISTORY FOR CURRENT LOGGED IN USER
            item {
                Text(
                    text = txt("Your Delivery Order History (${myOrders.size})", "আপনার অর্ডার ইতিহাস (${myOrders.size})", isEnglish),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 18.dp, bottom = 6.dp)
                )
            }

            if (myOrders.isEmpty()) {
                item {
                    Text(
                        text = txt("No orders placed yet. Purchase high-class T-shirts now!", "কোনো অর্ডার ইতিহাস পাওয়া যায়নি।", isEnglish),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            } else {
                items(myOrders) { o ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = o.orderNumber, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(
                                    text = o.status.uppercase(),
                                    color = when (o.status) {
                                        "Pending" -> Color.Yellow
                                        "Shipped" -> Color.Blue
                                        "Delivered" -> Color.Green
                                        else -> Color.Green
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = o.itemsSummary, fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Total Price: ৳ ${o.totalPrice.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}
