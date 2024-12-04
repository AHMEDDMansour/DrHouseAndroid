package com.example.appdrhouseandroid.ui.theme.Set_Goals

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appdrhouseandroid.data.network.AddGoalDto
import com.example.appdrhouseandroid.data.network.UpdateGoalRequest
import com.example.appdrhouseandroid.data.network.GoalResponse
import com.example.appdrhouseandroid.data.network.UpdateGoalDto
import com.example.appdrhouseandroid.data.repository.GoalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class GoalSettingScreenViewModel(private val repository: GoalRepository) : ViewModel() {

    private val _goals = MutableStateFlow<List<GoalResponse>?>(null)
    val goals: StateFlow<List<GoalResponse>?> = _goals

    private val _addGoalResponse = MutableStateFlow<Response<GoalResponse>?>(null)
    val addGoalResponse: StateFlow<Response<GoalResponse>?> get() = _addGoalResponse

    private val _updateGoalResponse = MutableStateFlow<Response<GoalResponse>?>(null)
    val updateGoalResponse: StateFlow<Response<GoalResponse>?> get() = _updateGoalResponse

    fun fetchGoals(userId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getGoals(userId)
                if (response.isSuccessful && response.body() != null) {
                    // Log the raw response for debugging
                    Log.d("GoalViewModel", "Raw response: ${response.body()}")

                    val goals = response.body()!!
                    // Verify that IDs are present
                    goals.forEach { goal ->
                        if (goal.id == null) {
                            Log.e("GoalViewModel", "Goal received without ID: $goal")
                        }
                    }
                    _goals.value = goals
                } else {
                    Log.e("GoalViewModel", "Failed to fetch goals: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("GoalViewModel", "Error fetching goals", e)
            }
        }
    }

    fun addGoal(userId: String, addGoalRequest: AddGoalDto) {
        viewModelScope.launch {
            val response = repository.createGoal(userId, addGoalRequest)
            _addGoalResponse.value = response
        }
    }

    fun updateGoal(goalId: String, updateGoalRequest: UpdateGoalDto) {  // Changed UpdateGoalRequest to UpdateGoalDto
        viewModelScope.launch {
            val response = repository.updateGoal(goalId, updateGoalRequest)
            _updateGoalResponse.value = response
        }
    }

}
