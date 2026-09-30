package com.example.castlerace.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.castlerace.logic.CastleRaceGameEngine
import com.example.castlerace.model.BoardTheme
import com.example.castlerace.model.CastlePiece
import com.example.castlerace.model.CastleRaceState
import com.example.castlerace.model.GameSettings
import com.example.castlerace.model.GridPosition
import com.example.castlerace.model.Player
import com.example.castlerace.ui.theme.CaptureWarning
import com.example.castlerace.ui.theme.CastleBorderHighlight
import com.example.castlerace.ui.theme.CastleIvory
import com.example.castlerace.ui.theme.CastlePlatform
import com.example.castlerace.ui.theme.DarkGridBorder
import com.example.castlerace.ui.theme.DarkStoneCellDark
import com.example.castlerace.ui.theme.DarkStoneCellLight
import com.example.castlerace.ui.theme.LastMoveDest
import com.example.castlerace.ui.theme.LastMoveOrigin
import com.example.castlerace.ui.theme.LocalBoardTheme
import com.example.castlerace.ui.theme.MidnightCellDark
import com.example.castlerace.ui.theme.MidnightCellLight
import com.example.castlerace.ui.theme.MidnightGridBorder
import com.example.castlerace.ui.theme.MoveHintDot
import com.example.castlerace.ui.theme.Player1Bronze
import com.example.castlerace.ui.theme.Player1BronzeDark
import com.example.castlerace.ui.theme.Player1BronzeLight
import com.example.castlerace.ui.theme.Player2Teal
import com.example.castlerace.ui.theme.Player2TealDark
import com.example.castlerace.ui.theme.Player2TealLight
import com.example.castlerace.ui.theme.SelectionGold
import com.example.castlerace.ui.viewmodel.GameViewModel

