package com.example.appdrhouseandroid.ui.theme.proggres

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appdrhouseandroid.data.network.ProgressResponse
import com.example.appdrhouseandroid.data.network.UpdateProgressRequest
import com.example.appdrhouseandroid.data.repositories.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class ProgressViewModel(private val repository: ProgressRepository) : ViewModel() {
    private val _progressState = MutableStateFlow<ProgressUIState>(ProgressUIState.Initial)
    val progressState: StateFlow<ProgressUIState> = _progressState

    private val _progressHistoryState = MutableStateFlow<ProgressHistoryState>(ProgressHistoryState.Initial)
    val progressHistoryState: StateFlow<ProgressHistoryState> = _progressHistoryState

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun setError(message: String) {
        _progressState.value = ProgressUIState.Error(message)
    }

    fun getTodayProgress(goalId: String?) {
        viewModelScope.launch {
            try {
                requireNotNull(goalId) { "Goal ID cannot be null" }
                require(goalId.isNotBlank()) { "Goal ID cannot be empty" }

                _progressState.value = ProgressUIState.Loading
                Log.d("ProgressViewModel", "Getting today's progress for goal: $goalId")

                val response = repository.getTodayProgress(goalId)
                if (response.isSuccessful && response.body() != null) {
                    _progressState.value = ProgressUIState.Success(response.body()!!)
                    Log.d("ProgressViewModel", "Successfully fetched progress: ${response.body()}")
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Failed to get today's progress"
                    Log.e("ProgressViewModel", "Error fetching progress: $errorMsg")
                    _progressState.value = ProgressUIState.Error(errorMsg)
                }
            } catch (e: Exception) {
                Log.e("ProgressViewModel", "Exception in getTodayProgress", e)
                _progressState.value = ProgressUIState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun getProgressHistory(goalId: String, days: Int = 7) {
        viewModelScope.launch {
            try {
                _progressHistoryState.value = ProgressHistoryState.Loading

                val calendar = Calendar.getInstance()
                val endDate = dateFormat.format(calendar.time)

                // Subtract days to get start date
                calendar.add(Calendar.DAY_OF_YEAR, -days)
                val startDate = dateFormat.format(calendar.time)

                Log.d("ProgressViewModel", "Fetching progress history for goal: $goalId from $startDate to $endDate")

                val response = repository.getProgressHistory(
                    goalId = goalId,
                    startDate = startDate,
                    endDate = endDate
                )

                if (response.isSuccessful && response.body() != null) {
                    _progressHistoryState.value = ProgressHistoryState.Success(response.body()!!)
                    Log.d("ProgressViewModel", "Successfully fetched history: ${response.body()?.size} entries")
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Failed to fetch progress history"
                    Log.e("ProgressViewModel", "Error fetching history: $errorMsg")
                    _progressHistoryState.value = ProgressHistoryState.Error(errorMsg)
                }
            } catch (e: Exception) {
                Log.e("ProgressViewModel", "Exception in getProgressHistory", e)
                _progressHistoryState.value = ProgressHistoryState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun addProgress(
        goalId: String?,
        steps: Int?,
        water: Int?,
        sleep: Int?,
        coffee: Int?,
        workout: Int?
    ) {
        viewModelScope.launch {
            try {
                requireNotNull(goalId) { "Goal ID cannot be null" }
                require(goalId.isNotBlank()) { "Goal ID cannot be empty" }

                _progressState.value = ProgressUIState.Loading

                val currentDate = dateFormat.format(Date())
                val progressRequest = UpdateProgressRequest(
                    date = currentDate,
                    steps = steps,
                    water = water,
                    sleepHours = sleep,
                    coffeeCups = coffee,
                    workout = workout
                )

                Log.d("ProgressViewModel", "Updating progress for goal $goalId with data: $progressRequest")

                val response = repository.addProgress(goalId, progressRequest)
                if (response.isSuccessful && response.body() != null) {
                    Log.d("ProgressViewModel", "Progress updated successfully: ${response.body()}")
                    _progressState.value = ProgressUIState.Success(response.body()!!)

                    // Refresh progress history after successful update
                    getProgressHistory(goalId)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Failed to update progress"
                    Log.e("ProgressViewModel", "Error updating progress: $errorMsg")
                    _progressState.value = ProgressUIState.Error(errorMsg)
                }
            } catch (e: Exception) {
                Log.e("ProgressViewModel", "Exception while updating progress", e)
                _progressState.value = ProgressUIState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun refreshProgress(goalId: String?) {
        getTodayProgress(goalId)
        if (goalId != null) {
            getProgressHistory(goalId)
        }
    }

    sealed class ProgressUIState {
        object Initial : ProgressUIState()
        object Loading : ProgressUIState()
        data class Success(val progress: ProgressResponse) : ProgressUIState()
        data class Error(val message: String) : ProgressUIState()
    }

    sealed class ProgressHistoryState {
        object Initial : ProgressHistoryState()
        object Loading : ProgressHistoryState()
        data class Success(val history: List<ProgressResponse>) : ProgressHistoryState()
        data class Error(val message: String) : ProgressHistoryState()
    }
}