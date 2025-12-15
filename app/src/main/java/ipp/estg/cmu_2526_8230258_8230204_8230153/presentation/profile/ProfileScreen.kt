package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.profile

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ipp.estg.cmu_2526_8230258_8230204_8230153.R
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.repository.AuthRepository
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.services.ForegroundService
import ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.profile.editProfile.EditProfileScreen
import ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.profile.editProfile.EditProfileViewModel
import ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.profile.personalInfo.PersonalInfoScreen
import ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.profile.personalInfo.PersonalInfoViewModel
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.DrawStyle.Stroke
import ir.ehsannarmani.compose_charts.models.Line
import kotlinx.coroutines.launch

/**
 * Ecrã de Perfil principal.
 *
 * Serve como um contentor de navegação aninhado para sub-ecrãs do perfil (Info Pessoal, Editar).
 * Verifica se o utilizador está autenticado, exibindo conteúdo restrito ou o perfil completo
 * com gaveta de navegação ([ModalNavigationDrawer]).
 *
 * @param state O estado atual da UI do perfil ([ProfileUiState]).
 * @param onEvent Callback para enviar eventos de interação para o [ProfileViewModel].
 * @param authRepository Repositório de autenticação para verificar o estado de login.
 * @param onNavigateToLogin Callback para navegar para o ecrã de Login (usado para utilizadores não autenticados).
 * @param onNavigateToRegister Callback para navegar para o ecrã de Registo (usado para utilizadores não autenticados).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    state: ProfileUiState,
    onEvent: (ProfileEvent) -> Unit,
    authRepository: AuthRepository,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val isUserLoggedIn = remember { authRepository.isUserLoggedIn() }

    if (!isUserLoggedIn) {
        GuestProfileContent(
            onNavigateToLogin = onNavigateToLogin,
            onNavigateToRegister = onNavigateToRegister
        )
        return
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val profileNavController = rememberNavController()
    val navBackStackEntry by profileNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val title = when (currentRoute) {
        "profile_main" -> "Perfil"
        "personal_info" -> stringResource(R.string.profile_personal_info)
        "edit_profile" -> stringResource(R.string.profile_edit)
        else -> "Perfil"
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Definições", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                        HorizontalDivider()
                        NavigationDrawerItem(
                            label = { Text("Informações Pessoais") },
                            selected = false,
                            icon = { Icon(Icons.Default.Person, null) },
                            onClick = {
                                profileNavController.navigate("personal_info")
                                scope.launch { drawerState.close() }
                            }
                        )
                        NavigationDrawerItem(
                            label = { Text("Editar Perfil") },
                            selected = false,
                            icon = { Icon(Icons.Default.Edit, null) },
                            onClick = {
                                profileNavController.navigate("edit_profile")
                                scope.launch { drawerState.close() }
                            }
                        )
                    }
                    NavigationDrawerItem(
                        label = { Text("Terminar Sessão") },
                        selected = false,
                        icon = { Icon(Icons.AutoMirrored.Filled.Logout, null) },
                        onClick = {
                            onEvent(ProfileEvent.OnLogout)
                            scope.launch { drawerState.close() }
                        },
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        if (currentRoute != "profile_main") {
                            IconButton(onClick = { profileNavController.navigateUp() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                            }
                        }
                    },
                    actions = {
                        if (currentRoute == "profile_main") {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Settings, "Abrir Menu")
                            }
                        }
                    }
                )
            }
        ) { paddingValues ->
            NavHost(
                navController = profileNavController,
                startDestination = "profile_main",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("profile_main") {
                    ProfileScreenContent(
                        modifier = Modifier.fillMaxSize(),
                        state = state
                    )
                }
                composable("personal_info") {
                    val vm: PersonalInfoViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T = PersonalInfoViewModel(authRepository) as T
                    })
                    val infoState by vm.uiState.collectAsState()
                    PersonalInfoScreen(state = infoState, onEvent = vm::onEvent, modifier = Modifier.fillMaxSize())
                }
                composable("edit_profile") {
                    val vm: EditProfileViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T = EditProfileViewModel(authRepository) as T
                    })
                    val editState by vm.uiState.collectAsState()
                    LaunchedEffect(editState.saveSuccess) { if (editState.saveSuccess) profileNavController.popBackStack() }
                    EditProfileScreen(state = editState, onEvent = vm::onEvent, modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

/**
 * Conteúdo exibido para utilizadores não autenticados (Convidados).
 *
 * Fornece contexto sobre as funcionalidades restritas e botões para Login ou Registo.
 *
 * @param onNavigateToLogin Callback para Login.
 * @param onNavigateToRegister Callback para Registo.
 */
