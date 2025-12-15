package ipp.estg.cmu_2526_8230258_8230204_8230153.navigation

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.DinnerDining
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ipp.estg.cmu_2526_8230258_8230204_8230153.R
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.local.UserPreferences
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.repository.AuthRepository
import ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.diary.DiaryScreen
import ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.diary.DiaryViewModel
import ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.diary.MealType
import ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.leaderboard.LeaderboardScreen
import ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.leaderboard.LeaderboardViewModel
import ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.map.MapScreen
import ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.map.MapViewModel
import ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.plans.PlanDetailScreen
import ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.plans.PlansScreen
import ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.profile.ProfileEvent
import ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.profile.ProfileNavigationEvent
import ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.profile.ProfileScreen
import ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.profile.ProfileViewModel
import ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.search.SearchScreen
import ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.search.SearchViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold(
    authRepository: AuthRepository,
    onLogout: () -> Unit,
    onNavigateToRegister: () -> Unit = { onLogout() }
) {
    val appNavController = rememberNavController()
    val navBackStackEntry by appNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isUserLoggedIn = remember { authRepository.isUserLoggedIn() }

    val currentTitle = when {
        currentRoute == "diario" -> stringResource(R.string.title_diary)
        currentRoute == "plans" -> stringResource(R.string.title_plans)
        currentRoute?.startsWith("planDetail") == true -> stringResource(R.string.title_plan_details)
        currentRoute == "map" -> stringResource(R.string.title_map)
        currentRoute == "leaderboard" -> stringResource(R.string.title_leaderboard)
        else -> "Diário"
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val isSheetOpen = sheetState.isVisible
    val context = LocalContext.current
    val userPreferences = UserPreferences(context)

    val sharedProfileViewModel: ProfileViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(
                    repository = authRepository,
                    userPreferences = UserPreferences(context)
                ) as T
            }
        }
    )

    Scaffold(
        topBar = {
            if (currentRoute != "profile" && currentRoute?.startsWith("search/") != true) {
                MyTopAppBar(
                    title = currentTitle,
                    onLeaderboardClick = { appNavController.navigate("leaderboard") }
                )
            }
        },
        bottomBar = { MyBottomBar(navController = appNavController) },

        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        if (isSheetOpen) sheetState.hide() else sheetState.show()
                    }
                },
                modifier = Modifier.offset(y = 83.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        NavHost(
            navController = appNavController,
            startDestination = "diario",
            modifier = Modifier.padding(padding)
        ) {
            composable("diario") {
                val diaryViewModel: DiaryViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            val application = context.applicationContext as Application
                            return DiaryViewModel(application, authRepository, userPreferences) as T
                        }
                    }
                )
                DiaryScreen(viewModel = diaryViewModel)
            }

            composable("plans") {
                val state by sharedProfileViewModel.uiState.collectAsState()
                PlansScreen(navController = appNavController, activePlanId = state.activePlanId)
            }

            composable(
                route = "planDetail/{planId}",
                arguments = listOf(navArgument("planId") { type = NavType.StringType })
            ) { backStackEntry ->
                val planId = backStackEntry.arguments?.getString("planId")
                val state by sharedProfileViewModel.uiState.collectAsState()

                PlanDetailScreen(
                    navController = appNavController,
                    planId = planId,
                    activePlanId = state.activePlanId,
                    onStartPlan = { id ->
                        if (isUserLoggedIn) {
                            sharedProfileViewModel.onEvent(ProfileEvent.UpdatePlan(id))
                        } else {
                            onLogout()
                        }
                    }
                )
            }

            composable("map") {
                val viewmodel: MapViewModel = viewModel()
                val state by viewmodel.uiState.collectAsState()
                MapScreen(state = state, onEvent = viewmodel::onEvent)
            }

            composable("profile") {
                val state by sharedProfileViewModel.uiState.collectAsState()

                // Ouve eventos de logout vindos do ViewModel
                LaunchedEffect(key1 = Unit) {
                    sharedProfileViewModel.navigationEvent.collect { event ->
                        when (event) {
                            ProfileNavigationEvent.NavigateToLogin -> {
                                onLogout()
                            }
                        }
                    }
                }

                ProfileScreen(
                    state = state,
                    onEvent = sharedProfileViewModel::onEvent,
                    authRepository = authRepository,
                    onNavigateToLogin = onLogout,
                    onNavigateToRegister = onNavigateToRegister
                )
            }

            composable("leaderboard") {
                val leaderboardViewModel: LeaderboardViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return LeaderboardViewModel(authRepository) as T
                        }
                    }
                )
                LeaderboardScreen(viewModel = leaderboardViewModel)
            }

            composable(
                route = "search/{mealType}?date={date}",
                arguments = listOf(
                    navArgument("mealType") { type = NavType.StringType },
                    navArgument("date") { type = NavType.StringType; nullable = true }
                )
            ) { backStackEntry ->
                val mealType = backStackEntry.arguments?.getString("mealType") ?: "LUNCH"
                val dateString = backStackEntry.arguments?.getString("date")
                requireNotNull(dateString) { "Date must be passed from Diary" }

                val selectedDate = LocalDate.parse(dateString)
                backStackEntry.savedStateHandle["mealType"] = mealType
                backStackEntry.savedStateHandle["date"] = selectedDate.toString()

                val viewModel: SearchViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return SearchViewModel(
                                application = context.applicationContext as Application,
                                savedStateHandle = backStackEntry.savedStateHandle,
                                authRepository = authRepository
                            ).apply {
                                updateSelectedDate(selectedDate)
                            } as T
                        }
                    }
                )
                val state by viewModel.uiState.collectAsState()

                SearchScreen(
                    state = state,
                    onEvent = viewModel::onEvent,
                    onNavigateBack = { appNavController.navigateUp() }
                )
            }
        }
    }

    if (isSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { scope.launch { sheetState.hide() } },
            sheetState = sheetState
        ) {
            var selectedDate by remember { mutableStateOf(LocalDate.now()) }
            AddMealBottomSheetContent(
                currentDate = selectedDate,
                onDateChanged = { newDate -> selectedDate = newDate },
                onItemClick = { mealType ->
                    appNavController.navigate("search/${mealType.name}?date=$selectedDate")
                    scope.launch { sheetState.hide() }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    title: String,
    showSettings: Boolean = false,
    onSettingsClick: () -> Unit = {},
    onLeaderboardClick: () -> Unit = {}
) {
    TopAppBar(
        title = { Text(text = title) },
        actions = {
            IconButton(onClick = onLeaderboardClick) {
                Icon(Icons.Filled.Leaderboard, contentDescription = "Leaderboard")
            }
            if (showSettings) {
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Definições"
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBottomBar(navController: NavHostController) {
    BottomAppBar(
        actions = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = { navController.navigate("diario") },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_book_5_24),
                            contentDescription = "Diary"
                        )
                    }
                    Text(text = "Diário")
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = { navController.navigate("plans") },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_menu_book_2_24),
                            contentDescription = "Plans"
                        )
                    }
                    Text(text = "Planos")
                }

                Spacer(Modifier.weight(1f))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = { navController.navigate("map") },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_map_search_24),
                            contentDescription = "Map"
                        )
                    }
                    Text(text = "Mapa")
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = { navController.navigate("profile") },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_account_circle_24),
                            contentDescription = "Profile"
                        )
                    }
                    Text(text = "Perfil", textAlign = TextAlign.Center)
                }
            }
        }
    )
}

