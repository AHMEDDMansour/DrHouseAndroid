package com.example.appdrhouseandroid

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun SignUp(navController: NavHostController) {
    val name = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val isNameValid = remember { mutableStateOf(true) }
    val isEmailValid = remember { mutableStateOf(true) }
    val isPasswordValid = remember { mutableStateOf(true) }

    fun validateEmail(input: String): Boolean {
        return input.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()
    }

    fun validateName(input: String): Boolean {
        return input.isNotBlank()
    }

    fun validatePassword(input: String): Boolean {
        val hasUpperCase = input.any { it.isUpperCase() }
        val hasLowerCase = input.any { it.isLowerCase() }
        val hasNumber = input.any { it.isDigit() }
        return input.isNotBlank() && hasUpperCase && hasLowerCase && hasNumber
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.backround),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().align(Alignment.Center),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 132.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Social Buttons

            OutlinedTextField(
                value = name.value,
                label = { Text(text = "Name") },
                onValueChange = {
                    name.value = it
                    isNameValid.value = validateName(it)
                },
                leadingIcon = { Icon(imageVector = Icons.Outlined.Person, contentDescription = "Name") },
                isError = !isNameValid.value,
                placeholder = { Text(text = "Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            )
            if (!isNameValid.value) {
                Text(
                    text = "Name cannot be empty",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 20.dp, bottom = 8.dp)
                )
            }

            OutlinedTextField(
                value = email.value,
                label = { Text(text = "Email") },
                onValueChange = {
                    email.value = it
                    isEmailValid.value = validateEmail(it)
                },
                leadingIcon = { Icon(imageVector = Icons.Outlined.Email, contentDescription = "Email") },
                isError = !isEmailValid.value,
                placeholder = { Text(text = "Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            )
            if (!isEmailValid.value) {
                Text(
                    text = "Enter a valid email address",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 20.dp, bottom = 8.dp)
                )
            }

            OutlinedTextField(
                value = password.value,
                label = { Text(text = "Password") },
                onValueChange = {
                    password.value = it
                    isPasswordValid.value = validatePassword(it)
                },
                leadingIcon = { Icon(imageVector = Icons.Outlined.Email, contentDescription = "Password") },
                isError = !isPasswordValid.value,
                placeholder = { Text(text = "Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            )
            if (!isPasswordValid.value) {
                Text(
                    text = "Password must contain uppercase, lowercase, and a number",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 20.dp, bottom = 8.dp)
                )
            }

            Button(
                onClick = {
                    if (isNameValid.value && isEmailValid.value && isPasswordValid.value) {
                        // Implement sign-up logic
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp, vertical = 20.dp)
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2980B9)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Sign Up")
            }
        }
    }
}
