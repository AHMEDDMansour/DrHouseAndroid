package com.example.appdrhouseandroid.data.repository

import com.example.appdrhouseandroid.data.network.AddGoalDto
import com.example.appdrhouseandroid.data.network.ApiService
import com.example.appdrhouseandroid.data.network.GoalResponse
import com.example.appdrhouseandroid.data.network.UpdateGoalDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class GoalRepository(private val apiService: ApiService) {

    // Fetch all goals for a user
    suspend fun getGoals(userId: String): Response<List<GoalResponse>> {
        return withContext(Dispatchers.IO) {

            apiService.getUserGoals(userId)
        }
    }

    // Add a new goal
    suspend fun createGoal(userId: String, addGoalRequest: AddGoalDto): Response<GoalResponse> {
        return withContext(Dispatchers.IO) {
            apiService.createGoal(userId, addGoalRequest)
        }
    }

    // Update an existing goal
    suspend fun updateGoal(
        goalId: String,
        updateGoalRequest: UpdateGoalDto
    ): Response<GoalResponse> {
        return withContext(Dispatchers.IO) {
            apiService.updateGoal(goalId, updateGoalRequest)
        }
    }
}
