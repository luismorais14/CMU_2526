package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.plans

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ipp.estg.cmu_2526_8230258_8230204_8230153.R
import ipp.estg.cmu_2526_8230258_8230204_8230153.ui.theme.CMU_2526_8230258_8230204_8230153Theme

/**
 * Modelo de dados que representa um plano de refeições.
 *
 * @property id Identificador único do plano.
 * @property title Título do plano (ex: "Dieta Keto").
 * @property duration Duração prevista do plano (ex: "2 Semanas").
 * @property description Breve descrição do plano.
 * @property color Cor de fundo primária para a apresentação do cartão.
 * @property imageRes ID do recurso de desenho (Drawable) para a imagem associada ao plano.
 * @property routeId ID de rota usado para navegação para os detalhes do plano.
 */
data class MealPlan(
    val id: Int,
    val title: String,
    val duration: String,
    val description: String,
    val color: Color,
    @DrawableRes val imageRes: Int,
    val routeId: String
)

/**
 * Ecrã de Planos de Refeição.
 *
 * Exibe uma lista de planos disponíveis, destacando o plano atualmente ativo (se houver).
 * Permite a navegação para o ecrã de detalhes do plano.
 *
 * @param navController Controlador de navegação para gerir a transição entre ecrãs.
 * @param activePlanId O ID do plano de refeição atualmente ativo pelo utilizador (null se nenhum estiver ativo).
 */
@Composable
fun PlansScreen(navController: NavHostController, activePlanId: Int? = null) {

    val allPlans = remember {
        listOf(
            MealPlan(1, "Alimentação Limpa", "2 Semanas", "Um plano de refeição baseado em alimentos frescos...", Color(0xFFD6D6FF), R.drawable.plano_limpa, "limpa"),
            MealPlan(2, "Económico", "2 Semanas", "Um plano de refeição perfeito para manter seu orçamento...", Color(0xFFFFF5D6), R.drawable.plano_economico, "economico"),
            MealPlan(3, "Rico em Proteína", "2 Semanas", "Um plano de refeição para aumentar a biodisponibilidade...", Color(0xFFD6F5F5), R.drawable.plano_proteina, "proteina"),
            MealPlan(4, "Dieta Keto", "1 Semana", "Um plano com baixo teor de carboidratos...", Color(0xFFEED6FF), R.drawable.plano_keto, "keto")
        )
    }

    val activePlanData = allPlans.find { it.id == activePlanId }
    val availablePlans = if (activePlanData != null) {
        allPlans.filter { it.id != activePlanId }
    } else {
        allPlans
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (activePlanData != null) {
            item {
                Text(
                    text = "Plano de Refeição Ativo",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            item {
                PlanRowCard(
                    plan = activePlanData,
                    onClick = { navController.navigate("planDetail/${activePlanData.routeId}") }
                )
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
        }

        item {
            Text(
                text = "Planos de Refeição Disponíveis",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(availablePlans) { plan ->
            PlanRowCard(
                plan = plan,
                onClick = { navController.navigate("planDetail/${plan.routeId}") }
            )
        }
    }
}

/**
 * Cartão que exibe de forma concisa as informações de um plano de refeição.
 *
 * @param plan O objeto [MealPlan] a ser exibido.
 * @param onClick Ação a ser executada ao clicar no cartão (navegar para detalhes).
 */
@Composable
private fun PlanRowCard(
    plan: MealPlan,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = plan.color)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .height(110.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = plan.duration,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black.copy(alpha = 0.6f)
                )
                Text(
                    text = plan.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = plan.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    color = Color.Black.copy(alpha = 0.7f)
                )
            }
            Spacer(Modifier.width(16.dp))

            Image(
                painter = painterResource(id = plan.imageRes),
                contentDescription = plan.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

/**
 * Pré-visualização do ecrã de Planos.
 */
@Preview(showBackground = true)
@Composable
fun PlansScreenPreview() {
    CMU_2526_8230258_8230204_8230153Theme {
        PlansScreen(navController = rememberNavController(), activePlanId = 2)
    }
}