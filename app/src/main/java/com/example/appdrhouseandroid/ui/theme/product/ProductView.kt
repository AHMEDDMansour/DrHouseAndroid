    package com.example.appdrhouseandroid.ui.theme.product

    import android.net.Uri
    import android.widget.Toast
    import androidx.activity.compose.rememberLauncherForActivityResult
    import androidx.activity.result.contract.ActivityResultContracts
    import androidx.compose.foundation.*
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.lazy.grid.*
    import androidx.compose.foundation.shape.*
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.*
    import androidx.compose.material3.Button
    import androidx.compose.material3.ButtonDefaults
    import androidx.compose.material3.Card
    import androidx.compose.material3.CardDefaults
    import androidx.compose.material3.CircularProgressIndicator
    import androidx.compose.material3.Icon
    import androidx.compose.material3.IconButton
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.OutlinedTextField
    import androidx.compose.material3.Text
    import androidx.compose.runtime.*
    import androidx.compose.ui.*
    import androidx.compose.ui.draw.clip
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.unit.dp
    import androidx.lifecycle.viewmodel.compose.viewModel
    import coil.compose.rememberAsyncImagePainter
    import com.example.appdrhouseandroid.data.network.ProductResponse
    import androidx.compose.material3.Surface
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.geometry.Offset
    import androidx.compose.ui.geometry.Size
    import androidx.compose.ui.graphics.Outline.Rectangle
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.text.style.TextOverflow
    import androidx.compose.ui.window.Dialog
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.ViewModelProvider
    import androidx.navigation.NavController
    import coil.compose.AsyncImagePainter
    import coil.compose.rememberImagePainter
    import com.example.appdrhouseandroid.data.network.ApiService
    import com.example.appdrhouseandroid.data.repositories.OcrRepository
    import com.example.appdrhouseandroid.ui.theme.OCR.OCRViewModel

    class OCRViewModelFactory(private val apiService: OcrRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(OCRViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return OCRViewModel(apiService) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
    @Composable
    fun ProductView(
        navController: NavController,
        viewModel: ProductViewModel = viewModel(),
        ocrViewModel: OCRViewModel // Pass OCRViewModel as a parameter
    ) {
        // Observing product state from ViewModel
        val products by viewModel.products.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()
        val error by viewModel.error.collectAsState()

        // States for filtering and searching
        val categories = listOf("All", "Vitamins", "MultiVit", "Minerals", "Bio-Meds", "Supplements", "Protein")
        var selectedCategory by remember { mutableStateOf("All") }
        var searchQuery by remember { mutableStateOf("") }
        val context = LocalContext.current

        //searchQuery = medicineName
        var selectedProduct by remember { mutableStateOf<ProductResponse?>(null) }

        // Automatically load products based on selected category
        LaunchedEffect(selectedCategory) {
            if (selectedCategory == "All") {
                viewModel.loadAllProducts()
            } else {
                viewModel.loadByCategory(selectedCategory)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            // Button to view the cart
            ViewCartButton(navController = navController)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Search bar for manual input
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    modifier = Modifier.weight(1f)
                )
                CameraButton(
                    ocrViewModel = ocrViewModel, // Pass the view model instance
                    onOCRResult = { extractedName ->
                        // Update the search query with the extracted medicine name
                        searchQuery = extractedName
                    }
                )

            }

            // Category filter for products
            CategoryFilter(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it },
                modifier = Modifier.padding(vertical = 18.dp)
            )

            // Handle loading, error, or display product grid
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (error != null) {
                Text(
                    text = error ?: "",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                // Display products filtered by category and search query
                ProductGrid(
                    products = products.filter {
                        (selectedCategory == "All" || it.category == selectedCategory) &&
                                it.name.contains(searchQuery, ignoreCase = true)
                    },
                    onProductClicked = { product -> selectedProduct = product }
                )
            }

            // Display product details dialog when a product is selected
            selectedProduct?.let {
                ProductDetailDialog(product = it, onDismiss = { selectedProduct = null })
            }
        }
    }

    @Composable
    fun CameraButton(
        ocrViewModel: OCRViewModel, // Pass OCRViewModel to handle OCR
        onOCRResult: (String) -> Unit // Callback to update the search query
    ) {
        val context = LocalContext.current


        // Image picker launcher
        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                // Convert URI to MultipartBody.Part for OCR upload
                val imagePart = ocrViewModel.createImagePart(context, it)
                if (imagePart != null) {
                    ocrViewModel.uploadImage(imagePart) // Upload image to backend for OCR
                } else {
                    Toast.makeText(context, "Failed to prepare image.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Observe OCR result from ViewModel
        val ocrResult by ocrViewModel.ocrResult.collectAsState()

        // Update search query with OCR result if successful
        LaunchedEffect(ocrResult) {
            ocrResult?.let { response ->
                if (response.isSuccessful) {
                    response.body()?.let { ocrResponse ->
                        onOCRResult(ocrResponse.text) // Pass OCR result to update the search query
                    }
                } else {
                    Toast.makeText(context, "OCR failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Camera button UI
        IconButton(onClick = { launcher.launch("image/*") }) {
            Icon(imageVector = Icons.Default.Camera, contentDescription = "Camera")
        }


    }



    @Composable
    fun SearchBar(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search products...") },
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
            },
            shape = RoundedCornerShape(16.dp)
        )
    }



    @Composable
    fun ProductDetailDialog(product: ProductResponse, onDismiss: () -> Unit) {
        val imageUrl = "http://192.168.100.12:3000" + product.image // Replace with your local IP address

        Dialog(onDismissRequest = onDismiss) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    if (product.image.isNullOrEmpty()) {
                        Text(
                            text = "Image not available",
                            color = Color(0xFFD3D3D3),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .padding(8.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    } else {
                        Image(
                            painter = rememberAsyncImagePainter(model = imageUrl),
                            contentDescription = "Product Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp) // Slightly reduced height
                                .clip(RoundedCornerShape(topStart = 1.dp, topEnd = 1.dp))
                                .align(Alignment.CenterHorizontally)
                        )
                    }

                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Price : $${product.price}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Category : $${product.category}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Description : ${product.description}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { /* Add to cart logic */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009AEE)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Add to Cart",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add to Cart",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                }
            }
        }
    }


    @Composable
    fun CategoryFilter(
        categories: List<String>,
        selectedCategory: String,
        onCategorySelected: (String) -> Unit,
        modifier: Modifier = Modifier
    ) {
        LazyHorizontalGrid(
            rows = GridCells.Fixed(1), // Single row for horizontal scrolling
            modifier = modifier
                .fillMaxWidth()
                .height(60.dp) // Limit height to prevent it from filling the interface
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.Center // Align items vertically within the row
        ) {
            items(categories) { category ->
                val isSelected = category == selectedCategory
                Button(
                    onClick = { onCategorySelected(category) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color(0xFF009AEE) else Color.White,
                        contentColor = if (isSelected) Color.White else Color(0xFF009AEE)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .wrapContentHeight() // Ensure buttons only take as much height as they need
                        .padding(4.dp)
                ) {
                    Text(text = category)
                }
            }
        }
    }

    @Composable
    fun ProductGrid(products: List<ProductResponse>, onProductClicked: (ProductResponse) -> Unit) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(1.dp),
            modifier = Modifier
                .padding(vertical = 14.dp),
        ) {
            items(products) { product ->
                ProductCard(product = product, onProductClicked = onProductClicked)
              //  ProductDetailDialog(product = it, onDismiss = { selectedProduct = null })

            }
        }
    }

    @Composable
    fun ProductCard(product: ProductResponse, onProductClicked: (ProductResponse) -> Unit) {
        val imageUrl = "http://192.168.100.12:3000" + product.image // Replace with your local IP address

        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable { onProductClicked(product) }, // Make card clickable
            elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (product.image.isNullOrEmpty()) {
                    Text(
                        text = "Image not available",
                        color = Color(0xFFD3D3D3),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(model = imageUrl),
                        contentDescription = "Product Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp) // Slightly reduced height
                            .clip(RoundedCornerShape(topStart = 1.dp, topEnd = 1.dp))
                            .align(Alignment.CenterHorizontally)
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 1.dp) // Reduced top padding
                    )

                    Text(
                        text = "$${product.price}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF009AEE)
                        ),
                        modifier = Modifier.padding(top = 4.dp) // Reduced space
                    )
                }
            }
        }
    }

    @Composable
    fun ViewCartButton(navController: NavController) {

        Button(
            onClick = {navController.navigate("CartView") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009AEE)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

        ) {
            Text(text = "View Cart", color = Color.White) //a revoire
        }
    }



