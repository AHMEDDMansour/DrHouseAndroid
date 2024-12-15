package com.example.appdrhouseandroid.data.repositories

import android.content.SharedPreferences
import com.example.appdrhouseandroid.data.network.ApiService
import com.example.appdrhouseandroid.data.network.ProgressResponse
import com.example.appdrhouseandroid.data.network.UpdateProgressRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PreferenceKeys {
    const val USER_ID = "USER_ID"
    const val PREFS_NAME = "MyPrefs"
    const val DATE_FORMAT = "yyyy-MM-dd"
}

class ProgressRepository(
    private val apiService: ApiService,
    private val sharedPreferences: SharedPreferences
) {
    private fun getUserId(): String {
        return sharedPreferences.getString(PreferenceKeys.USER_ID, null)
            ?: throw IllegalStateException("User ID not found. Please login again.")
    }

    suspend fun addProgress(goalId: String, progressRequest: UpdateProgressRequest): Response<ProgressResponse> {
        return apiService.updateProgress(goalId, progressRequest)
    }

    suspend fun getTodayProgress(goalId: String): Response<ProgressResponse> {
        return apiService.getTodayProgress(goalId)
    }

    suspend fun getProgressHistory(
        goalId: String,
        startDate: String? = null,
        endDate: String? = null
    ): Response<List<ProgressResponse>> {
        return apiService.getGoalProgress(goalId, startDate, endDate)
    }
}
