package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.viewmodel.ShopViewModel

@Composable
fun AdminDashboardScreen(
    viewModel: ShopViewModel,
    isEnglish: Boolean,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var activeTab by remember { mutableStateOf("Products") } // Products, Orders, Policies

    val products by viewModel.productsList.collectAsState()
    val orders by viewModel.allOrders.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<ProductEntity?>(null) }

    Scaffold(
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(8.dp)
            ) {
                IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("admin_back_btn")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = txt("Secure Admin Controls Dashboard", "অ্যাডমিন কন্ট্রোল ড্যাশবোর্ড", isEnglish),
                    fontSize = 17.sp,
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
                .padding(bottom = 80.dp)
        ) {
            // Tab Selection Rows
            ScrollableTabRow(
                selectedTabIndex = when (activeTab) {
                    "Products" -> 0
                    "Orders" -> 1
                    else -> 2
                },
                contentColor = MaterialTheme.colorScheme.primary,
                containerColor = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = activeTab == "Products",
                    onClick = { activeTab = "Products" },
                    text = { Text(text = txt("Products Manager", "পণ্য ব্যবস্থাপনা", isEnglish), fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = activeTab == "Orders",
                    onClick = { activeTab = "Orders" },
                    text = { Text(text = txt("Orders Pipeline", "অর্ডার পাইপলাইন", isEnglish), fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = activeTab == "Policies",
                    onClick = { activeTab = "Policies" },
                    text = { Text(text = txt("Policy HTML Editor", "আইনি পলিসি এডিটর", isEnglish), fontWeight = FontWeight.Bold) }
                )
            }

            // Tab contents rendering
            when (activeTab) {
                "Products" -> {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Button(
                            onClick = { showAddDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .testTag("admin_add_product_trigger")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = Color.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = txt("Add New Premium T-Shirt Model", "নতুন টি-শার্ট কালেকশন যুক্ত করুন", isEnglish), color = Color.Black)
                        }

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(products) { p ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = if (isEnglish) p.title else p.titleBn, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text(text = "Price: ৳ ${p.price.toInt()} | Stock: ${p.stock}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                        }

                                        IconButton(onClick = { editingProduct = p }) {
                                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                                        }

                                        IconButton(onClick = {
                                            viewModel.deleteProduct(p)
                                            Toast.makeText(context, "Product deleted", Toast.LENGTH_SHORT).show()
                                        }) {
                                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "Orders" -> {
                    if (orders.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = "No orders received yet", color = MaterialTheme.colorScheme.primary)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(orders) { o ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(0.4f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = o.orderNumber, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                            Text(text = "৳ ${o.totalPrice.toInt()}", fontWeight = FontWeight.Black)
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = "Buyer: ${o.customerName} (${o.customerPhone})", fontSize = 12.sp)
                                        Text(text = "Address: ${o.deliveryAddress}", fontSize = 12.sp)
                                        Text(text = "Items: ${o.itemsSummary}", fontSize = 11.sp, color = Color.Gray)

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Toggle Order Status
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.background(MaterialTheme.colorScheme.background, RoundedCornerShape(6.dp))
                                            ) {
                                                Text(
                                                    text = "Status: ${o.status}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(8.dp)
                                                )
                                                IconButton(
                                                    onClick = {
                                                        val next = when (o.status) {
                                                            "Pending" -> "Shipped"
                                                            "Shipped" -> "Delivered"
                                                            else -> "Pending"
                                                        }
                                                        viewModel.updateOrderStatus(o.id, next)
                                                    },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = "Next State", modifier = Modifier.size(16.dp))
                                                }
                                            }

                                            // Toggle Payment Status
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.background(MaterialTheme.colorScheme.background, RoundedCornerShape(6.dp))
                                            ) {
                                                Text(
                                                    text = "Payment: ${o.paymentStatus}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(8.dp)
                                                )
                                                IconButton(
                                                    onClick = {
                                                        val next = if (o.paymentStatus == "Paid") "Unpaid" else "Paid"
                                                        viewModel.updateOrderPaymentStatus(o.id, next)
                                                    },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(imageVector = Icons.Default.Paid, contentDescription = "Paytoggle", modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "Policies" -> {
                    PolicyHtmlEditor(viewModel, isEnglish)
                }
            }
        }
    }

    // Modal Add Product Dialog
    if (showAddDialog) {
        ProductModifyDialog(
            isEnglish = isEnglish,
            onDismiss = { showAddDialog = false },
            onSave = {
                viewModel.addProduct(it)
                showAddDialog = false
            }
        )
    }

    // Modal Edit Product Dialog
    if (editingProduct != null) {
        ProductModifyDialog(
            product = editingProduct,
            isEnglish = isEnglish,
            onDismiss = { editingProduct = null },
            onSave = {
                viewModel.editProduct(it)
                editingProduct = null
            }
        )
    }
}

// ------ PRODUCT ADD/EDIT FIELD MANAGER DIALOG ------
@Composable
fun ProductModifyDialog(
    product: ProductEntity? = null,
    isEnglish: Boolean,
    onDismiss: () -> Unit,
    onSave: (ProductEntity) -> Unit
) {
    var title by remember { mutableStateOf(product?.title ?: "") }
    var titleBn by remember { mutableStateOf(product?.titleBn ?: "") }
    var description by remember { mutableStateOf(product?.description ?: "") }
    var descriptionBn by remember { mutableStateOf(product?.descriptionBn ?: "") }
    var price by remember { mutableStateOf(product?.price?.toString() ?: "990") }
    var category by remember { mutableStateOf(product?.category ?: "Streetwear Collection") }
    var categoryBn by remember { mutableStateOf(product?.categoryBn ?: "স্ট্রিটওয়্যার কালেকশন") }
    var imageUrl by remember { mutableStateOf(product?.imageUrl ?: "img_premium_gold_tshirt") }
    var isPremium by remember { mutableStateOf(product?.isPremium ?: false) }
    var rating by remember { mutableStateOf(product?.rating?.toString() ?: "4.8") }
    var stock by remember { mutableStateOf(product?.stock?.toString() ?: "20") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (product == null) "Create New T-shirt" else "Edit Core Apparel Profile",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title (EN)") },
                    modifier = Modifier.fillMaxWidth().testTag("add_title_en")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = titleBn,
                    onValueChange = { titleBn = it },
                    label = { Text("Title (BN)") },
                    modifier = Modifier.fillMaxWidth().testTag("add_title_bn")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (EN)") },
                    modifier = Modifier.fillMaxWidth().height(80.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = descriptionBn,
                    onValueChange = { descriptionBn = it },
                    label = { Text("Description (BN)") },
                    modifier = Modifier.fillMaxWidth().height(80.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Price (BDT)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("add_price_input")
                    )
                    OutlinedTextField(
                        value = stock,
                        onValueChange = { stock = it },
                        label = { Text("Stock") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category (EN)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = categoryBn,
                        onValueChange = { categoryBn = it },
                        label = { Text("Category (BN)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Drawable Resource Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isPremium, onCheckedChange = { isPremium = it })
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Exquisite Premium Launch Gold Label", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            if (title.isEmpty() || titleBn.isEmpty() || price.toDoubleOrNull() == null) return@Button
                            onSave(
                                ProductEntity(
                                    id = product?.id ?: 0,
                                    title = title,
                                    titleBn = titleBn,
                                    description = description,
                                    descriptionBn = descriptionBn,
                                    price = price.toDoubleOrNull() ?: 990.0,
                                    category = category,
                                    categoryBn = categoryBn,
                                    imageUrl = imageUrl,
                                    isPremium = isPremium,
                                    rating = rating.toDoubleOrNull() ?: 4.8,
                                    stock = stock.toIntOrNull() ?: 20
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Save Apparel", color = Color.Black)
                    }
                }
            }
        }
    }
}


// ------ RICH TEXT POLICY MANAGER (HTML-STYLE TEXT EDITOR) ------
@Composable
fun PolicyHtmlEditor(
    viewModel: ShopViewModel,
    isEnglish: Boolean
) {
    val context = LocalContext.current
    var selectedPolicy by remember { mutableStateOf("privacy") } // privacy, refund, terms

    // Keep dynamic track of values
    val pageFlow = remember(selectedPolicy) { viewModel.observeLegalPage(selectedPolicy) }
    val activePage by pageFlow.collectAsState(initial = null)

    var editTitleEn by remember { mutableStateOf("") }
    var editTitleBn by remember { mutableStateOf("") }
    var editContentEn by remember { mutableStateOf("") }
    var editContentBn by remember { mutableStateOf("") }

    var isPreviewActive by remember { mutableStateOf(false) }

    LaunchedEffect(activePage) {
        activePage?.let {
            editTitleEn = it.titleEn
            editTitleBn = it.titleBn
            editContentEn = it.contentEn
            editContentBn = it.contentBn
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = txt("Select Legal Document to Edit", "সম্পাদনার জন্য আইনি নথি নির্বাচন করুন", isEnglish),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val docs = listOf("privacy", "refund", "terms")
            docs.forEach { doc ->
                val label = when (doc) {
                    "privacy" -> txt("Privacy Policy", "গোপনীয়তা নীতি", isEnglish)
                    "refund" -> txt("Refund Policy", "রিফান্ড নীতি", isEnglish)
                    else -> txt("Terms & Cond.", "শর্তাবলী ও নিয়ম", isEnglish)
                }

                Box(
                    modifier = Modifier
                        .clickable { selectedPolicy = doc }
                        .background(
                            color = if (selectedPolicy == doc) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(1.dp, MaterialTheme.colorScheme.outline)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedPolicy == doc) Color.Black else MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Rich Editor Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = txt("Rich HTML Code Editor Canvas", "সমৃদ্ধ এইচটিএমএল কোড এডিটর", isEnglish),
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )

            // Preview Toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = txt("Preview mode", "প্রিভিউ মোড", isEnglish), fontSize = 11.sp)
                Switch(checked = isPreviewActive, onCheckedChange = { isPreviewActive = it })
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (isPreviewActive) {
            // Live Formatted HTML-like Preview Section
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.3f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = txt("LIVE PREVIEW", "লাইভ প্রিভিউ", isEnglish),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Text(
                        text = if (isEnglish) editTitleEn else editTitleBn,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HtmlTextParser(html = if (isEnglish) editContentEn else editContentBn)
                }
            }
        } else {
            // Standard Text Field editor containers
            Column {
                OutlinedTextField(
                    value = editTitleEn,
                    onValueChange = { editTitleEn = it },
                    label = { Text("Document Title (English)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_policy_title_en")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = editTitleBn,
                    onValueChange = { editTitleBn = it },
                    label = { Text("ডকুমেন্ট শিরোনাম (বাংলা)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_policy_title_bn")
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = editContentEn,
                    onValueChange = { editContentEn = it },
                    label = { Text("Content Body Tags EN (HTML tags like <h3> and <p> supported)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .testTag("edit_policy_content_en")
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = editContentBn,
                    onValueChange = { editContentBn = it },
                    label = { Text("আইনি বিবরণী বডি BN (<h3> ও <p> এইচটিএমএল ট্যাগ সমর্থন করবে)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .testTag("edit_policy_content_bn")
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                viewModel.saveLegalPage(selectedPolicy, editTitleEn, editTitleBn, editContentEn, editContentBn)
                Toast.makeText(context, txt("Policy saved successfully!", "আইনি নথি সফলভাবে সংরক্ষিত হয়েছে!", isEnglish), Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("save_policy_btn")
        ) {
            Icon(imageVector = Icons.Default.Save, contentDescription = "Save", tint = Color.Black)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = txt("COMMIT & SAVE TO CLOUD/DB", "সংরক্ষণ করুন (কমিট করুন)", isEnglish), color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}


// ------ POLICY VIEWER SINGLE COMPONENT ------
@Composable
fun PolicyViewerScreen(
    policyId: String,
    viewModel: ShopViewModel,
    isEnglish: Boolean,
    onNavigateBack: () -> Unit
) {
    val pageFlow = remember(policyId) { viewModel.observeLegalPage(policyId) }
    val page by pageFlow.collectAsState(initial = null)

    Scaffold(
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(12.dp)
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = page?.let { if (isEnglish) it.titleEn else it.titleBn } ?: txt("Legal details", "আইনি শর্তাবলী", isEnglish),
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
                .padding(16.dp)
        ) {
            val titleText = page?.let { if (isEnglish) it.titleEn else it.titleBn } ?: "TZB Legal Details"
            val contentText = page?.let { if (isEnglish) it.contentEn else it.contentBn } ?: txt("Loading...", "অপেক্ষা করুন...", isEnglish)

            Text(
                text = titleText,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(bottom = 14.dp))

            HtmlTextParser(html = contentText)
        }
    }
}


// ------ SUB-COMPONENT: A HYBRID XML/MARKDOWN/HTML TEXT PARSER FOR ANDROID COMPOSE ------
@Composable
fun HtmlTextParser(html: String) {
    val lines = remember(html) {
        val list = mutableListOf<ParsedLine>()
        // Parse raw text for <h3> tags, <p> tags, or plain lines
        var index = 0
        while (index < html.length) {
            val h3Start = html.indexOf("<h3>", index)
            val pStart = html.indexOf("<p>", index)

            if (h3Start == -1 && pStart == -1) {
                val rem = html.substring(index).trim()
                if (rem.isNotEmpty()) list.add(ParsedLine(rem, LineStyle.NORMAL))
                break
            }

            if (h3Start != -1 && (pStart == -1 || h3Start < pStart)) {
                val h3End = html.indexOf("</h3>", h3Start)
                if (h3End != -1) {
                    val text = html.substring(h3Start + 4, h3End)
                    list.add(ParsedLine(text, LineStyle.HEADER))
                    index = h3End + 5
                } else {
                    list.add(ParsedLine(html.substring(h3Start), LineStyle.NORMAL))
                    break
                }
            } else {
                val pEnd = html.indexOf("</p>", pStart)
                if (pEnd != -1) {
                    val text = html.substring(pStart + 3, pEnd)
                    list.add(ParsedLine(text, LineStyle.NORMAL))
                    index = pEnd + 4
                } else {
                    list.add(ParsedLine(html.substring(pStart), LineStyle.NORMAL))
                    break
                }
            }
        }
        list
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        lines.forEach { pl ->
            when (pl.style) {
                LineStyle.HEADER -> {
                    Text(
                        text = pl.text,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
                LineStyle.NORMAL -> {
                    Text(
                        text = pl.text,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

enum class LineStyle { HEADER, NORMAL }
data class ParsedLine(val text: String, val style: LineStyle)
