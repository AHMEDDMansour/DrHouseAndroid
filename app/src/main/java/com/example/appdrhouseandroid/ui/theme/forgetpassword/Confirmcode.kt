package com.example.appdrhouseandroid.ui.theme.forgetpassword

import androidx.compose.runtime.Composable
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appdrhouseandroid.Routes
import com.example.appdrhouseandroid.data.network.RetrofitClient
import com.example.appdrhouseandroid.data.network.VerifyCodeRequest
import com.example.appdrhouseandroid.data.network.VerifyCodeResponse
import kotlinx.coroutines.launch
import okhttp3.Route
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Composable
fun ConfirmCodeScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(color = MaterialTheme.colorScheme.primaryContainer)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Email Verification",
                style = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.onPrimaryContainer),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Enter your email and 4-digit verification code to proceed.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(bottom = 24.dp),
                textAlign = TextAlign.Center
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                isError = email.isEmpty()
            )

            OutlinedTextField(
                value = code,
                onValueChange = {
                    if (it.length <= 4) code = it
                },
                label = { Text("Verification Code") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                isError = code.length != 4
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (email.isNotEmpty() && code.length == 4) {
                        isLoading = true
                        coroutineScope.launch {
                            RetrofitClient.getApiService().verifyResetCode(VerifyCodeRequest(email, code))
                                .enqueue(object : Callback<VerifyCodeResponse> {
                                    override fun onResponse(
                                        call: Call<VerifyCodeResponse>,
                                        response: Response<VerifyCodeResponse>
                                    ) {
                                        isLoading = false
                                        if (response.isSuccessful) {
                                            val responseBody = response.body()
                                            if (responseBody?.message == "Reset code verified") {
                                                message = "Code verified successfully!"
                                                navController.navigate(Routes.ResetPass.route)
                                            } else {
                                                message = "Verification failed: ${responseBody?.message ?: "Unknown error"}"
                                            }
                                        } else {
                                            message = "Error: ${response.message()}"
                                        }
                                    }

                                    override fun onFailure(call: Call<VerifyCodeResponse>, t: Throwable) {
                                        isLoading = false
                                        message = "Error: ${t.localizedMessage}"
                                    }
                                })
                        }
                    } else {
                        message = "Please enter both your email and a 4-digit code."
                    }
                },
                enabled = email.isNotEmpty() && code.length == 4 && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(text = "Verify Code", color = MaterialTheme.colorScheme.onPrimary)
                }
            }

            if (message.isNotEmpty()) {
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}
