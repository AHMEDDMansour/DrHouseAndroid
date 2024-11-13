package com.example.appdrhouseandroid.ID

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appdrhouseandroid.data.repositories.UserRepository
import com.example.appdrhouseandroid.ui.signup.SignUpViewModel
import com.example.appdrhouseandroid.ui.theme.login.LoginViewModel


class ViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                SignUpViewModel(userRepository) as T
            }


            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    companion object {
        private var factory: ViewModelFactory? = null

        fun getInstance(userRepository: UserRepository): ViewModelFactory {
            if (factory == null) {
                synchronized(ViewModelFactory::class.java) {
                    if (factory == null) {
                        factory = ViewModelFactory(userRepository)
                    }
                }
            }
            return factory!!
        }
    }
}