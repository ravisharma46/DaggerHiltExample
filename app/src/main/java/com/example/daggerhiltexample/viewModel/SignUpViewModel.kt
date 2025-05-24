package com.example.daggerhiltexample.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SignUpUiState {
    object Idle : SignUpUiState()
    object Loading : SignUpUiState()
    object Success : SignUpUiState()
    data class Error(val message: String) : SignUpUiState()
}

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _signUpUiState = MutableStateFlow<SignUpUiState>(SignUpUiState.Idle)
    val signUpUiState: StateFlow<SignUpUiState> = _signUpUiState

    fun signUpUser(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _signUpUiState.value = SignUpUiState.Error("Email and password cannot be empty.")
            return
        }
        // Basic password validation (e.g., length) can be added here
        if (password.length < 6) {
            _signUpUiState.value = SignUpUiState.Error("Password should be at least 6 characters.")
            return
        }


        _signUpUiState.value = SignUpUiState.Loading
        viewModelScope.launch {
            try {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Optionally sign in the user automatically or navigate to login
                            _signUpUiState.value = SignUpUiState.Success
                        } else {
                            _signUpUiState.value = SignUpUiState.Error(
                                task.exception?.message ?: "Sign up failed. Please try again."
                            )
                        }
                    }
            } catch (e: Exception) {
                _signUpUiState.value = SignUpUiState.Error(
                    e.message ?: "An unexpected error occurred."
                )
            }
        }
    }

    fun resetState() {
        _signUpUiState.value = SignUpUiState.Idle
    }
}
