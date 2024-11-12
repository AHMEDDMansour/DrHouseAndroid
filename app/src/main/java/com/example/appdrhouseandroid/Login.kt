package com.example.appdrhouseandroid

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun Login() {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }


    val isEmailValid = remember { mutableStateOf(false) }
    val isPasswordValid = remember { mutableStateOf(false) }
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
                "You can search course, apply course and find\n scholarship for abroad studies",
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
                SocialButton(
                    label = "Google",
                    iconRes = R.drawable.google
                )
                SocialButton(
                    label = "Facebook",
                    iconRes = R.drawable.facebok1
                )
            }

            OutlinedTextField(
                value = email.value,
                label = { Text(text = "Email") },
                onValueChange = { email.value = it },
                leadingIcon = { Icon(imageVector = Icons.Outlined.Email, contentDescription = "Email") },
                placeholder = { Text(text = "Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            )
            OutlinedTextField(
                value = password.value,
                label = { Text(text = "Password") },
                onValueChange = {
                    password.value = it
                },
                leadingIcon = { Icon(imageVector = Icons.Outlined.Email, contentDescription = "Email") },
                trailingIcon = {

                },
                placeholder = { Text(text = "Password") },

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            )




            Button(
                onClick = {  },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp, vertical = 20.dp) // Reduce horizontal padding to make the button wider
                    .height(54.dp), // Optional: Increase the height if needed
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2980B9)),
                shape = RoundedCornerShape(12.dp) // Adjust the corner radius as needed
            ) {
                Text(text = "Login")
            }

            Text(
                "Forgot password",
                color = Color(0xFF2980B9),
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(bottom = 145.dp, start = 135.dp)
            )

            Text(
                "Don’t have an account? Join us",
                color = Color(0xFF2980B9),
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(start = 89.dp)
            )
        }
    }
}

@Composable
fun SocialButton(label: String, iconRes: Int) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(shape = RoundedCornerShape(12.dp))
            .width(160.dp)
            .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(12.dp))
            .padding(vertical = 18.dp)
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier
                .padding(end = 14.dp)
                .width(18.dp)
                .height(18.dp)
        )
        Text(
            label,
            color = Color(0xFF677294),
            fontSize = 16.sp,
        )
    }
}






@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    Login()
}
