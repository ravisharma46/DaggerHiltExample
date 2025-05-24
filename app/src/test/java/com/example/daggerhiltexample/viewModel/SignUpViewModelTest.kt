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
class SignUpViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule() // Reuse the rule from LoginViewModelTest or define here

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var signUpViewModel: SignUpViewModel

    @Before
    fun setUp() {
        firebaseAuth = mock()
        signUpViewModel = SignUpViewModel(firebaseAuth)
    }

    @Test
    fun `signUpUser with valid details success`() = runTest {
        val mockAuthResult: AuthResult = mock()
        val mockTask: Task<AuthResult> = mock {
            on { isSuccessful } doReturn true
            on { exception } doReturn null
            on { addOnCompleteListener(any()) } doAnswer { invocation ->
                val listener = invocation.getArgument<com.google.android.gms.tasks.OnCompleteListener<AuthResult>>(0)
                listener.onComplete(mockTask)
                mockTask
            }
        }
        whenever(firebaseAuth.createUserWithEmailAndPassword(any(), any())).thenReturn(mockTask)

        val states = mutableListOf<SignUpUiState>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            signUpViewModel.signUpUiState.toList(states)
        }

        signUpViewModel.signUpUser("newuser@example.com", "password123")

        assertTrue(states.size >= 3)
        assertEquals(SignUpUiState.Idle, states[states.size - 3])
        assertEquals(SignUpUiState.Loading, states[states.size - 2])
        assertEquals(SignUpUiState.Success, states[states.size - 1])

        verify(firebaseAuth).createUserWithEmailAndPassword("newuser@example.com", "password123")
        job.cancel()
    }

    @Test
    fun `signUpUser with existing email failure`() = runTest {
        val mockException = mock<Exception> {
            on { message } doReturn "Email already exists"
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
        whenever(firebaseAuth.createUserWithEmailAndPassword(any(), any())).thenReturn(mockTask)

        val states = mutableListOf<SignUpUiState>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            signUpViewModel.signUpUiState.toList(states)
        }

        signUpViewModel.signUpUser("existing@example.com", "password123")

        assertTrue(states.size >= 3)
        assertEquals(SignUpUiState.Idle, states[states.size - 3])
        assertEquals(SignUpUiState.Loading, states[states.size - 2])
        assertTrue(states[states.size - 1] is SignUpUiState.Error)
        assertEquals("Email already exists", (states[states.size - 1] as SignUpUiState.Error).message)

        job.cancel()
    }

    @Test
    fun `signUpUser with short password emits error`() = runTest {
         val states = mutableListOf<SignUpUiState>()
         val job = launch(UnconfinedTestDispatcher(testScheduler)) {
             signUpViewModel.signUpUiState.toList(states)
         }
        signUpViewModel.signUpUser("test@example.com", "123") // Password too short

        assertTrue(states.last() is SignUpUiState.Error)
        assertEquals("Password should be at least 6 characters.", (states.last() as SignUpUiState.Error).message)
        job.cancel()
    }

    @Test
    fun `signUpUser with empty email emits error`() = runTest {
         val states = mutableListOf<SignUpUiState>()
         val job = launch(UnconfinedTestDispatcher(testScheduler)) {
             signUpViewModel.signUpUiState.toList(states)
         }
        signUpViewModel.signUpUser("", "password123")

        assertTrue(states.last() is SignUpUiState.Error)
        assertEquals("Email and password cannot be empty.", (states.last() as SignUpUiState.Error).message)
        job.cancel()
    }
}
