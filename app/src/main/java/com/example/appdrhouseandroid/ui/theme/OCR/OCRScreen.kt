package com.example.appdrhouseandroid.ui.theme.OCR

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage

@Composable
fun OCRScreen(navController: NavHostController, viewModel: OCRViewModel) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var extractedText by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedImageUri = uri
    }

    // Collect OCR result from ViewModel
    val ocrResult = viewModel.ocrResult.collectAsState()
    val context = LocalContext.current

    // Handle the result of OCR processing
    LaunchedEffect(ocrResult.value) {
        when {
            ocrResult.value?.isSuccessful == true -> {
                extractedText = ocrResult.value?.body()?.text
                isLoading = false
            }
            ocrResult.value?.isSuccessful == false -> {
                errorMessage = "Error: ${ocrResult.value?.message()}"
                isLoading = false
            }
            else -> {
                isLoading = true // Keep loading state when processing
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { launcher.launch("image/*") }) {
            Text("Select Image")
        }

        Spacer(modifier = Modifier.height(16.dp))

        selectedImageUri?.let { uri ->
            AsyncImage(
                model = uri,
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .padding(8.dp)
            )

            Button(onClick = {
                val imagePart = viewModel.createImagePart(context, uri)

                if (imagePart != null) {
                    viewModel.uploadImage(imagePart)
                    isLoading = true // Show loading state when starting OCR process
                } else {
                    Log.e("OCRScreen", "Failed to create image part from URI.")
                    errorMessage = "Failed to create image part from URI."
                }
            }) {
                Text("Upload and Extract Text")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Show loading spinner when the OCR process is ongoing
        if (isLoading) {
            CircularProgressIndicator()
        }

        // Show error message if OCR failed
        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Show extracted text if available
        extractedText?.let {
            TextField(
                value = it,
                onValueChange = {},
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)),
                modifier = Modifier.fillMaxWidth(),
                enabled = false // Make the TextField read-only
            )
        }
    }
}
