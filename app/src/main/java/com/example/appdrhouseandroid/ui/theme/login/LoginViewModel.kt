package com.example.appdrhouseandroid.ui.theme.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appdrhouseandroid.data.network.LoginResponse
import com.example.appdrhouseandroid.data.repositories.UserRepository
import kotlinx.coroutines.launch
import retrofit2.Response


// Define the UI state
data class LoginUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val token: String? = null,
    val errorMessage: String? = null,
    val hasNavigated: Boolean = false
)

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private var _loginUiState: MutableLiveData<LoginUiState> = MutableLiveData(LoginUiState())
    val loginUiState: LiveData<LoginUiState> get() = _loginUiState // Expose as LiveData

    // Function to handle user login
    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _loginUiState.value = LoginUiState(isLoading = true)  // Set loading state

            try {
                // Make the login request
                val response: Response<LoginResponse> = userRepository.login(email, password)

                if (response.isSuccessful) {
                    // Extract tokens and user ID from the response
                    val loginResponse = response.body()
                    print(loginResponse)
                    if (loginResponse != null) {
                        val accessToken = loginResponse.accessToken
                        val refreshToken = loginResponse.refreshToken
                        val userId = loginResponse.userId

                        // Update state with success
                        _loginUiState.value = LoginUiState(isLoggedIn = true, token = accessToken)
                        // Optionally, you can store refreshToken and userId if needed
                    } else {
                        // Handle case where response body is null
                        _loginUiState.value = LoginUiState(errorMessage = "Login failed: No response body")
                    }
                } else {
                    // Update state with error message
                    _loginUiState.value = LoginUiState(errorMessage = "Login failed: ${response.message()}")
                }
            } catch (e: Exception) {
                // Handle exceptions during the network call
                _loginUiState.value = LoginUiState(errorMessage = e.message)
            }
        }
    }
}