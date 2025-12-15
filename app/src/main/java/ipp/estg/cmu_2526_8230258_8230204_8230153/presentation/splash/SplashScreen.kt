package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import ipp.estg.cmu_2526_8230258_8230204_8230153.ui.theme.CMU_2526_8230258_8230204_8230153Theme
import kotlinx.coroutines.delay

/**
 * Composable que representa o Ecrã de Splash da aplicação.
 *
 * Este ecrã é exibido inicialmente quando a aplicação arranca. Ele gere a transição
 * temporal, aguardando um período definido antes de sinalizar que terminou.
 * Durante a espera, exibe o componente visual de carregamento.
 *
 * @param onFinished Uma função de callback que é invocada quando o tempo de duração
 * do splash termina (atualmente 1000ms), indicando que a navegação deve prosseguir
 * para o próximo ecrã (Login ou Home).
 */
@Composable
fun SplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1000)
        onFinished()
    }
    LoadingScreen()
}

/**
 * Pré-visualização do Composable [SplashScreen] no Android Studio.
 *
 * Permite visualizar o layout estático do ecrã de splash com o tema da aplicação aplicado.
 */
@Preview(showBackground = true, name = "Splash Screen (Estático)")
@Composable
fun SplashScreenPreview() {
    CMU_2526_8230258_8230204_8230153Theme(darkTheme = true) {
        SplashScreen(
            onFinished = {}
        )
    }
}

/**
 * Pré-visualização isolada do componente visual de carregamento [LoadingScreen].
 *
 * Útil para ajustar o design do indicador de progresso ou logótipo sem a lógica de temporização.
 */
@Preview(showBackground = true, name = "Loading Screen (Componente)")
@Composable
fun LoadingScreenComponentPreview() {
    CMU_2526_8230258_8230204_8230153Theme(darkTheme = true) {
        LoadingScreen()
    }
}