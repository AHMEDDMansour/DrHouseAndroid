package com.example.appdrhouseandroid.ui.theme.OCR


import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log // Import for logging
import androidx.compose.ui.graphics.Outline.Rectangle
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appdrhouseandroid.data.network.OcrResponse
import com.example.appdrhouseandroid.data.repositories.OcrRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

import com.google.mlkit.vision.text.Text

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class OCRViewModel(private val apiService: OcrRepository) : ViewModel() {
    // To hold OCR result and handle UI state updates
    private val _ocrResult = MutableStateFlow<Response<OcrResponse>?>(null)
    val ocrResult: StateFlow<Response<OcrResponse>?> = _ocrResult

    // Function to upload image and get OCR result
    fun uploadImage(imagePart: MultipartBody.Part) {
        viewModelScope.launch {
            try {
                val response = apiService.uploadImage(imagePart)
                _ocrResult.value = response
                if (!response.isSuccessful) {
                    Log.e("OCRViewModel", "Image upload failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("OCRViewModel", "Exception during image upload", e)
                _ocrResult.value = null
            }
        }
    }

    // Function to create an image part for the API request
    fun createImagePart(context: Context, uri: Uri): MultipartBody.Part? {
        return try {
            val contentResolver: ContentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri)

            // Create a unique temporary file name using timestamp to avoid overwriting
            val timestamp = System.currentTimeMillis()
            val tempFile = File(context.cacheDir, "temp_image_$timestamp.jpg")

            inputStream?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }

            // Create the MultipartBody.Part for uploading
            val requestBody = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("file", tempFile.name, requestBody)

            // Optional: Clean up the file after the upload is done (make sure this doesn't cause issues)
            tempFile.deleteOnExit()

            part
        } catch (e: Exception) {
            Log.e("OCRViewModel", "Error creating image part", e)
            null
        }
    }
}