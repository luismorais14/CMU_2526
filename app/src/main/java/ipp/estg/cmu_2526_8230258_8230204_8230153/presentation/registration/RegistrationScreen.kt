package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.registration

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.utils.activityLevels
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.utils.fitnessGoals
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.utils.registrationSteps

/**
 * Ecrã principal de registo de utilizador.
 *
 * Implementa um assistente (wizard) multi-etapa para recolher dados de autenticação e
 * biométricos do utilizador. O ecrã utiliza um [Scaffold] com uma barra de progresso
 * para indicar o passo atual.
 *
 * @param state O estado atual da UI do registo ([RegistrationUiState]).
 * @param onEvent Callback para enviar eventos de interação para o [RegistrationViewModel].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    state: RegistrationUiState,
    onEvent: (RegistrationEvent) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Criar conta", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    if (state.currentStep > 0) {
                        IconButton(onClick = { onEvent(RegistrationEvent.PreviousStep) }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            LinearProgressIndicator(
                progress = (state.currentStep + 1) / registrationSteps.size.toFloat(),
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            RegistrationStepContent(
                state = state,
                onEvent = onEvent
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botão "Seguinte" aparece até ao passo 5 (excluindo os passos de seleção 6 e 7)
            if (state.currentStep < 6) {
                Button(
                    onClick = { onEvent(RegistrationEvent.NextStep) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Seguinte")
                }
            }
        }
    }
}

/**
 * Container principal que renderiza o conteúdo específico de cada passo do assistente de registo.
 *
 * @param state O estado atual da UI do registo.
 * @param onEvent Callback para enviar eventos para o ViewModel.
 */
@Composable
fun RegistrationStepContent(
    state: RegistrationUiState,
    onEvent: (RegistrationEvent) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = registrationSteps[state.currentStep],
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            when (state.currentStep) {
                0 -> NameStep(state, onEvent)
                1 -> EmailStep(state, onEvent)
                2 -> PasswordStep(state, onEvent)
                3 -> AgeStep(state, onEvent)
                4 -> WeightStep(state, onEvent)
                5 -> HeightStep(state, onEvent)
                6 -> FitnessGoalStep(state, onEvent)
                7 -> ActivityLevelStep(state, onEvent)
            }
        }
    }
}

/**
 * Passo 0: Recolha do Nome do utilizador.
 *
 * @param state O estado atual da UI.
 * @param onEvent Callback para enviar eventos.
 */
