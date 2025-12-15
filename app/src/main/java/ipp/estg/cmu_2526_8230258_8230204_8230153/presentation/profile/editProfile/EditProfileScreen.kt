package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.profile.editProfile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ipp.estg.cmu_2526_8230258_8230204_8230153.ui.theme.CMU_2526_8230258_8230204_8230153Theme

/**
 * Ecrã que permite ao utilizador editar as suas informações de perfil (nome, email, altura, peso)
 * e alterar a palavra-passe.
 *
 * O ecrã utiliza um [OutlinedTextField] para cada campo e inclui validação básica
 * de confirmação de palavra-passe na UI, além da gestão de estados de carregamento e erros.
 *
 * @param state O estado atual da UI de edição de perfil ([EditProfileUiState]).
 * @param onEvent Callback para enviar eventos de alteração de campo ou clique em guardar para o ViewModel.
 * @param modifier Modificador para ajustar o layout do ecrã.
 */
@Composable
fun EditProfileScreen(
    state: EditProfileUiState,
    onEvent: (EditProfileEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Dados Pessoais",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        OutlinedTextField(
            value = state.nome,
            onValueChange = { onEvent(EditProfileEvent.OnNameChanged(it)) },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.email,
            onValueChange = { onEvent(EditProfileEvent.OnEmailChanged(it)) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        OutlinedTextField(
            value = state.altura,
            onValueChange = { onEvent(EditProfileEvent.OnHeightChanged(it)) },
            label = { Text("Altura (cm)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = state.peso,
            onValueChange = { onEvent(EditProfileEvent.OnWeightChanged(it)) },
            label = { Text("Peso (kg)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Password",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Preencha apenas se quiser alterar a password.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = state.passwordNova,
            onValueChange = { onEvent(EditProfileEvent.OnNewPasswordChanged(it)) },
            label = { Text("Nova Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        OutlinedTextField(
            value = state.passwordConfirmacao,
            onValueChange = { onEvent(EditProfileEvent.OnConfirmPasswordChanged(it)) },
            label = { Text("Confirmar Nova Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = state.passwordNova != state.passwordConfirmacao && state.passwordConfirmacao.isNotEmpty(),
            supportingText = {
                if (state.passwordNova != state.passwordConfirmacao && state.passwordConfirmacao.isNotEmpty()) {
                    Text("As passwords não coincidem")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (state.errorMessagem != null) {
            Text(
                text = state.errorMessagem,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(
            onClick = { onEvent(EditProfileEvent.OnSaveClicked)},
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            if (state.isLoading) {
                Text("A guardar...")
            } else {
                Text("Guardar Alterações")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

/**
 * Pré-visualização do ecrã completo de Edição de Perfil.
 */
@Preview(showBackground = true, name = "Ecrã Editar Perfil")
@Composable
fun EditProfileScreenPreview() {
    CMU_2526_8230258_8230204_8230153Theme(darkTheme = true) {

        val previewState = EditProfileUiState(
            nome = "Luísa (Preview)",
            email = "preview@email.com",
            altura = "170"
        )

        EditProfileScreen(
            state = previewState,
            onEvent = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}