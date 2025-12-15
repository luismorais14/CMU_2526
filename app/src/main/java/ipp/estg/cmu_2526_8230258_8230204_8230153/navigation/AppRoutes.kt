package ipp.estg.cmu_2526_8230258_8230204_8230153.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.repository.AuthRepository
import ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.login.LoginEvent
import ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.login.LoginScreen
import ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.login.LoginViewModel
import ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.login.NavigationEvent
import ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.login.forgetPassword.ForgetPasswordScreen
import ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.login.forgetPassword.ForgetPasswordViewModel
import ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.registration.RegistrationNavigationEvent
import ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.registration.RegistrationScreen
import ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.registration.RegistrationViewModel
import ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.splash.SplashScreen

@Composable
fun AppNavHost(
    authRepository: AuthRepository,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "splash"
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable("splash") {
            val isUserLoggedIn = authRepository.isUserLoggedIn()
            SplashScreen(onFinished = {
                val destination = if (isUserLoggedIn) "home" else "login"
                navController.navigate(destination) {
                    popUpTo("splash") {
                        inclusive = true
                    }
                }
            })
        }

        composable("login") {
            val viewmodel: LoginViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return LoginViewModel(authRepository) as T
                    }
                }
            )
            val state by viewmodel.uiState.collectAsState()

            LaunchedEffect(key1 = Unit) {
                viewmodel.navigationEvent.collect { event ->
                    when (event) {
                        is NavigationEvent.NavigateToHome -> {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }

                        is NavigationEvent.NavigateToForgetPassword -> {
                            navController.navigate("forgetPassword")
                        }

                        is NavigationEvent.NavigateToRegistration -> {
                            navController.navigate("registration")
                        }

                        is NavigationEvent.NavigateToProfile -> {
                            navController.navigate("profile")
                        }
                    }
                }
            }

            LoginScreen(
                state = state,
                onEvent = viewmodel::onEvent,
                onNavigateToRegistration = {
                    viewmodel.onEvent(LoginEvent.NavigateToRegistration)
                }
            )
        }
        composable("forgetPassword") {
            val viewmodel: ForgetPasswordViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return ForgetPasswordViewModel(authRepository) as T
                    }
                }
            )
            val state by viewmodel.uiState.collectAsState()

            ForgetPasswordScreen(
                onEvent = viewmodel::onEvent,
                state = state
            )
        }
        composable("registration") {
            val viewmodel: RegistrationViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return RegistrationViewModel(authRepository) as T
                    }
                }
            )

            val state by viewmodel.uiState.collectAsState()

            LaunchedEffect(key1 = Unit) {
                viewmodel.navigationEvent.collect { event ->
                    when (event) {
                        is RegistrationNavigationEvent.RegistrationComplete -> {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }
                }
            }

            RegistrationScreen(state = state, onEvent = viewmodel::onEvent)
        }
        composable("home") {
            MainAppScaffold(
                authRepository = authRepository,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}