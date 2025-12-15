package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.login.forgetPassword

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Ecrã de Recuperação de Palavra-passe.
 *
 * Permite ao utilizador introduzir o seu email para solicitar um link de reposição
 * de password, usando a lógica provida pelo [ForgetPasswordViewModel].
 *
 * @param onEvent Callback para enviar eventos de interação para o ViewModel.
 * @param state O estado atual da UI de recuperação de password ([ForgetPasswordUiState]).
 */
@Composable
fun ForgetPasswordScreen(
    onEvent: (ForgetPasswordEvent) -> Unit,
    state: ForgetPasswordUiState
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Recuperar Palavra-passe",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Introduza o seu email para enviarmos um link de recuperação.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = { onEvent(ForgetPasswordEvent.EmailChanged(it)) },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = state.emailError != null,
                supportingText = {
                    if (state.emailError != null) {
                        Text(
                            text = state.emailError ?: "Erro desconhecido",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { onEvent(ForgetPasswordEvent.Submit) },
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ENVIAR LINK")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Pré-visualização do ecrã de Recuperação de Palavra-passe no estado padrão.
 */
@Preview(name = "Default State", showBackground = true)
@Composable
fun ForgetPasswordScreenPreview() {
    ForgetPasswordScreen(
        onEvent = {},
        state = ForgetPasswordUiState(email = "exemplo@dominio.com")
    )
}

/**
 * Pré-visualização do ecrã de Recuperação de Palavra-passe no estado de carregamento (Loading).
 */
@Preview(name = "Loading State", showBackground = true)
@Composable
fun ForgetPasswordScreenLoadingPreview() {
    ForgetPasswordScreen(
        onEvent = {},
        state = ForgetPasswordUiState(email = "exemplo@dominio.com", isLoading = true)
    )
}