@Composable
fun NameStep(state: RegistrationUiState, onEvent: (RegistrationEvent) -> Unit) {
    OutlinedTextField(
        value = state.name,
        onValueChange = { onEvent(RegistrationEvent.NameChanged(it)) },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Nome") },
        singleLine = true,
        keyboardActions = KeyboardActions(onNext = { onEvent(RegistrationEvent.NextStep) }),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        leadingIcon = { Icon(Icons.Default.Person, "Nome") },
        isError = state.errorMessage != null
    )
    if (state.errorMessage != null) {
        Text(
            text = state.errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

/**
 * Passo 1: Recolha do Email do utilizador.
 *
 * @param state O estado atual da UI.
 * @param onEvent Callback para enviar eventos.
 */
@Composable
fun EmailStep(state: RegistrationUiState, onEvent: (RegistrationEvent) -> Unit) {
    OutlinedTextField(
        value = state.email,
        onValueChange = { onEvent(RegistrationEvent.EmailChanged(it)) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("seu@email.com") },
        leadingIcon = { Icon(Icons.Default.Email, "Email") },
        keyboardActions = KeyboardActions(onNext = { onEvent(RegistrationEvent.NextStep) }),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Email),
        singleLine = true,
    )
    if (state.errorMessage != null) {
        Text(
            text = state.errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

/**
 * Passo 2: Recolha da Palavra-passe do utilizador.
 *
 * @param state O estado atual da UI.
 * @param onEvent Callback para enviar eventos.
 */
@Composable
fun PasswordStep(state: RegistrationUiState, onEvent: (RegistrationEvent) -> Unit) {
    OutlinedTextField(
        value = state.password,
        onValueChange = { onEvent(RegistrationEvent.PasswordChanged(it)) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Mínimo 6 caracteres") },
        leadingIcon = { Icon(Icons.Default.Lock, "Senha") },
        keyboardActions = KeyboardActions(onNext = { onEvent(RegistrationEvent.NextStep) }),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Password),
        singleLine = true,
        visualTransformation = PasswordVisualTransformation()
    )
    if (state.errorMessage != null) {
        Text(
            text = state.errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

/**
 * Passo 3: Recolha da Idade do utilizador.
 *
 * @param state O estado atual da UI.
 * @param onEvent Callback para enviar eventos.
 */
@Composable
fun AgeStep(state: RegistrationUiState, onEvent: (RegistrationEvent) -> Unit) {
    OutlinedTextField(
        value = state.age,
        onValueChange = { onEvent(RegistrationEvent.AgeChanged(it)) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Sua idade") },
        singleLine = true,
        keyboardActions = KeyboardActions(onNext = { onEvent(RegistrationEvent.NextStep) }),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Number),
    )
    if (state.errorMessage != null) {
        Text(
            text = state.errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

/**
 * Passo 4: Recolha do Peso do utilizador.
 *
 * @param state O estado atual da UI.
 * @param onEvent Callback para enviar eventos.
 */
@Composable
fun WeightStep(state: RegistrationUiState, onEvent: (RegistrationEvent) -> Unit) {
    OutlinedTextField(
        value = state.weight,
        onValueChange = { onEvent(RegistrationEvent.WeightChanged(it)) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Peso em kg") },
        singleLine = true,
        keyboardActions = KeyboardActions(onNext = { onEvent(RegistrationEvent.NextStep) }),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Decimal),
    )
    if (state.errorMessage != null) {
        Text(
            text = state.errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

/**
 * Passo 5: Recolha da Altura do utilizador.
 *
 * @param state O estado atual da UI.
 * @param onEvent Callback para enviar eventos.
 */
@Composable
fun HeightStep(state: RegistrationUiState, onEvent: (RegistrationEvent) -> Unit) {
    OutlinedTextField(
        value = state.height,
        onValueChange = { onEvent(RegistrationEvent.HeightChanged(it)) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Altura em cm") },
        singleLine = true,
        keyboardActions = KeyboardActions(onNext = { onEvent(RegistrationEvent.NextStep) }),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Number)
    )
    if (state.errorMessage != null) {
        Text(
            text = state.errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

/**
 * Passo 6: Seleção do Objetivo de Fitness.
 *
 * @param state O estado atual da UI.
 * @param onEvent Callback para enviar eventos.
 */
@Composable
fun FitnessGoalStep(state: RegistrationUiState, onEvent: (RegistrationEvent) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        fitnessGoals.forEach { goal ->
            val isSelected = state.fitnessGoal == goal
            Card(
                modifier = Modifier.fillMaxWidth().clickable {
                    onEvent(RegistrationEvent.FitnessGoalChanged(goal))
                    onEvent(RegistrationEvent.NextStep)
                },
                elevation = CardDefaults.cardElevation(if (isSelected) 4.dp else 1.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                )
            ) {
                Text(text = goal, modifier = Modifier.padding(16.dp))
            }
        }
    }
}

/**
 * Passo 7: Seleção do Nível de Atividade.
 *
 * Este é o passo final que aciona o evento de submissão do registo.
 *
 * @param state O estado atual da UI.
 * @param onEvent Callback para enviar eventos.
 */
@Composable
fun ActivityLevelStep(state: RegistrationUiState, onEvent: (RegistrationEvent) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        activityLevels.forEach { level ->
            val isSelected = state.activityLevel == level
            Card(
                modifier = Modifier.fillMaxWidth().clickable {
                    onEvent(RegistrationEvent.ActivityLevelChanged(level))
                    onEvent(RegistrationEvent.SubmitRegistration)
                },
                elevation = CardDefaults.cardElevation(if (isSelected) 4.dp else 1.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                )
            ) {
                Text(text = level, modifier = Modifier.padding(16.dp))
            }
        }
    }
}