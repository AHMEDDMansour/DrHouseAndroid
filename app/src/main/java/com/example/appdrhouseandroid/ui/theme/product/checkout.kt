package com.example.appdrhouseandroid.ui.theme.product


import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CheckoutScreen(
    viewModel: ProductViewModel,
    onOrderSuccess: () -> Unit,
    onOrderError: (String) -> Unit,

    ) {
    // Correctly collect states
    val orderCreationState by viewModel.orderCreationState.collectAsState()
    val cartItems = viewModel.cartItems
    val totalPrice by remember { derivedStateOf { viewModel.calculateTotalPrice() } }

    LaunchedEffect(orderCreationState) {
        when (orderCreationState) {
            is OrderCreationState.Success -> {
                onOrderSuccess()
                viewModel.resetOrderCreationState()
            }
            is OrderCreationState.Error -> {
                onOrderError((orderCreationState as OrderCreationState.Error).message)
                viewModel.resetOrderCreationState()
            }
            else -> {} // Handle other states if needed
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Cart Items List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(cartItems) { item ->
                CartItemView(
                    cartItem = item,
                    onQuantityChange = { newQuantity ->
                        viewModel.updateCartItemQuantity(item, newQuantity)
                    },
                    onRemove = {
                        viewModel.removeFromCart(item)
                    }
                )
            }
        }

        // Total and Checkout Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text(
                text = "Total: $${String.format("%.2f", totalPrice)}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(
                onClick = {   Log.d("CheckoutScreen", "Cart contents before order: ${viewModel.cartItems}")
                    viewModel.createOrder() },
                enabled = cartItems.isNotEmpty() && orderCreationState !is OrderCreationState.Creating,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = when(orderCreationState) {
                        is OrderCreationState.Creating -> "Creating Order..."
                        else -> "Place Order"
                    }
                )
            }
        }
    }
}

@Composable
fun CartItemView(
    cartItem: CartItemUI,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = cartItem.product.name, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "$${String.format("%.2f", cartItem.product.price)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = { onQuantityChange(cartItem.quantity - 1) }) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease quantity")
                }

                Text(text = cartItem.quantity.toString())

                IconButton(onClick = { onQuantityChange(cartItem.quantity + 1) }) {
                    Icon(Icons.Default.Add, contentDescription = "Increase quantity")
                }

                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove item")
                }
            }
        }
    }
}