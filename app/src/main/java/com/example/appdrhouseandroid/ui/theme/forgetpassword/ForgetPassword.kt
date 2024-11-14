package com.example.appdrhouseandroid.ui.theme.forgetpassword

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appdrhouseandroid.Routes
import com.example.appdrhouseandroid.data.network.ForgotPasswordRequest
import com.example.appdrhouseandroid.data.network.ForgotPasswordResponse
import com.example.appdrhouseandroid.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun ForgetPassword(navController: NavController) {
    val email = remember { mutableStateOf("") }
    val isEmailValid = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val resetMessage = remember { mutableStateOf("") }
    val showSuccessMessage = remember { mutableStateOf(false) }

    fun validateEmail(input: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(Color(0xFFF4F5F7))
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .background(color = Color.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Reset Password",
                        color = Color(0xFF333333),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Enter your email to receive password reset instructions.",
                        color = Color(0xFF666666),
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    TextField(
                        value = email.value,
                        onValueChange = {
                            email.value = it
                            isEmailValid.value = validateEmail(it)
                        },
                        placeholder = { Text("Email") },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = if (isEmailValid.value) Color.Green else Color.Gray,
                            unfocusedIndicatorColor = if (isEmailValid.value) Color.Green else Color.LightGray
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .padding(vertical = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (isEmailValid.value) {
                                coroutineScope.launch(Dispatchers.IO) {
                                    val request = ForgotPasswordRequest(email.value)
                                    RetrofitClient.getApiService().forgotPassword(request)
                                        .enqueue(object : Callback<ForgotPasswordResponse> {
                                            override fun onResponse(
                                                call: Call<ForgotPasswordResponse>,
                                                response: Response<ForgotPasswordResponse>
                                            ) {
                                                if (response.isSuccessful) {
                                                    resetMessage.value = "Reset email sent successfully."
                                                    showSuccessMessage.value = true
                                                    navController.navigate(Routes.ConfirmCode.route)
                                                } else {
                                                    resetMessage.value = "Failed to send reset email."
                                                    showSuccessMessage.value = false
                                                }
                                            }

                                            override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
                                                resetMessage.value = "Error: ${t.message}"
                                                showSuccessMessage.value = false
                                            }
                                        })
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF677294)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Text("Send Reset Token", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (resetMessage.value.isNotEmpty()) {
                        Text(
                            text = resetMessage.value,
                            color = if (showSuccessMessage.value) Color(0xFF4CAF50) else Color(0xFFD32F2F),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
