package com.example.castlerace.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.castlerace.ui.theme.CastleBorderHighlight
import com.example.castlerace.ui.theme.CastleIvory
import com.example.castlerace.ui.theme.DarkBackground
import com.example.castlerace.ui.theme.DarkStoneCellDark
import com.example.castlerace.ui.theme.DarkStoneCellLight
import com.example.castlerace.ui.theme.Player1Bronze
import com.example.castlerace.ui.theme.Player1BronzeGlow
import com.example.castlerace.ui.theme.Player1BronzeLight
import com.example.castlerace.ui.theme.Player2Teal
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit
) {
    // Animation progress states
    val boardAlpha = remember { Animatable(0f) }
    val castleAlpha = remember { Animatable(0f) }
    val pieceProgress = remember { Animatable(0f) }
    val captureProgress = remember { Animatable(0f) }
    val castlePulse = remember { Animatable(1f) }
    val titleAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Step 1: Board fades in (0-300ms)
        launch { boardAlpha.animateTo(1f, tween(300, easing = FastOutSlowInEasing)) }

        // Step 2: Castles appear (200-500ms)
        delay(200)
        launch { castleAlpha.animateTo(1f, tween(300, easing = FastOutSlowInEasing)) }

        // Step 3: Pieces advance (400-800ms)
        delay(200)
        launch { pieceProgress.animateTo(1f, tween(400, easing = FastOutSlowInEasing)) }

        // Step 4: Capture occurs (750-1000ms)
        delay(350)
        launch { captureProgress.animateTo(1f, tween(250, easing = FastOutSlowInEasing)) }

        // Step 5: Castle pulse & entry (950-1300ms)
        delay(200)
        launch {
            castlePulse.animateTo(
                targetValue = 1.35f,
                animationSpec = tween(180, easing = FastOutSlowInEasing)
            )
            castlePulse.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(180, easing = FastOutSlowInEasing)
            )
        }

        // Step 6: Title appears (1100-1500ms)
        delay(150)
        launch { titleAlpha.animateTo(1f, tween(350, easing = FastOutSlowInEasing)) }

        // Transition to Home at ~1.65 seconds
        delay(400)
        onNavigateToHome()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                // Tap to skip
                onNavigateToHome()
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Animated Splash Board Demonstration
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .alpha(boardAlpha.value),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val gridSize = 5
                    val cellSize = size.width / gridSize

                    // Mini board cells
                    for (r in 0 until gridSize) {
                        for (c in 0 until gridSize) {
                            val isAlt = (r + c) % 2 == 1
                            val cellColor = if (isAlt) DarkStoneCellDark else DarkStoneCellLight
                            drawRoundRect(
                                color = cellColor,
                                topLeft = Offset(c * cellSize + 1f, r * cellSize + 1f),
                                size = Size(cellSize - 2f, cellSize - 2f),
                                cornerRadius = CornerRadius(4f, 4f)
                            )
                        }
                    }

                    // Border outline
                    drawRoundRect(
                        color = CastleBorderHighlight.copy(alpha = 0.4f),
                        topLeft = Offset(0f, 0f),
                        size = size,
                        cornerRadius = CornerRadius(8f, 8f),
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // Castles (Top center (0,2) and Bottom center (4,2))
                    if (castleAlpha.value > 0f) {
                        val p2CastleCenter = Offset(2.5f * cellSize, 0.5f * cellSize)
                        val p1CastleCenter = Offset(2.5f * cellSize, 4.5f * cellSize)

                        // P1 Home Castle
                        drawCircle(
                            color = Player1Bronze.copy(alpha = 0.35f * castleAlpha.value),
                            radius = cellSize * 0.38f,
                            center = p1CastleCenter
                        )
                        drawCircle(
                            color = CastleIvory.copy(alpha = 0.8f * castleAlpha.value),
                            radius = cellSize * 0.22f,
                            center = p1CastleCenter,
                            style = Stroke(width = 2.dp.toPx())
                        )

                        // P2 Target Castle (pulsing on entry)
                        val p2Pulse = if (castlePulse.value > 1f) castlePulse.value else 1f
                        drawCircle(
                            color = CastleIvory.copy(alpha = 0.4f * castleAlpha.value),
                            radius = (cellSize * 0.45f) * p2Pulse,
                            center = p2CastleCenter
                        )
                        drawCircle(
                            color = Player2Teal.copy(alpha = 0.8f * castleAlpha.value),
                            radius = cellSize * 0.25f,
                            center = p2CastleCenter,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }

                    // Piece 2 (P2 Teal rival) starts at (1, 2) and gets captured
                    if (captureProgress.value < 1f) {
                        val p2Alpha = 1f - captureProgress.value
                        val p2Scale = 1f - (captureProgress.value * 0.5f)
                        drawCircle(
                            color = Player2Teal.copy(alpha = p2Alpha),
                            radius = cellSize * 0.28f * p2Scale,
                            center = Offset(2.5f * cellSize, 1.5f * cellSize)
                        )
                    }

                    // Piece 1 (P1 Bronze hero) starts at (3, 2) -> advances to (1, 2) capturing -> into Castle at (0, 2)
                    if (pieceProgress.value > 0f) {
                        val startY = 3.5f * cellSize
                        val targetY = 0.5f * cellSize
                        val currentY = startY + (targetY - startY) * pieceProgress.value

                        // Glow
                        drawCircle(
                            color = Player1BronzeGlow,
                            radius = cellSize * 0.38f,
                            center = Offset(2.5f * cellSize, currentY)
                        )

                        // Diamond Motif
                        val dSize = cellSize * 0.28f
                        val diamondPath = Path().apply {
                            moveTo(2.5f * cellSize, currentY - dSize)
                            lineTo(2.5f * cellSize + dSize, currentY)
                            lineTo(2.5f * cellSize, currentY + dSize)
                            lineTo(2.5f * cellSize - dSize, currentY)
                            close()
                        }
                        drawPath(diamondPath, color = Player1Bronze)
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Title & Subtitle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(titleAlpha.value)
            ) {
                Text(
                    text = "CASTLE RACE",
                    style = MaterialTheme.typography.headlineLarge,
                    color = CastleIvory,
                    letterSpacing = 4.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "ADVANCE  •  BREAK THROUGH  •  REACH THE CASTLE",
                    style = MaterialTheme.typography.labelMedium,
                    color = Player1BronzeLight,
                    letterSpacing = 1.5.sp
                )
            }
        }
    }
}
