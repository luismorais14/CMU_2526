package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.plans

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ipp.estg.cmu_2526_8230258_8230204_8230153.R
import ipp.estg.cmu_2526_8230258_8230204_8230153.ui.theme.CMU_2526_8230258_8230204_8230153Theme

/**
 * Modelo de dados interno para armazenar as informações detalhadas de um plano.
 */
private data class PlanData(
    val id: Int,
    val title: String,
    val duration: String,
    val description: String,
    @DrawableRes val mainImageRes: Int,
    val backgroundColor: Color
)

/**
 * Ecrã de Detalhes do Plano de Refeição.
 *
 * Exibe informações completas sobre um plano específico e permite ao utilizador
 * ativá-lo, a menos que já esteja ativo.
 *
 * @param navController Controlador de navegação para gerir o regresso.
 * @param planId O ID da rota do plano selecionado (ex: "economico", "keto").
 * @param activePlanId O ID do plano atualmente ativo (usado para desativar o botão "Iniciar Plano").
 * @param onStartPlan Lambda para ser chamada quando o utilizador decide iniciar o plano.
 */
@Composable
fun PlanDetailScreen(
    navController: NavHostController,
    planId: String?,
    activePlanId: Int? = null,
    onStartPlan: (Int) -> Unit = {}
) {
    val planData = when (planId) {
        "economico" -> PlanData(
            id = 2,
            title = "Económico",
            duration = "2 SEMANAS",
            description = "Um plano de refeição perfeito para manter seu orçamento alimentar sob controle. Foca-se em ingredientes acessíveis, receitas simples e minimiza o desperdício.",
            mainImageRes = R.drawable.plano_economico,
            backgroundColor = Color(0xFFFFF5D6)
        )
        "proteina" -> PlanData(
            id = 3,
            title = "Rico em Proteína",
            duration = "2 SEMANAS",
            description = "Um plano de refeição concebido para maximizar a ingestão de proteínas, essencial para a construção muscular e para aumentar a saciedade. Ideal para quem pratica atividade física intensa.",
            mainImageRes = R.drawable.plano_proteina,
            backgroundColor = Color(0xFFD6F5F5)
        )
        "keto" -> PlanData(
            id = 4,
            title = "Dieta Keto",
            duration = "1 SEMANA",
            description = "Um plano de refeição com baixo teor de carboidratos e alto teor de gordura. O objetivo é levar o corpo a um estado de cetose, onde a gordura é queimada para obter energia.",
            mainImageRes = R.drawable.plano_keto,
            backgroundColor = Color(0xFFEED6FF)
        )
        else -> PlanData( // Default: "limpa"
            id = 1,
            title = "Alimentação Limpa",
            duration = "2 SEMANAS",
            description = "O plano alimentar limpo envolve o consumo de alimentos minimamente processados e ricos em nutrientes, como frutas, vegetais, proteínas magras e grãos integrais, evitando açúcares adicionados e aditivos.",
            mainImageRes = R.drawable.plano_limpa,
            backgroundColor = Color(0xFFD6D6FF)
        )
    }

    val isActive = activePlanId == planData.id

    Scaffold(
        bottomBar = {
            Button(
                onClick = {
                    if (!isActive) {
                        onStartPlan(planData.id)
                        navController.popBackStack()
                    }
                },
                enabled = !isActive,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp)
            ) {
                Text(if (isActive) "Plano Ativo" else "Iniciar Plano")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(planData.backgroundColor)
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar"
                    )
                }

                Image(
                    painter = painterResource(id = planData.mainImageRes),
                    contentDescription = planData.title,
                    modifier = Modifier
                        .size(180.dp)
                        .clip(CircleShape)
                        .align(Alignment.Center),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = planData.duration,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = planData.title,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Sobre o Plano de Refeição",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = planData.description,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/**
 * Pré-visualização do ecrã de Detalhes do Plano.
 */
@Preview(showBackground = true)
@Composable
fun PlanDetailScreenPreview() {
    CMU_2526_8230258_8230204_8230153Theme {
        PlanDetailScreen(
            navController = rememberNavController(),
            planId = "limpa"
        )
    }
}