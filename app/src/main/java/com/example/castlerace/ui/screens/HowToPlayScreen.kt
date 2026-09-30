package com.example.castlerace.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.castlerace.ui.theme.CaptureWarning
import com.example.castlerace.ui.theme.CastleBorderHighlight
import com.example.castlerace.ui.theme.CastleIvory
import com.example.castlerace.ui.theme.DarkStoneCellDark
import com.example.castlerace.ui.theme.DarkStoneCellLight
import com.example.castlerace.ui.theme.Player1Bronze
import com.example.castlerace.ui.theme.Player1BronzeLight
import com.example.castlerace.ui.theme.Player2Teal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HowToPlayScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "HOW TO PLAY",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = CastleIvory,
                    navigationIconContentColor = CastleIvory
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.widthIn(max = 540.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Movement Diagram Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "DIRECTIONAL MOVEMENT (PLAYER 1 EXAMPLE)",
                            style = MaterialTheme.typography.labelMedium,
                            color = Player1BronzeLight,
                            letterSpacing = 0.8.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // 3x3 Mini Grid Visualizing Legal Moves for P1 at center
                        Box(
                            modifier = Modifier
                                .size(180.dp)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, CastleBorderHighlight.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val gridSize = 3
                                val cellSize = size.width / gridSize

                                for (r in 0 until gridSize) {
                                    for (c in 0 until gridSize) {
                                        val isCenter = r == 1 && c == 1
                                        val isLegalP1 = when {
                                            r == 0 && c == 1 -> true // Up
                                            r == 0 && c == 0 -> true // Up-Left
                                            r == 0 && c == 2 -> true // Up-Right
                                            r == 1 && c == 0 -> true // Left
                                            r == 1 && c == 2 -> true // Right
                                            else -> false // Down, Down-Left, Down-Right are forbidden
                                        }

                                        val cellColor = if ((r + c) % 2 == 1) DarkStoneCellDark else DarkStoneCellLight
                                        drawRect(
                                            color = cellColor,
                                            topLeft = Offset(c * cellSize, r * cellSize),
                                            size = Size(cellSize, cellSize)
                                        )

                                        val center = Offset((c + 0.5f) * cellSize, (r + 0.5f) * cellSize)

                                        if (isCenter) {
                                            // P1 Diamond Piece
                                            val dSize = cellSize * 0.3f
                                            val diamond = Path().apply {
                                                moveTo(center.x, center.y - dSize)
                                                lineTo(center.x + dSize, center.y)
                                                lineTo(center.x, center.y + dSize)
                                                lineTo(center.x - dSize, center.y)
                                                close()
                                            }
                                            drawPath(diamond, color = Player1Bronze)
                                        } else if (isLegalP1) {
                                            // Green / Gold check dot
                                            drawCircle(
                                                color = Player1BronzeLight,
                                                radius = cellSize * 0.16f,
                                                center = center
                                            )
                                        } else {
                                            // Red forbidden X
                                            val crossSize = cellSize * 0.14f
                                            drawLine(
                                                color = CaptureWarning.copy(alpha = 0.6f),
                                                start = center - Offset(crossSize, crossSize),
                                                end = center + Offset(crossSize, crossSize),
                                                strokeWidth = 2.dp.toPx()
                                            )
                                            drawLine(
                                                color = CaptureWarning.copy(alpha = 0.6f),
                                                start = center - Offset(-crossSize, crossSize),
                                                end = center + Offset(-crossSize, crossSize),
                                                strokeWidth = 2.dp.toPx()
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Player1BronzeLight))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Legal: Forward, Diagonals & Sideways", style = MaterialTheme.typography.labelSmall, color = CastleIvory)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("✕ Backward Forbidden", style = MaterialTheme.typography.labelSmall, color = CaptureWarning)
                        }
                    }
                }

                // Rule Section 1: Objective & Setup
                RuleCard(
                    title = "1. Setup & Pieces",
                    description = "• The board is a compact 7 × 7 stone tactical grid.\n• Each player commands 4 pieces.\n• Player 1 (Warm Bronze Diamonds) starts near the bottom.\n• Player 2 (Deep Teal Circles) starts near the top.\n• Both players take turns moving one piece per turn."
                )

                // Rule Section 2: Movement Rules
                RuleCard(
                    title = "2. Directional Movement",
                    description = "• Player 1 moves generally UPWARD: Up, Left, Right, Up-Left, and Up-Right.\n• Player 2 moves generally DOWNWARD: Down, Left, Right, Down-Left, and Down-Right.\n• NO BACKWARD MOVEMENT: Pieces can never step backward or diagonally backward.\n• You cannot move onto a friendly piece."
                )

                // Rule Section 3: Captures
                RuleCard(
                    title = "3. Captures",
                    description = "• Land directly on an opponent's piece to capture it.\n• The captured rival piece is immediately eliminated from the board.\n• Captured pieces do not respawn."
                )

                // Rule Section 4: Castle Infiltration
                RuleCard(
                    title = "4. Castle Entry",
                    description = "• Reach the opponent's castle cell (Row 0 for P1, Row 6 for P2) with a legal move.\n• Entering scores +1 Castle Entry.\n• The scoring piece has completed its journey and is removed from active play.\n• You cannot move backward into your own castle."
                )

                // Rule Section 5: Victory Conditions
                RuleCard(
                    title = "5. Win Conditions & Edge Cases",
                    description = "• Castle Infiltration: First player to score 3 Castle Entries wins immediately!\n• Elimination: If an opponent's remaining pieces + castle entries drops below 3, they can no longer win. You win by Elimination.\n• Blockade: If on a player's turn none of their active pieces have any legal moves, the opponent wins by Blockade."
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun RuleCard(
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = CastleIvory
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )
        }
    }
}
