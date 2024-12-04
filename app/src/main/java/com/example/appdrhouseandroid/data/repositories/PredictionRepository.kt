package com.example.appdrhouseandroid.data.repositories

import com.example.appdrhouseandroid.data.network.ApiService
import com.example.appdrhouseandroid.data.network.PredictionResponse
import com.example.appdrhouseandroid.data.network.SymptomsRequest
import retrofit2.Response

class PredictionRepository(private val apiService: ApiService) {
    suspend fun getPrediction(symptoms: List<String>): Response<PredictionResponse> {
        val request = SymptomsRequest(symptoms)
        return apiService.getPrediction(request)
    }
}
