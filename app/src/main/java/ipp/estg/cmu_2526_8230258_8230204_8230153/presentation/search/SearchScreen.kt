package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import java.time.format.DateTimeFormatter

/**
 * Ecrã principal de pesquisa de alimentos.
 *
 * Este ecrã permite ao utilizador:
 * 1. Visualizar para que refeição (ex: Almoço) e data está a adicionar alimentos.
 * 2. Pesquisar alimentos através de uma barra de texto conectada à API.
 * 3. Visualizar o estado de carregamento e eventuais erros.
 * 4. Visualizar uma lista de resultados e selecionar um alimento para adicionar ao diário.
 *
 * @param state O estado atual da UI ([SearchUiState]), contendo a query, resultados, flags de loading e erros.
 * @param onEvent Callback para enviar eventos de interação (ex: digitar, pesquisar, clicar num alimento) para o ViewModel.
 * @param onNavigateBack Callback para navegar para o ecrã anterior (geralmente o Diário).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    state: SearchUiState,
    onEvent: (SearchEvent) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "Adicionar ao ${state.mealType}")
                        Text(
                            text = "Data: ${
                                state.selectedDate.format(
                                    DateTimeFormatter.ofPattern("EEE, d MMM")
                                )
                            }",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            state.error?.let { error ->
                AlertCard(
                    message = error,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { onEvent(SearchEvent.OnQueryChanged(it)) },
                label = { Text("Procurar alimento...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    onEvent(SearchEvent.OnSearch)
                    keyboardController?.hide()
                })
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.searchResults) { foodItem ->
                        FoodResultItem(
                            food = foodItem,
                            onClick = { onEvent(SearchEvent.OnFoodClicked(foodItem)) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Componente visual para exibir mensagens de alerta no ecrã de pesquisa.
 * Pode ser usado tanto para mensagens de erro como para confirmações de sucesso.
 *
 * @param message A mensagem de texto a ser exibida.
 * @param modifier Modificador para ajustar o layout ou aparência do cartão.
 * @param isSuccess Define o estilo visual: Verde para sucesso (true), Vermelho para erro (false). O padrão é erro.
 */
@Composable
fun AlertCard(
    message: String,
    modifier: Modifier = Modifier,
    isSuccess: Boolean = false
) {
    Surface(
        modifier = modifier,
        color = if (isSuccess) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.errorContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = if (isSuccess) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

/**
 * Item individual da lista de resultados da pesquisa.
 *
 * Apresenta o nome do alimento, as calorias e a fonte da informação, juntamente com um ícone de adição.
 *
 * @param food O objeto [FoodItem] contendo os dados do alimento a apresentar.
 * @param onClick Ação a ser executada quando o utilizador clica no cartão (adicionar o alimento).
 */
@Composable
private fun FoodResultItem(food: FoodItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = food.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${food.calories} kcal - ${food.source}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Icon(Icons.Default.Add, contentDescription = "Adicionar")
        }
    }
}