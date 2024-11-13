package com.example.appdrhouseandroid.ui.theme.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.appdrhouseandroid.BottomNavigationItems
import com.example.appdrhouseandroid.R
import java.util.regex.Pattern
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.onFocusChanged
import com.example.appdrhouseandroid.Routes
import kotlinx.coroutines.launch
@Composable
fun Login(navController: NavHostController, viewModel: LoginViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val emailError = remember { mutableStateOf<String?>(null) }
    val passwordError = remember { mutableStateOf<String?>(null) }

    val emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$")
    val loginUiState by viewModel.loginUiState.observeAsState(LoginUiState())

    // Flags to track focus on each text field
    var emailFocused by remember { mutableStateOf(false) }
    var passwordFocused by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.backround),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 132.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "Welcome back",
                color = Color(0xFF000000),
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(bottom = 13.dp, start = 108.dp)
            )

            Text(
                "You can search courses, apply for courses, and find\n scholarships for abroad studies",
                color = Color(0xFF677294),
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(bottom = 76.dp, start = 46.dp, end = 46.dp)
                    .width(283.dp)
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(bottom = 37.dp, start = 20.dp, end = 20.dp)
                    .fillMaxWidth()
            ) {
                SocialButtonWithIcon(label = "Google", iconRes = R.drawable.google)
                SocialButtonWithIcon(label = "Facebook", iconRes = R.drawable.facebok1)
            }

            // Email Field
            OutlinedTextField(
                value = email,
                label = { Text(text = "Email") },
                onValueChange = { email = it },
                leadingIcon = { Icon(imageVector = Icons.Outlined.Email, contentDescription = "Email") },
                placeholder = { Text(text = "Email") },
                isError = emailError.value != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .onFocusChanged { focusState ->
                        emailFocused = focusState.isFocused
                        if (!focusState.isFocused && email.isEmpty()) {
                            emailError.value = "Email cannot be empty"
                        } else if (!focusState.isFocused && !emailPattern.matcher(email).matches()) {
                            emailError.value = "Please enter a valid email"
                        } else if (focusState.isFocused) {
                            emailError.value = null
                        }
                    }
            )
            emailError.value?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 30.dp, bottom = 8.dp)
                )
            }

            // Password Field
            OutlinedTextField(
                value = password,
                label = { Text(text = "Password") },
                onValueChange = { password = it },
                placeholder = { Text(text = "Password") },
                visualTransformation = PasswordVisualTransformation(),
                isError = passwordError.value != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .onFocusChanged { focusState ->
                        passwordFocused = focusState.isFocused
                        if (!focusState.isFocused && password.isEmpty()) {
                            passwordError.value = "Password cannot be empty"
                        } else if (!focusState.isFocused && password.length < 6) {
                            passwordError.value = "Password must be at least 6 characters"
                        } else if (focusState.isFocused) {
                            passwordError.value = null
                        }
                    }
            )
            passwordError.value?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 30.dp, bottom = 8.dp)
                )
            }

            Button(
                onClick = {
                    // Trigger login action
                    viewModel.loginUser(email, password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp, vertical = 20.dp)
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2980B9)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Login")
            }

            // Loading Indicator
            if (loginUiState.isLoading) {
                CircularProgressIndicator()
            }

            // Show Snackbar on successful login
            if (loginUiState.isLoggedIn) {
                LaunchedEffect(loginUiState.isLoggedIn) {
                    if (loginUiState.isLoggedIn) {
                        snackbarHostState.showSnackbar("Login successful! Welcome back.")
                        navController.navigate(BottomNavigationItems.Home.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            }

            // Forgot password link
            Text(
                "Forgot password",
                color = Color(0xFF2980B9),
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(start = 135.dp)
            )

            // Sign-up navigation link
            Text(
                "Don’t have an account? Join us",
                color = Color(0xFF2980B9),
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(top = 16.dp) // Adjust top padding to ensure it’s visible
                    .align(Alignment.CenterHorizontally) // Align it horizontally at the center
                    .clickable {
                        navController.navigate(Routes.SignUp.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
            )
        }

        // Snackbar Host for feedback
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun SocialButtonWithIcon(label: String, iconRes: Int) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(shape = RoundedCornerShape(12.dp))
            .width(160.dp)
            .background(color = Color.White, shape = RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp)
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier
                .padding(end = 8.dp)
                .size(18.dp)
        )
        Text(
            label,
            color = Color(0xFF677294),
            fontSize = 16.sp,
        )
    }
}
