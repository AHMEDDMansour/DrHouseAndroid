package com.example.appdrhouseandroid.ui.theme.AiPredection

import android.util.Log
import androidx.lifecycle.*
import com.example.appdrhouseandroid.data.network.PredictionResponse
import com.example.appdrhouseandroid.data.repositories.PredictionRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class PredictionViewModel(private val repository: PredictionRepository) : ViewModel() {
    private val _predictionResponse = MutableLiveData<PredictionResponse>()
    val predictionResponse: LiveData<PredictionResponse> get() = _predictionResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun fetchPrediction(symptoms: List<String>) {
        _isLoading.value = true // Start loading
        viewModelScope.launch {
            try {
                val response = repository.getPrediction(symptoms)
                if (response.isSuccessful) {
                    _predictionResponse.postValue(response.body())
                } else {
                    _error.postValue("Error: ${response.errorBody()?.string()}")
                }
            } catch (e: HttpException) {
                _error.postValue("HTTP Error: ${e.message()}")
            } catch (e: IOException) {
                _error.postValue("Network Error: ${e.message}")
            } catch (e: Exception) {
                _error.postValue("Unknown Error: ${e.message}")
            } finally {
                _isLoading.value = false // Stop loading
            }
        }
    }
}

