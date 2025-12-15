package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.repository.AuthRepository
import ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.login.forgetPassword.ForgetPasswordEvent
import ipp.estg.cmu_2526_8230258_8230204_8230153.ui.theme.CMU_2526_8230258_8230204_8230153Theme

/**
 * Ecrã de Login.
 *
 * Permite ao utilizador autenticar-se através de email e password.
 * Também fornece links para o registo, recuperação de password e continuação como convidado.
 *
 * @param state O estado atual da UI de login ([LoginUiState]).
 * @param onEvent Callback para enviar eventos de interação para o [LoginViewModel].
 * @param onNavigateToRegistration Callback para iniciar a navegação para o ecrã de Registo.
 */
@Composable
fun LoginScreen(
    state : LoginUiState,
    onEvent : (LoginEvent) -> Unit,
    onNavigateToRegistration: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Outlined.Home,
                contentDescription = "Ícone de Login",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "BEM-VINDO DE VOLTA",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = { email -> onEvent(LoginEvent.EmailChanged(email)) },
                label = { Text("Endereço de Email") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = "Ícone de Email"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = state.loginError != null
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.password,
                onValueChange = { password -> onEvent(LoginEvent.PasswordChanged(password)) },
                label = { Text("Password") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = "Ícone de Password"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = state.loginError != null
            )

            if (state.loginError != null) {
                Text(
                    text = state.loginError ?: "Erro de login",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onEvent(LoginEvent.LoginClicked) },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Login")
                }
            }

            //Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onNavigateToRegistration,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Criar nova conta")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { onEvent(LoginEvent.ForgotPasswordClicked) }) {
                Text(
                    text = "Esqueceu-se da Password?",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }

            TextButton(onClick = { onEvent(LoginEvent.ContinueWithoutSession) }) {
                Text(
                    text = "Continuar sem iniciar sessão",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Pré-visualização do ecrã de Login no estado padrão.
 */
@Preview(name = "Estado Padrão", showBackground = true)
@Composable
fun LoginScreenPreview() {
    CMU_2526_8230258_8230204_8230153Theme(darkTheme = true) {
        LoginScreen(
            state = LoginUiState(email = "test@example.com"),
            onEvent = {}
        )
    }
}

/**
 * Pré-visualização do ecrã de Login no estado de carregamento (Loading).
 */
@Preview(name = "Estado de Loading", showBackground = true)
@Composable
fun LoginScreenLoadingPreview() {
    CMU_2526_8230258_8230204_8230153Theme(darkTheme = true) {
        LoginScreen(
            state = LoginUiState(isLoading = true, email = "user@loading.com", password = "123"),
            onEvent = {}
        )
    }
}

/**
 * Pré-visualização do ecrã de Login no estado de erro.
 */
@Preview(name = "Estado de Erro", showBackground = true)
@Composable
fun LoginScreenErrorPreview() {
    CMU_2526_8230258_8230204_8230153Theme(darkTheme = true) {
        LoginScreen(
            state = LoginUiState(loginError = "Password ou email inválidos"),
            onEvent = {}
        )
    }
}