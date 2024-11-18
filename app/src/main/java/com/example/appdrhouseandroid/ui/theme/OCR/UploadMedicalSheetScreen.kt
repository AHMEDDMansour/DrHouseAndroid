package com.example.appdrhouseandroid.ui.theme.OCR


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.appdrhouseandroid.R

import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.platform.LocalContext

@Composable
fun UploadMedicalSheetScreen(
    ocrViewModel: OCRViewModel = viewModel()
) {
    var imageUri: Uri? = null
    val isLoading = ocrViewModel.isLoading
    val ocrResult = ocrViewModel.ocrResult

    // Image picker logic using activity result API
    val imagePickerLauncher: ActivityResultLauncher<Intent> =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    imageUri = uri
                    ocrViewModel.uploadFile(uri, LocalContext.current,
                        onSuccess = {
                            // Handle successful OCR result
                        },
                        onFailure = { errorMessage ->
                            // Handle failure
                            Toast.makeText(LocalContext.current, errorMessage, Toast.LENGTH_SHORT).show()
                        })
                }
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Upload Medical Sheet and Scan for Text")

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                // Launch the image picker when the button is clicked
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                imagePickerLauncher.launch(intent)
            }
        ) {
            Text("Select Medical Sheet Image")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Show the selected image
        imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Uploaded Image",
                modifier = Modifier.size(200.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Text("OCR Result: $ocrResult")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    UploadMedicalSheetScreen()
}
