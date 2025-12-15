package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.leaderboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ipp.estg.cmu_2526_8230258_8230204_8230153.ui.theme.CMU_2526_8230258_8230204_8230153Theme

/**
 * Ecrã principal da Leaderboard (Tabela de Classificação).
 *
 * Observa o estado do [LeaderboardViewModel] e delega a renderização para [LeaderboardContent].
 *
 * @param viewModel O ViewModel que contém a lógica e o estado da Leaderboard.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    viewModel: LeaderboardViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    LeaderboardContent(uiState = uiState)
}

/**
 * Conteúdo da Leaderboard, que inclui o cabeçalho e a lista de classificação.
 *
 * @param uiState O estado atual da UI da Leaderboard.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardContent(
    uiState: LeaderboardUiState
) {
    Scaffold(
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            LeaderboardHeader()

            Spacer(modifier = Modifier.height(24.dp))

            LeaderboardList(
                items = uiState.leaderboardItems,
                isLoading = uiState.isLoading
            )
        }
    }
}

/**
 * Componente do cabeçalho da Leaderboard.
 */
@Composable
fun LeaderboardHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Classificação Geral",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Exibe a lista principal de itens da Leaderboard.
 *
 * Gere os estados de carregamento e lista vazia.
 *
 * @param items A lista de [LeaderboardItem] a serem exibidos.
 * @param isLoading Indica se o carregamento está em curso.
 */
@Composable
fun LeaderboardList(
    items: List<LeaderboardItem>,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            if (items.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Sem dados disponíveis.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items) { item ->
                        LeaderboardItemRow(item = item)
                    }
                }
            }
        }
    }
}

/**
 * Linha individual na Leaderboard, exibindo a posição, o utilizador e a pontuação.
 *
 * Destaca o item do utilizador atual com cores e elevação diferentes.
 *
 * @param item O [LeaderboardItem] a ser exibido.
 */
@Composable
fun LeaderboardItemRow(
    item: LeaderboardItem
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (item.isCurrentUser) 6.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isCurrentUser)
                MaterialTheme.colorScheme.primaryContainer // Cor de destaque para o utilizador atual
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.width(40.dp),
                contentAlignment = Alignment.Center
            ) {
                when (item.position) {
                    1 -> Text("🥇", style = MaterialTheme.typography.headlineSmall)
                    2 -> Text("🥈", style = MaterialTheme.typography.headlineSmall)
                    3 -> Text("🥉", style = MaterialTheme.typography.headlineSmall)
                    else -> Text(
                        text = "${item.position}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isCurrentUser) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.flagEmoji,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Column {
                    Text(
                        text = item.username,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (item.isCurrentUser) FontWeight.Bold else FontWeight.Medium,
                        color = if (item.isCurrentUser) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = item.countryName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = "${item.score} pts",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = if (item.isCurrentUser) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Pré-visualização do conteúdo da Leaderboard.
 */
@Preview(showBackground = true, name = "Leaderboard Preview")
@Composable
fun LeaderboardGlobalPreview() {
    val dummyItems = listOf(
        LeaderboardItem(1, "Ana Silva", "Portugal", "PT", 1500, "🇵🇹", false),
        LeaderboardItem(2, "João Santos", "Brasil", "BR", 1200, "🇧🇷", true),
        LeaderboardItem(3, "Mike Jones", "USA", "US", 900, "🇺🇸", false),
        LeaderboardItem(4, "Sophie Dubois", "França", "FR", 750, "🇫🇷", false),
        LeaderboardItem(5, "Chen Wei", "China", "CN", 600, "🇨🇳", false),
    )

    CMU_2526_8230258_8230204_8230153Theme {
        LeaderboardContent(
            uiState = LeaderboardUiState(
                leaderboardItems = dummyItems,
                userCountry = "Portugal"
            )
        )
    }
}