@Composable
private fun AddMealBottomSheetContent(
    onItemClick: (MealType) -> Unit,
    currentDate: LocalDate,
    onDateChanged: (LocalDate) -> Unit
){
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DateSelector(
            currentDate = currentDate,
            onDateChanged = onDateChanged,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        MealRow(
            text = "Pequeno-almoço",
            icon = Icons.Outlined.Coffee,
            recommendation = "Recomendado: 600 kcal",
            onClick = { onItemClick(MealType.BREAKFAST) }
        )
        MealRow(
            text = "Almoço",
            icon = Icons.Outlined.Restaurant,
            recommendation = "Recomendado: 700 kcal",
            onClick = { onItemClick(MealType.LUNCH) }
        )
        MealRow(
            text = "Jantar",
            icon = Icons.Outlined.DinnerDining,
            recommendation = "Recomendado: 500 kcal",
            onClick = { onItemClick(MealType.DINNER) }
        )
        MealRow(
            text = "Lanches",
            icon = Icons.Outlined.Fastfood,
            recommendation = "Recomendado: 200 kcal",
            onClick = { onItemClick(MealType.SNACK) }
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun DateSelector(
    currentDate: LocalDate,
    onDateChanged: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val minDate = today.minusDays(2)
    val formatter = DateTimeFormatter.ofPattern("EEE, d MMM yyyy")

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = {
                    val newDate = currentDate.minusDays(1)
                    if (!newDate.isBefore(minDate)) {
                        onDateChanged(newDate)
                    }
                },
                enabled = !currentDate.isEqual(minDate) && !currentDate.isBefore(minDate),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Dia anterior",
                    tint = if (!currentDate.isEqual(minDate) && !currentDate.isBefore(minDate))
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = when {
                        currentDate.isEqual(today) -> "HOJE"
                        currentDate.isEqual(today.minusDays(1)) -> "ONTEM"
                        currentDate.isEqual(today.minusDays(2)) -> "ANTEONTEM"
                        else -> currentDate.format(DateTimeFormatter.ofPattern("EEE")).uppercase()
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currentDate.format(formatter),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            IconButton(
                onClick = {
                    val newDate = currentDate.plusDays(1)
                    if (!newDate.isAfter(today)) {
                        onDateChanged(newDate)
                    }
                },
                enabled = !currentDate.isEqual(today) && !currentDate.isAfter(today),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "Próximo dia",
                    tint = if (!currentDate.isEqual(today) && !currentDate.isAfter(today))
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MealRow(text: String, icon: ImageVector, recommendation: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = text, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = text, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = recommendation,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(Icons.Default.Add, contentDescription = "Adicionar")
        }
    }
}