@Composable
fun BoardView(
    state: CastleRaceState,
    settings: GameSettings,
    activeAnimation: GameViewModel.AnimationEvent?,
    onCellClick: (GridPosition) -> Unit,
    modifier: Modifier = Modifier
) {
    val boardTheme = LocalBoardTheme.current
    val view = LocalView.current

    // Animated transition factor for active piece movement
    val moveProgress = remember { Animatable(0f) }
    LaunchedEffect(activeAnimation) {
        if (activeAnimation != null) {
            moveProgress.snapTo(0f)
            moveProgress.animateTo(1f, tween(180, easing = FastOutSlowInEasing))
        } else {
            moveProgress.snapTo(0f)
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(1f)
            .shadow(12.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .border(2.dp, CastleBorderHighlight.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
    ) {
        val totalWidth = maxWidth
        val cellSizeDp = totalWidth / 7

        // Canvas for board tiles, castles, highlights, and pieces
        Canvas(modifier = Modifier.fillMaxSize()) {
            val boardSize = size.width
            val cellSize = boardSize / CastleRaceGameEngine.GRID_SIZE

            val (cellLight, cellDark, gridBorder) = when (boardTheme) {
                BoardTheme.CLASSIC_STONE -> Triple(DarkStoneCellLight, DarkStoneCellDark, DarkGridBorder)
                BoardTheme.MIDNIGHT -> Triple(MidnightCellLight, MidnightCellDark, MidnightGridBorder)
            }

            // 1. Draw Board Grid Cells
            for (row in 0 until CastleRaceGameEngine.GRID_SIZE) {
                for (col in 0 until CastleRaceGameEngine.GRID_SIZE) {
                    val pos = GridPosition(row, col)
                    val isAlt = (row + col) % 2 == 1
                    val baseCellColor = if (isAlt) cellDark else cellLight

                    val cellTopLeft = Offset(col * cellSize, row * cellSize)
                    val cellRect = Size(cellSize, cellSize)

                    // Draw base tile
                    drawRect(
                        color = baseCellColor,
                        topLeft = cellTopLeft,
                        size = cellRect
                    )

                    // Subtle inner bevel / border
                    drawRect(
                        color = gridBorder.copy(alpha = 0.35f),
                        topLeft = cellTopLeft,
                        size = cellRect,
                        style = Stroke(width = 1f)
                    )

                    // 2. Castle Visual Rendering
                    if (pos == CastleRaceGameEngine.PLAYER_1_CASTLE) {
                        drawCastlePlatform(
                            topLeft = cellTopLeft,
                            cellSize = cellSize,
                            ownerColor = Player1Bronze,
                            ownerLabel = "P1 Castle",
                            isOpponentCastleForCurrent = state.currentPlayer == Player.PLAYER_TWO,
                            isHighlighted = state.legalMoves.contains(pos) && settings.showLegalMoves
                        )
                    } else if (pos == CastleRaceGameEngine.PLAYER_2_CASTLE) {
                        drawCastlePlatform(
                            topLeft = cellTopLeft,
                            cellSize = cellSize,
                            ownerColor = Player2Teal,
                            ownerLabel = "P2 Castle",
                            isOpponentCastleForCurrent = state.currentPlayer == Player.PLAYER_ONE,
                            isHighlighted = state.legalMoves.contains(pos) && settings.showLegalMoves
                        )
                    }

                    // 3. Last Move Highlight
                    if (settings.showLastMove && state.lastMove != null) {
                        val (lastFrom, lastTo) = state.lastMove
                        if (pos == lastFrom) {
                            drawRoundRect(
                                color = LastMoveOrigin,
                                topLeft = Offset(cellTopLeft.x + 4f, cellTopLeft.y + 4f),
                                size = Size(cellSize - 8f, cellSize - 8f),
                                cornerRadius = CornerRadius(8f, 8f)
                            )
                        } else if (pos == lastTo) {
                            drawRoundRect(
                                color = LastMoveDest,
                                topLeft = Offset(cellTopLeft.x + 4f, cellTopLeft.y + 4f),
                                size = Size(cellSize - 8f, cellSize - 8f),
                                cornerRadius = CornerRadius(8f, 8f),
                                style = Stroke(width = 2.dp.toPx())
                            )
                        }
                    }

                    // 4. Legal Move Hints
                    if (settings.showLegalMoves && state.legalMoves.contains(pos)) {
                        val center = Offset(cellTopLeft.x + cellSize / 2f, cellTopLeft.y + cellSize / 2f)
                        val pieceAtDest = state.pieceAt(pos)

                        if (pos == CastleRaceGameEngine.getOpponentCastle(state.currentPlayer)) {
                            // Opponent Castle highlight aura
                            drawCircle(
                                color = CastleIvory.copy(alpha = 0.7f),
                                radius = cellSize * 0.42f,
                                center = center,
                                style = Stroke(width = 3.dp.toPx())
                            )
                        } else if (pieceAtDest != null && pieceAtDest.player != state.currentPlayer) {
                            // Capture Warning Ring
                            drawCircle(
                                color = CaptureWarning,
                                radius = cellSize * 0.44f,
                                center = center,
                                style = Stroke(width = 3.dp.toPx())
                            )
                        } else {
                            // Normal move dot
                            drawCircle(
                                color = MoveHintDot,
                                radius = cellSize * 0.12f,
                                center = center
                            )
                        }
                    }
                }
            }

            // 5. Draw Pieces
            for (piece in state.pieces) {
                // If this piece is currently being animated
                val isMoving = activeAnimation?.movingPieceId == piece.id
                val isBeingCaptured = activeAnimation?.capturedPieceId == piece.id

                val anim = activeAnimation
                val pieceCenter = if (isMoving && anim != null) {
                    val fromX = (anim.origin.col + 0.5f) * cellSize
                    val fromY = (anim.origin.row + 0.5f) * cellSize
                    val toX = (anim.destination.col + 0.5f) * cellSize
                    val toY = (anim.destination.row + 0.5f) * cellSize
                    val curX = fromX + (toX - fromX) * moveProgress.value
                    val curY = fromY + (toY - fromY) * moveProgress.value
                    Offset(curX, curY)
                } else {
                    Offset(
                        (piece.position.col + 0.5f) * cellSize,
                        (piece.position.row + 0.5f) * cellSize
                    )
                }

                val isSelected = state.selectedPieceId == piece.id
                val captureAlpha = if (isBeingCaptured) (1f - moveProgress.value).coerceIn(0f, 1f) else 1f
                val captureScale = if (isBeingCaptured) (1f - moveProgress.value * 0.4f).coerceIn(0.5f, 1f) else 1f

                if (captureAlpha > 0f) {
                    drawPieceToken(
                        piece = piece,
                        center = pieceCenter,
                        cellSize = cellSize * captureScale,
                        isSelected = isSelected,
                        alpha = captureAlpha
                    )
                }
            }
        }

        // Invisible touch target overlay for accessible taps & semantics
        for (row in 0 until CastleRaceGameEngine.GRID_SIZE) {
            for (col in 0 until CastleRaceGameEngine.GRID_SIZE) {
                val pos = GridPosition(row, col)
                val piece = state.pieceAt(pos)
                val isCastle = pos == CastleRaceGameEngine.PLAYER_1_CASTLE || pos == CastleRaceGameEngine.PLAYER_2_CASTLE
                val isLegal = state.legalMoves.contains(pos)

                val cellDescription = buildString {
                    if (piece != null) {
                        append("${piece.player.displayName} piece row ${pos.row + 1} column ${pos.col + 1}. ")
                        if (state.selectedPieceId == piece.id) append("Selected. ")
                    }
                    if (isCastle) {
                        val owner = if (pos == CastleRaceGameEngine.PLAYER_1_CASTLE) "Player 1" else "Player 2"
                        append("$owner castle. ")
                    }
                    if (isLegal) {
                        if (piece != null && piece.player != state.currentPlayer) {
                            append("Legal capture! ")
                        } else if (pos == CastleRaceGameEngine.getOpponentCastle(state.currentPlayer)) {
                            append("Legal castle entry! ")
                        } else {
                            append("Legal move destination. ")
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .size(cellSizeDp)
                        .offsetCell(col, row, cellSizeDp)
                        .semantics { contentDescription = cellDescription }
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onCellClick(pos)
                        }
                )
            }
        }
    }
}

private fun Modifier.offsetCell(col: Int, row: Int, cellSizeDp: androidx.compose.ui.unit.Dp): Modifier {
    return this.offset(
        x = cellSizeDp * col,
        y = cellSizeDp * row
    )
}

private fun DrawScope.drawCastlePlatform(
    topLeft: Offset,
    cellSize: Float,
    ownerColor: Color,
    ownerLabel: String,
    isOpponentCastleForCurrent: Boolean,
    isHighlighted: Boolean
) {
    val padding = cellSize * 0.08f
    val platformRect = Size(cellSize - padding * 2, cellSize - padding * 2)
    val platformTopLeft = Offset(topLeft.x + padding, topLeft.y + padding)

    // Platform base
    drawRoundRect(
        color = CastlePlatform,
        topLeft = platformTopLeft,
        size = platformRect,
        cornerRadius = CornerRadius(8f, 8f)
    )

    // Platform highlight border
    val borderColor = if (isHighlighted) CastleIvory else ownerColor.copy(alpha = 0.7f)
    drawRoundRect(
        color = borderColor,
        topLeft = platformTopLeft,
        size = platformRect,
        cornerRadius = CornerRadius(8f, 8f),
        style = Stroke(width = if (isHighlighted) 3.dp.toPx() else 1.5.dp.toPx())
    )

    // Minimal Battlement Silhouette (3 crenels / notches)
    val crenelCount = 3
    val totalCrenelWidth = platformRect.width * 0.7f
    val crenelW = totalCrenelWidth / (crenelCount * 2 - 1)
    val crenelH = platformRect.height * 0.22f
    val startX = platformTopLeft.x + (platformRect.width - totalCrenelWidth) / 2f
    val startY = platformTopLeft.y + platformRect.height * 0.28f

    for (i in 0 until crenelCount) {
        val cx = startX + i * (crenelW * 2)
        drawRoundRect(
            color = ownerColor.copy(alpha = 0.85f),
            topLeft = Offset(cx, startY),
            size = Size(crenelW, crenelH),
            cornerRadius = CornerRadius(2f, 2f)
        )
    }

    // Fortress core arch/gateway
    val archW = platformRect.width * 0.28f
    val archH = platformRect.height * 0.30f
    val archX = platformTopLeft.x + (platformRect.width - archW) / 2f
    val archY = startY + crenelH + 2f
    drawRoundRect(
        color = Color(0xFF14171D),
        topLeft = Offset(archX, archY),
        size = Size(archW, archH),
        cornerRadius = CornerRadius(archW / 2f, archW / 2f)
    )
}

private fun DrawScope.drawPieceToken(
    piece: CastlePiece,
    center: Offset,
    cellSize: Float,
    isSelected: Boolean,
    alpha: Float
) {
    val radius = cellSize * 0.36f
    val shadowOffset = if (isSelected) Offset(0f, 6f) else Offset(0f, 3f)

    // Soft Shadow
    drawCircle(
        color = Color.Black.copy(alpha = 0.45f * alpha),
        radius = radius * (if (isSelected) 1.15f else 1.0f),
        center = center + shadowOffset
    )

    // Selection ring
    if (isSelected) {
        drawCircle(
            color = SelectionGold.copy(alpha = 0.9f * alpha),
            radius = radius * 1.25f,
            center = center,
            style = Stroke(width = 3.dp.toPx())
        )
    }

    when (piece.player) {
        Player.PLAYER_ONE -> {
            // Player 1: DIAMOND MOTIF (Warm bronze)
            val dSize = radius * (if (isSelected) 1.08f else 1.0f)
            val diamondPath = Path().apply {
                moveTo(center.x, center.y - dSize)
                lineTo(center.x + dSize, center.y)
                lineTo(center.x, center.y + dSize)
                lineTo(center.x - dSize, center.y)
                close()
            }

            // Outer bronze bevel
            drawPath(
                path = diamondPath,
                color = Player1BronzeDark.copy(alpha = alpha)
            )

            // Inner bronze gradient body
            val innerSize = dSize * 0.82f
            val innerDiamond = Path().apply {
                moveTo(center.x, center.y - innerSize)
                lineTo(center.x + innerSize, center.y)
                lineTo(center.x, center.y + innerSize)
                lineTo(center.x - innerSize, center.y)
                close()
            }
            drawPath(
                path = innerDiamond,
                brush = Brush.radialGradient(
                    colors = listOf(
                        Player1BronzeLight.copy(alpha = alpha),
                        Player1Bronze.copy(alpha = alpha)
                    ),
                    center = center - Offset(innerSize * 0.2f, innerSize * 0.2f),
                    radius = innerSize * 1.2f
                )
            )

            // Diamond Owner Center Core
            val coreSize = innerSize * 0.4f
            val corePath = Path().apply {
                moveTo(center.x, center.y - coreSize)
                lineTo(center.x + coreSize, center.y)
                lineTo(center.x, center.y + coreSize)
                lineTo(center.x - coreSize, center.y)
                close()
            }
            drawPath(
                path = corePath,
                color = CastleIvory.copy(alpha = 0.9f * alpha)
            )
        }

        Player.PLAYER_TWO -> {
            // Player 2: CIRCLE MOTIF (Deep teal)
            val cRadius = radius * (if (isSelected) 1.08f else 1.0f)

            // Outer dark teal bevel
            drawCircle(
                color = Player2TealDark.copy(alpha = alpha),
                radius = cRadius,
                center = center
            )

            // Inner teal body with subtle radial gradient
            val innerRadius = cRadius * 0.84f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Player2TealLight.copy(alpha = alpha),
                        Player2Teal.copy(alpha = alpha)
                    ),
                    center = center - Offset(innerRadius * 0.25f, innerRadius * 0.25f),
                    radius = innerRadius * 1.3f
                ),
                radius = innerRadius,
                center = center
            )

            // Owner Center Core (Ivory inner dot)
            drawCircle(
                color = CastleIvory.copy(alpha = 0.9f * alpha),
                radius = innerRadius * 0.38f,
                center = center
            )
        }
    }
}
