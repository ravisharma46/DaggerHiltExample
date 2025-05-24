package com.example.daggerhiltexample.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Sealed class to represent different UI states for login
sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _loginUiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginUiState: StateFlow<LoginUiState> = _loginUiState

    fun loginUser(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginUiState.value = LoginUiState.Error("Email and password cannot be empty.")
            return
        }

        _loginUiState.value = LoginUiState.Loading
        viewModelScope.launch {
            try {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _loginUiState.value = LoginUiState.Success
                        } else {
                            _loginUiState.value = LoginUiState.Error(
                                task.exception?.message ?: "Login failed. Please try again."
                            )
                        }
                    }
            } catch (e: Exception) {
                _loginUiState.value = LoginUiState.Error(
                    e.message ?: "An unexpected error occurred."
                )
            }
        }
    }

    // Optional: Function to reset state if needed, e.g., when navigating away
    fun resetState() {
        _loginUiState.value = LoginUiState.Idle
    }
}
