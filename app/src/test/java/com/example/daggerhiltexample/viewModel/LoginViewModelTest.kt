package com.example.daggerhiltexample.viewModel

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    // Rule for Main dispatcher substitution for coroutines
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setUp() {
        firebaseAuth = mock()
        loginViewModel = LoginViewModel(firebaseAuth)
    }

    @Test
    fun `loginUser with valid credentials success`() = runTest {
        val mockAuthResult: AuthResult = mock()
        val mockTask: Task<AuthResult> = mock {
            on { isSuccessful } doReturn true
            on { exception } doReturn null
            // For addOnCompleteListener
            on { addOnCompleteListener(any()) } doAnswer { invocation ->
                val listener = invocation.getArgument<com.google.android.gms.tasks.OnCompleteListener<AuthResult>>(0)
                listener.onComplete(mockTask) // Simulate immediate completion
                mockTask // Return the task itself
            }
        }
        whenever(firebaseAuth.signInWithEmailAndPassword(any(), any())).thenReturn(mockTask)

        val states = mutableListOf<LoginUiState>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { // Use UnconfinedTestDispatcher for eager collection
            loginViewModel.loginUiState.toList(states)
        }

        loginViewModel.loginUser("test@example.com", "password123")

        // Expected states: Idle -> Loading -> Success
        assertTrue(states.size >= 3) // Might have more if previous tests left state
        assertEquals(LoginUiState.Idle, states[states.size - 3]) // More robust check: collect specific number or filter
        assertEquals(LoginUiState.Loading, states[states.size - 2])
        assertEquals(LoginUiState.Success, states[states.size - 1])

        verify(firebaseAuth).signInWithEmailAndPassword("test@example.com", "password123")
        job.cancel()
    }

    @Test
    fun `loginUser with invalid credentials failure`() = runTest {
        val mockException = mock<Exception> {
            on { message } doReturn "Invalid credentials"
        }
        val mockTask: Task<AuthResult> = mock {
            on { isSuccessful } doReturn false
            on { exception } doReturn mockException
            on { addOnCompleteListener(any()) } doAnswer { invocation ->
                val listener = invocation.getArgument<com.google.android.gms.tasks.OnCompleteListener<AuthResult>>(0)
                listener.onComplete(mockTask)
                mockTask
            }
        }
        whenever(firebaseAuth.signInWithEmailAndPassword(any(), any())).thenReturn(mockTask)

        val states = mutableListOf<LoginUiState>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            loginViewModel.loginUiState.toList(states)
        }

        loginViewModel.loginUser("test@example.com", "wrongpassword")

        assertTrue(states.size >= 3)
        assertEquals(LoginUiState.Idle, states[states.size - 3])
        assertEquals(LoginUiState.Loading, states[states.size - 2])
        assertTrue(states[states.size - 1] is LoginUiState.Error)
        assertEquals("Invalid credentials", (states[states.size - 1] as LoginUiState.Error).message)

        job.cancel()
    }

    @Test
    fun `loginUser with empty email emits error`() = runTest {
        val states = mutableListOf<LoginUiState>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            loginViewModel.loginUiState.toList(states)
        }

        loginViewModel.loginUser("", "password")

        // Expected: Idle -> Error (or just Error if Idle is the absolute initial)
        // The viewmodel sets state directly without Loading for this case
        assertTrue(states.last() is LoginUiState.Error)
        assertEquals("Email and password cannot be empty.", (states.last() as LoginUiState.Error).message)
        job.cancel()
    }
}

// Helper Rule for tests using Coroutines
@ExperimentalCoroutinesApi
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : org.junit.rules.TestWatcher() {
    override fun starting(description: org.junit.runner.Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: org.junit.runner.Description) {
        Dispatchers.resetMain()
    }
}
