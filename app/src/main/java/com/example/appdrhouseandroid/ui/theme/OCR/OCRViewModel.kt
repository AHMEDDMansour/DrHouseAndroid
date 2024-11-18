package com.example.appdrhouseandroid.ui.theme.OCR


import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appdrhouseandroid.data.network.RetrofitClient
import com.example.appdrhouseandroid.data.network.ApiService
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class OCRViewModel : ViewModel() {

    // Mutable states for storing the OCR result and loading state
    private val _ocrResult = mutableStateOf("")
    val ocrResult: String get() = _ocrResult.value

    private val _isLoading = mutableStateOf(false)
    val isLoading: Boolean get() = _isLoading.value

    private val apiService: ApiService = RetrofitClient.getApiService()

    // Function to upload the image and get the OCR result
    fun uploadFile(
        uri: Uri?,
        context: Context,
        onSuccess: (String) -> Unit,
        onFailure: @Composable (String) -> Unit
    ) {
        if (uri == null) {
            onFailure("No file selected")
            return
        }

        _isLoading.value = true

        // Convert URI to File
        val file = File(uri.path!!) // Make sure URI to File conversion is correct
        val requestFile: RequestBody = RequestBody.create(
            okhttp3.MediaType.parse("image/*"),
            file
        )
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        viewModelScope.launch {
            try {
                val response = apiService.uploadFile(body)
                if (response.isSuccessful) {
                    _ocrResult.value = response.body() ?: "No text found"
                    onSuccess(_ocrResult.value)
                } else {
                    onFailure("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                onFailure("Exception: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
