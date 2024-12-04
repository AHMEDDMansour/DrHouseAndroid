package com.example.appdrhouseandroid.ui.theme.Set_Goals

// GoalsViewModel.kt


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appdrhouseandroid.data.repositories.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

data class GoalsUiState(
    val userGoals: UserGoals = UserGoals(
        stepsGoal = 0f, stepsProgress = 0f,
        waterGoal = 0f, waterProgress = 0f,
        sleepGoal = 0f, sleepProgress = 0f,
        coffeeGoal = 0f, coffeeProgress = 0f,
        workoutGoal = 0f, workoutProgress = 0f
    ),
    val isLoading: Boolean = false,
    val error: String? = null
)

class GoalsViewModel(
    private val repository: ProgressRepository,
    private val userId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalsUiState())
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()

    /*init {
        loadDailyTracking()
    }

    private fun loadDailyTracking() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                repository.getDailyTracking(userId, Date()).fold(
                    onSuccess = { response ->
                        _uiState.value = _uiState.value.copy(
                            userGoals = UserGoals(
                                stepsGoal = response.tracking.goalId.steps.toFloat(),
                                    stepsProgress = response.tracking.actualSteps.toFloat(),
                                    waterGoal = response.tracking.goalId.water.toFloat(),
                                    waterProgress = response.tracking.actualWater.toFloat(),
                                    sleepGoal = response.tracking.goalId.sleepHours.toFloat(),
                                    sleepProgress = response.tracking.actualSleepHours.toFloat(),
                                    coffeeGoal = response.tracking.goalId.coffeeCups.toFloat(),
                                    coffeeProgress = response.tracking.actualCoffeeCups.toFloat(),
                                    workoutGoal = response.tracking.goalId.workout.toFloat(),
                                    workoutProgress = response.tracking.actualWorkout.toFloat()
                            ),
                            isLoading = false,
                            error = null
                        )
                    },
                    onFailure = { e ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = e.message
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun updateProgress(
        type: String,
        value: Float
    ) {
        viewModelScope.launch {
            val currentGoals = _uiState.value.userGoals
            val updatedGoals = when (type) {
                "steps" -> currentGoals.copy(stepsProgress = value)
                "water" -> currentGoals.copy(waterProgress = value)
                "sleep" -> currentGoals.copy(sleepProgress = value)
                "coffee" -> currentGoals.copy(coffeeProgress = value)
                "workout" -> currentGoals.copy(workoutProgress = value)
                else -> currentGoals
            }

            repository.createTracking(
                userId = userId,
                goalId = "", // Get from current tracking
                steps = updatedGoals.stepsProgress.toInt(),
                water = updatedGoals.waterProgress.toInt(),
                sleepHours = updatedGoals.sleepProgress.toInt(),
                coffeeCups = updatedGoals.coffeeProgress.toInt(),
                workout = updatedGoals.workoutProgress.toInt()
            ).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        userGoals = updatedGoals,
                        error = null
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        error = e.message
                    )
                }
            )
        }
    }

    fun refreshTracking() {
        loadDailyTracking()
    }*/
}