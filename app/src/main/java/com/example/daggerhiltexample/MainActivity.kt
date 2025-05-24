package com.example.daggerhiltexample

import android.os.Bundle
import androidx.activity.compose.setContent
// import androidx.activity.viewModels // Not needed if MainViewModel is obtained via hiltViewModel()
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.daggerhiltexample.presentation.component.MovieCard
import com.example.daggerhiltexample.presentation.ui.auth.LoginScreen
import com.example.daggerhiltexample.presentation.ui.auth.SignUpScreen
import com.example.daggerhiltexample.viewModel.LoginUiState
import com.example.daggerhiltexample.viewModel.LoginViewModel
import com.example.daggerhiltexample.viewModel.MainViewModel
import com.example.daggerhiltexample.viewModel.SignUpUiState
import com.example.daggerhiltexample.viewModel.SignUpViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

// Potentially: import androidx.lifecycle.compose.collectAsStateWithLifecycle // Add if switching to this

object AppRoutes {
    const val LOGIN = "login"
    const val SIGN_UP = "signup"
    const val MAIN_CONTENT = "main_content"
}

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    // private val mainViewModel: MainViewModel by viewModels() // Replaced by hiltViewModel() in MainContentScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth.
        val firebaseAuth = FirebaseAuth.getInstance()
        val startDestination = if (firebaseAuth.currentUser != null) {
            AppRoutes.MAIN_CONTENT
        } else {
            AppRoutes.LOGIN
        }

        setContent {
            AppNavigation(startDestination = startDestination) // AppNavigation will be added next
        }
    }

    @Composable
    fun AppNavigation(startDestination: String) {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = startDestination) {
            composable(AppRoutes.LOGIN) {
                val loginViewModel: LoginViewModel = hiltViewModel()
                val loginState by loginViewModel.loginUiState.collectAsState() // or collectAsStateWithLifecycle

                LoginScreen(
                    onLoginClick = { email, password ->
                        loginViewModel.loginUser(email, password)
                    },
                    onSignUpClick = {
                        navController.navigate(AppRoutes.SIGN_UP)
                    },
                    isLoading = loginState is LoginUiState.Loading,
                    errorMessage = if (loginState is LoginUiState.Error) (loginState as LoginUiState.Error).message else null
                )

                LaunchedEffect(loginState) {
                    if (loginState is LoginUiState.Success) {
                        navController.navigate(AppRoutes.MAIN_CONTENT) {
                            popUpTo(AppRoutes.LOGIN) { inclusive = true } // Clear login from back stack
                        }
                        loginViewModel.resetState() // Reset state after navigation
                    }
                }
            }

            composable(AppRoutes.SIGN_UP) {
                val signUpViewModel: SignUpViewModel = hiltViewModel()
                val signUpState by signUpViewModel.signUpUiState.collectAsState() // or collectAsStateWithLifecycle

                SignUpScreen(
                    onSignUpClick = { email, password ->
                        signUpViewModel.signUpUser(email, password)
                    },
                    onLoginClick = {
                        navController.popBackStack() // Go back to Login screen
                    },
                    isLoading = signUpState is SignUpUiState.Loading,
                    errorMessage = if (signUpState is SignUpUiState.Error) (signUpState as SignUpUiState.Error).message else null
                )

                LaunchedEffect(signUpState) {
                    if (signUpState is SignUpUiState.Success) {
                        // Navigate to login on successful signup, so user can log in
                        // Or directly to main content if auto-login is implemented
                        navController.navigate(AppRoutes.LOGIN) {
                            popUpTo(AppRoutes.SIGN_UP) { inclusive = true }
                        }
                        signUpViewModel.resetState() // Reset state
                    }
                }
            }

            // MAIN_CONTENT route will be added in the next step
            composable(AppRoutes.MAIN_CONTENT) {
                // Assuming MainViewModel is now obtained here for this specific screen
                val mainViewModel: MainViewModel = hiltViewModel()
                MainContentScreen(mainViewModel = mainViewModel) // Pass it to your existing main content
            }
        }
    }

    // This is your original composeView(), refactored to be the MainContentScreen
    // It now takes MainViewModel as a parameter, which will be provided by Hilt.
    @Composable
    fun MainContentScreen(mainViewModel: MainViewModel) {
        val movieList = mainViewModel.movieList.value // Assuming movieList is still a State<List<MoviePojo?>>

        if (movieList == null || movieList.isEmpty()) {
            // Handle loading or empty state for movie list if necessary
            // For now, just showing an empty LazyColumn or a message
            // Text("Loading movies or no movies found.")
        } else {
            LazyColumn {
                itemsIndexed(
                    items = movieList
                ) { index, movie ->
                    movie?.let { // Ensure movie is not null
                        MovieCard(
                            moviePojo = it,
                            onClick = { /* Define action on movie click */ },
                            context = this@MainActivity // Context might be an issue here, consider if needed or how to provide
                        )
                    }
                }
            }
        }
    }
}