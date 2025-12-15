package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.profile.personalInfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ipp.estg.cmu_2526_8230258_8230204_8230153.ui.theme.CMU_2526_8230258_8230204_8230153Theme

/**
 * Ecrã que exibe as informações pessoais do utilizador de forma estruturada.
 *
 * Apresenta dados de perfil como nome, email e dados biométricos, obtidos através
 * do [PersonalInfoUiState].
 *
 * @param state O estado atual da UI contendo os dados do utilizador.
 * @param onEvent Callback para processar eventos de interação (atualmente não usado).
 * @param modifier Modificador para ajustar o layout do ecrã.
 */
@Composable
fun PersonalInfoScreen(
    state: PersonalInfoUiState,
    onEvent: (PersonalInfoEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        InfoItem(label = "Nome", value = state.nome)
        HorizontalDivider()
        InfoItem(label = "Email", value = state.email)
        HorizontalDivider()
        InfoItem(label = "Altura", value = state.altura)
        HorizontalDivider()
        InfoItem(label = "Peso Inicial", value = state.pesoInicial)
        HorizontalDivider()
        InfoItem(label = "Peso Atual", value = state.pesoAtual)
        HorizontalDivider()
        InfoItem(label = "Meta de Peso", value = state.metaPeso)    }
}

/**
 * Componente reutilizável para exibir um par rótulo-valor de informação.
 *
 * @param label O nome do campo (ex: "Nome", "Altura").
 * @param value O valor real do campo (ex: "João Silva", "180 cm").
 */
@Composable
private fun InfoItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Pré-visualização do ecrã completo de Informações Pessoais.
 */
@Preview(showBackground = true, name = "Ecrã Informações Pessoais")
@Composable
fun PersonalInfoScreenPreview() {
    CMU_2526_8230258_8230204_8230153Theme(darkTheme = true) {

        val previewState = PersonalInfoUiState(
            nome = "Luísa (Preview)",
            email = "preview@email.com",
            altura = "170 cm",
            pesoInicial = "60,0 kg",
            pesoAtual = "76,0 kg",
            metaPeso = "Perder Peso"
        )

        PersonalInfoScreen(
            state = previewState,
            onEvent = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * Pré-visualização do componente de item de informação individual.
 */
@Preview(showBackground = true, name = "Item de Informação")
@Composable
private fun InfoItemPreview() {
    CMU_2526_8230258_8230204_8230153Theme(darkTheme = true) {
        Column(modifier = Modifier.padding(16.dp)) {
            InfoItem(label = "Email", value = "preview@email.com")
        }
    }
}