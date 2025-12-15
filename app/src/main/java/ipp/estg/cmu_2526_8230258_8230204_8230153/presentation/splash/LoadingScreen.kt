package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ipp.estg.cmu_2526_8230258_8230204_8230153.ui.theme.CMU_2526_8230258_8230204_8230153Theme
import kotlinx.coroutines.delay
import kotlin.math.min

/**
 * Composable que apresenta o ecrã de carregamento animado da aplicação.
 *
 * Este ecrã combina elementos de UI padrão (texto e barra de progresso) com uma animação
 * personalizada ([AppleBouncing]). A barra de progresso simula um carregamento contínuo
 * e reinicia ciclicamente até que a navegação ocorra.
 * A animação da maçã é interativa e reage ao toque do utilizador.
 *
 * @param modifier Modificador para ajustar o layout ou aparência do componente raiz.
 * @param onFinished Callback opcional invocado quando o ciclo de progresso simulado atinge 100%.
 */
@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
    onFinished: (() -> Unit)? = null,
) {

    var progress by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            val steps = 100
            repeat(steps) {
                progress = it / steps.toFloat()
                delay(18)
            }
            progress = 1f
            onFinished?.invoke()
            delay(250)
            progress = 0f
        }
    }

    var taps by remember { mutableIntStateOf(0) }
    val tapScale by animateFloatAsState(
        targetValue = if (taps == 0) 1f else 0.9f,
        animationSpec = tween(180),
        label = "tapScale"
    )
    LaunchedEffect(taps) {
        if (taps > 0) {
            delay(180)
            taps = 0
        }
    }

    Column(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Food Logger",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )


        AppleBouncing(
            diameter = 140.dp,
            modifier = Modifier
                .scale(tapScale)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { taps++ }
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "A preparar o teu diário…",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}


/**
 * Componente gráfico personalizado que desenha uma maçã animada utilizando Canvas.
 *
 * A maçã possui duas animações infinitas:
 * 1. Bounce: Movimento vertical e alteração da sombra para simular um salto.
 * 2. Wiggle: Rotação ligeira para dar um efeito de "gelatina" ou movimento orgânico.
 *
 * O desenho é feito programaticamente através de primitivas gráficas (círculos, retângulos, paths).
 * Otimizado para evitar alocação de memória durante o desenho.
 *
 * @param diameter O tamanho (diâmetro) da área onde a maçã será desenhada.
 * @param modifier Modificador para aplicar transformações (como escala ou cliques) ao componente.
 */
@Composable
private fun AppleBouncing(
    diameter: Dp,
    modifier: Modifier = Modifier
) {
    val infinite = rememberInfiniteTransition(label = "apple")
    val bounce by infinite.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000, easing = EaseInOut), RepeatMode.Reverse),
        label = "bounce"
    )
    val wiggle by infinite.animateFloat(
        initialValue = -6f, targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Reverse),
        label = "wiggle"
    )

    val bgColor = MaterialTheme.colorScheme.background

    val leafPath = remember { Path() }

    Box(
        modifier = modifier.size(diameter).padding(bottom = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        val shadowScale = 0.7f + 0.3f * (1 - bounce)
        Box(
            Modifier.fillMaxWidth(shadowScale).height(10.dp)
                .background(Color.Black.copy(alpha = 0.08f), CircleShape)
                .align(Alignment.BottomCenter)
        )

        Canvas(
            modifier = Modifier.size(diameter).offset(y = ((-10).dp * bounce)).rotate(wiggle)
        ) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h * 0.55f
            val bodyR = min(w, h) * 0.28f

            val leftCenter = Offset(cx - bodyR * 0.9f, cy)
            val rightCenter = Offset(cx + bodyR * 0.9f, cy)
            val appleRed = Color(0xFFE53935)
            val appleDark = Color(0xFFC62828)

            drawCircle(color = appleRed, radius = bodyR * 1.1f, center = leftCenter)
            drawCircle(color = appleDark, radius = bodyR * 1.1f, center = rightCenter)

            drawRect(
                color = bgColor,
                topLeft = Offset(cx - bodyR * 2f, cy + bodyR),
                size = Size(bodyR * 4f, bodyR * 1.4f)
            )

            drawRect(
                color = Color(0xFF6D4C41),
                topLeft = Offset(cx - 6f, cy - bodyR * 2.2f),
                size = Size(12f, bodyR * 0.9f)
            )

            val leafCenter = Offset(cx + bodyR * 0.2f, cy - bodyR * 2.1f)

            leafPath.reset()
            leafPath.moveTo(leafCenter.x - 8f, leafCenter.y)
            leafPath.quadraticBezierTo(leafCenter.x + 26f, leafCenter.y - 18f, leafCenter.x + 48f, leafCenter.y)
            leafPath.quadraticBezierTo(leafCenter.x + 26f, leafCenter.y + 18f, leafCenter.x - 8f, leafCenter.y)
            leafPath.close()

            drawPath(path = leafPath, color = Color(0xFF2E7D32))
        }
    }
}

/**
 * Pré-visualização do ecrã completo de carregamento no Android Studio.
 */
@Preview(showBackground = true, name = "Ecrã de Loading (Completo)")
@Composable
fun LoadingScreenPreview() {
    CMU_2526_8230258_8230204_8230153Theme(darkTheme = true) {
        LoadingScreen()
    }
}

/**
 * Pré-visualização isolada da animação da maçã no Android Studio.
 */
@Preview(showBackground = true, name = "Animação da Maçã")
@Composable
private fun AppleBouncingPreview() {
    CMU_2526_8230258_8230204_8230153Theme(darkTheme = true) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(contentAlignment = Alignment.Center) {
                AppleBouncing(diameter = 140.dp)
            }
        }
    }
}