@Composable
fun GuestProfileContent(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Bloqueado",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Funcionalidade Restrita",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Crie uma conta ou inicie sessão para monitorizar o seu peso, aceder a estatísticas detalhadas e ganhar pontos!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onNavigateToLogin,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Icon(Icons.Default.AccountCircle, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Iniciar Sessão")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onNavigateToRegister,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Criar Conta Nova")
        }
    }
}

/**
 * O conteúdo principal do ecrã de perfil para utilizadores autenticados.
 *
 * Contém o cartão de gamificação, os gráficos de progresso e o controlo de serviço em segundo plano.
 *
 * @param modifier Modificador.
 * @param state O estado da UI com os dados do utilizador.
 */
@Composable
fun ProfileScreenContent(
    modifier: Modifier = Modifier,
    state: ProfileUiState
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        GamificationCard(points = state.points)

        WeightTrackerCard(
            data = state.weightHistory,
            currentWeight = state.userData?.weight ?: 0.0,
            initialWeight = state.userData?.initialWeight ?: 0.0
        )

        EnergyBalanceCard(data = state.weeklyCalories)

        NotificationControlButton()

        Spacer(modifier = Modifier.height(32.dp))
    }
}

/**
 * Cartão que exibe a pontuação de gamificação do utilizador.
 *
 * @param points A pontuação total do utilizador.
 */
@Composable
private fun GamificationCard(points: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "A tua Pontuação",
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = "$points pts",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Troféu",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Cartão que exibe o gráfico de histórico de peso e o resumo do progresso.
 *
 * @param data Lista de valores de peso para o gráfico de linha.
 * @param currentWeight O peso mais recente.
 * @param initialWeight O peso registado no início do percurso do utilizador.
 */
@Composable
private fun WeightTrackerCard(data: List<Double>, currentWeight: Double, initialWeight: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.MonitorWeight, "Rastreador de Peso", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Rastreador de Peso", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (data.isNotEmpty()) {
                    LineChart(
                        data = listOf(
                            Line(
                                label = "Peso",
                                values = data,
                                color = SolidColor(MaterialTheme.colorScheme.primary),
                                drawStyle = Stroke(width = 3.dp)
                            )
                        ),
                        animationDelay = 0,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text("Sem registos de peso", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val displayInitial = if (initialWeight > 0) initialWeight else data.firstOrNull() ?: currentWeight

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), shape = MaterialTheme.shapes.medium)
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Inicial", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("$displayInitial kg", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                VerticalDivider(modifier = Modifier.height(40.dp).width(1.dp), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Atual", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("$currentWeight kg", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Cartão que exibe o gráfico de barras do Saldo de Energia semanal (calorias por dia).
 *
 * @param data Lista de valores calóricos diários para os últimos 7 dias.
 */
@Composable
private fun EnergyBalanceCard(data: List<Float>) {
    val weekDays = listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom")
    val maxCalories = (data.maxOrNull() ?: 1f).coerceAtLeast(1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.LocalFireDepartment,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Saldo de Energia",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (data.isEmpty()) {
                Text(
                    "Sem dados de diário",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                var selectedIndex by remember { mutableStateOf<Int?>(null) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    weekDays.forEachIndexed { index, day ->
                        val value = data.getOrNull(index) ?: 0f
                        val barHeightRatio = (value / maxCalories).coerceIn(0f, 1f)
                        val isSelected = selectedIndex == index

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedIndex = if (isSelected) null else index
                                }
                        ) {
                            if (isSelected) {
                                Text(
                                    text = "${value.toInt()}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                            Box(
                                modifier = Modifier
                                    .height((120 * barHeightRatio).dp)
                                    .width(12.dp)
                                    .background(
                                        color = if (value > 2000f) Color(0xFFFF5252) else Color(0xFF4CAF50),
                                        shape = RoundedCornerShape(6.dp)
                                    )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = day,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Botão que gere a permissão de notificações e inicia o [ForegroundService]
 * para monitorização em segundo plano.
 */
@Composable
fun NotificationControlButton() {
    val context = LocalContext.current

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                androidx.core.content.ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasNotificationPermission = isGranted
            if (isGranted) {
                val intent = Intent(context, ForegroundService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            }
        }
    )

    Button(
        onClick = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                val intent = Intent(context, ForegroundService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
    ) {
        Text(if (hasNotificationPermission) "Ativar Monitorização" else "Permitir Notificações")
    }
}