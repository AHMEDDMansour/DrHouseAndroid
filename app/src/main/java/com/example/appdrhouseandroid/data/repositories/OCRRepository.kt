package com.example.appdrhouseandroid.data.repositories


import com.example.appdrhouseandroid.data.network.ApiService
import com.example.appdrhouseandroid.data.network.OcrResponse
import okhttp3.MultipartBody
import retrofit2.Response

class OcrRepository(private val apiService: ApiService) {
    suspend fun uploadImage(imagePart: MultipartBody.Part): Response<OcrResponse> {
        return apiService.uploadImage(imagePart)
    }
}