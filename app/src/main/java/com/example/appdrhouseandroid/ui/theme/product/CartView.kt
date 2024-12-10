package com.example.appdrhouseandroid.ui.theme.product

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appdrhouseandroid.data.network.ProductResponse

@Composable
fun CartView(cartViewModel: CartViewModel = viewModel()) {
    val cartItems = cartViewModel.cartItems
    val totalPrice = cartViewModel.calculateTotalPrice()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        HeaderCartSection()
//        Text(
//            text = " (Cart)",
//            style = MaterialTheme.typography.headlineSmall,
//            modifier = Modifier.padding(bottom = 16.dp)
//        )

        // Show products in the cart
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp) // To avoid overlap with total price
        ) {
            items(cartItems.size) { index ->
                val product = cartItems[index] // Get the product from the list by index
                CartProductCard(
                    product = product,
                    onRemove = { cartViewModel.removeFromCart(product) }
                )
            }
        }

        // Show total price
        Text(
            text = "Total: $${totalPrice}",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.End)
        )
    }
}
@Composable
fun HeaderCartSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF4CAF50)) // Green background color
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Cart",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFFFFFFFF)
                )
                Text(
                    text = "Track your meds!",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            // Placeholder for Profile Picture
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
        }
    }
}

@Composable
fun CartProductCard(product: ProductResponse, onRemove: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { /* Handle item click, if needed */ }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "$${product.price}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF4CAF50)
            )

            IconButton(onClick = { onRemove() }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove")
            }
        }
    }
}
