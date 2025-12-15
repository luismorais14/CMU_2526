package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ipp.estg.cmu_2526_8230258_8230204_8230153.ui.theme.CMU_2526_8230258_8230204_8230153Theme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(
    viewModel: DiaryViewModel = viewModel()
) {
    val selectedDate by viewModel.selectedDate.observeAsState(LocalDate.now())
    val meals by viewModel.meals.observeAsState(emptyList())
    val suggestions by viewModel.suggestions.observeAsState(emptyList())
    val scrollState = rememberScrollState()
    val dailyCalories by viewModel.dailyCalories.observeAsState(0)
    val dailyProtein by viewModel.dailyProtein.observeAsState(0)
    val dailyCarbs by viewModel.dailyCarbs.observeAsState(0)
    val dailyFat by viewModel.dailyFat.observeAsState(0)
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState()

    val state = DiaryUiState(
        selectedDate = selectedDate,
        meals = meals,
        dailyCalories = dailyCalories,
        goalCalories = 2000,
        dailyProtein = dailyProtein,
        dailyCarbs = dailyCarbs,
        dailyFat = dailyFat,
        isLoading = isLoading,
        error = error,
        suggestions = suggestions
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        val selectedDate by viewModel.selectedDate.observeAsState(LocalDate.now())

        DateSelector(
            currentDate = selectedDate,
            onDateChanged = { viewModel.selectDate(it) }
        )

        Spacer(Modifier.height(16.dp))

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            state.error?.let { error ->
                AlertCard(
                    message = error,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            DailySummarySection(state)

            Spacer(Modifier.height(16.dp))

            if (state.suggestions.isNotEmpty()) {
                Text(
                    text = "Recomendado pelo Plano",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                SuggestionsList(
                    suggestions = state.suggestions,
                    onAddSuggestion = { food ->
                        viewModel.addSuggestionToMeal(food)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            MealsListSection(
                meals = state.meals,
                onRemoveFood = { foodId, mealId ->
                    val foodIdLong = foodId.toLongOrNull()
                    val mealIdLong = mealId.toLongOrNull()
                    if (foodIdLong != null && mealIdLong != null) {
                        viewModel.deleteFood(foodIdLong, mealIdLong)
                    }
                }
            )
        }
    }
}

@Composable
fun SuggestionsList(
    suggestions: List<FoodUi>,
    onAddSuggestion: (FoodUi) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(suggestions) { food ->
            SuggestionCard(food, onAddSuggestion)
        }
    }
}

@Composable
fun SuggestionCard(
    food: FoodUi,
    onAdd: (FoodUi) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        modifier = Modifier.width(160.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                IconButton(
                    onClick = { onAdd(food) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Adicionar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = food.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = "${food.calories} kcal",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}


@Composable
fun AlertCard(
    message: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.errorContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
fun MealsListSection(
    meals: List<MealUi>,
    onRemoveFood: (foodId: String, mealId: String) -> Unit
) {
    if (meals.isNotEmpty()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            meals.forEach { meal ->
                MealCard(
                    meal = meal,
                    onRemoveFood = { foodId ->
                        onRemoveFood(foodId, meal.id)
                    }
                )
            }
        }
    } else {
        EmptyStateMessage()
    }
}

@Composable
fun EmptyStateMessage() {
    Box(
        modifier = Modifier.padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Sem refeições registadas",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Nenhuma refeição registada",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Adicione alimentos para ver o seu diário alimentar",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MealCard(
    meal: MealUi,
    onRemoveFood: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = getMealTypeName(meal.type),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${meal.totalCalories} kcal",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (meal.foods.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    meal.foods.forEach { food ->
                        FoodItemRow(
                            food = food,
                            onRemove = { onRemoveFood(food.id) }
                        )
                    }
                }
            } else {
                Text(
                    text = "Sem alimentos nesta refeição",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun FoodItemRow(
    food: FoodUi,
    onRemove: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = food.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NutritionBadge(
                        text = "${food.calories} kcal",
                        color = MaterialTheme.colorScheme.primary
                    )
                    NutritionBadge(
                        text = "P:${food.protein}g",
                        color = MaterialTheme.colorScheme.secondary
                    )
                    NutritionBadge(
                        text = "C:${food.carbs}g",
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    NutritionBadge(
                        text = "G:${food.fat}g",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Remover comida",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun NutritionBadge(
    text: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        fontWeight = FontWeight.Medium,
        modifier = modifier
            .background(
                color = color.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.extraSmall
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}

@Composable
fun DailySummarySection(state: DiaryUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Resumo do Dia - ${state.selectedDate}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            CaloriesProgress(
                current = state.dailyCalories,
                goal = state.goalCalories
            )

            Spacer(modifier = Modifier.height(16.dp))

            NutritionGrid(
                protein = state.dailyProtein,
                carbs = state.dailyCarbs,
                fat = state.dailyFat
            )
        }
    }
}

@Composable
fun CaloriesProgress(
    current: Int,
    goal: Int
) {
    val progress = if (goal > 0) current.toFloat() / goal else 0f
    val percentage = (progress * 100).toInt()

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Calorias",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "$current / $goal kcal ($percentage%)",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun NutritionGrid(
    protein: Int,
    carbs: Int,
    fat: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        NutritionCard("Proteína", "$protein g", MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.width(8.dp))
        NutritionCard("Hidratos", "$carbs g", MaterialTheme.colorScheme.tertiary)
        Spacer(modifier = Modifier.width(8.dp))
        NutritionCard("Gordura", "$fat g", MaterialTheme.colorScheme.error)
    }
}

@Composable
fun NutritionCard(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.padding(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun DateSelector(
    currentDate: LocalDate,
    onDateChanged: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val minDate = today.minusDays(2)
    val formatter = DateTimeFormatter.ofPattern("EEE, d MMM yyyy")

    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
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
                enabled = !currentDate.isEqual(minDate) && !currentDate.isBefore(minDate)
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

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (currentDate.isEqual(today)) "Hoje" else
                        if (currentDate.isEqual(today.minusDays(1))) "Ontem" else
                            currentDate.format(DateTimeFormatter.ofPattern("EEE")),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(2.dp))
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
                enabled = !currentDate.isEqual(today) && !currentDate.isAfter(today)
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

private fun getMealTypeName(type: MealType): String {
    return when (type) {
        MealType.BREAKFAST -> "Pequeno-almoço"
        MealType.LUNCH -> "Almoço"
        MealType.SNACK -> "Lanche"
        MealType.DINNER -> "Jantar"
        MealType.OTHER -> "Outros / Extras"
    }
}

@Preview(showBackground = true, name = "Diary Screen Preview")
@Composable
fun DiaryScreenPreview() {
    CMU_2526_8230258_8230204_8230153Theme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            DiaryScreen()
        }
    }
}