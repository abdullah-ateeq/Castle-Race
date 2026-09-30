package com.example.castlerace.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.castlerace.model.Player
import com.example.castlerace.ui.theme.CastleBorderHighlight
import com.example.castlerace.ui.theme.CastleIvory
import com.example.castlerace.ui.theme.Player1Bronze
import com.example.castlerace.ui.theme.Player1BronzeLight
import com.example.castlerace.ui.theme.Player2Teal
import com.example.castlerace.ui.theme.Player2TealLight

@Composable
fun PlayerPanel(
    player: Player,
    isCurrentTurn: Boolean,
    castleEntries: Int,
    activePiecesCount: Int,
    capturesCount: Int,
    modifier: Modifier = Modifier
) {
    val isP1 = player == Player.PLAYER_ONE
    val themeAccent = if (isP1) Player1Bronze else Player2Teal
    val themeLightAccent = if (isP1) Player1BronzeLight else Player2TealLight
    val playerName = if (isP1) "PLAYER 1" else "PLAYER 2"

    val cardBorder = if (isCurrentTurn) {
        androidx.compose.foundation.BorderStroke(2.dp, themeAccent)
    } else {
        androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    }

    val panelDescription = "$playerName. Castle: $castleEntries of 3. Active: $activePiecesCount. Captures: $capturesCount. ${if (isCurrentTurn) "Current turn" else ""}"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = panelDescription },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentTurn) {
                themeAccent.copy(alpha = 0.12f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            }
        ),
        border = cardBorder
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Player Identity and Motif
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Owner Symbol
                if (isP1) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(Player1Bronze, shape = RoundedCornerShape(2.dp))
                            .border(1.dp, CastleIvory, RoundedCornerShape(2.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(Player2Teal, shape = CircleShape)
                            .border(1.dp, CastleIvory, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = playerName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isCurrentTurn) themeLightAccent else MaterialTheme.colorScheme.onSurface,
                            letterSpacing = 1.sp
                        )
                        if (isCurrentTurn) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(themeAccent.copy(alpha = 0.25f))
                                    .border(1.dp, themeAccent, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "TURN",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = themeLightAccent,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Castle entries visual slots
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Castle: $castleEntries/3",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        for (i in 1..3) {
                            val filled = i <= castleEntries
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(if (filled) themeAccent else Color.Transparent)
                                    .border(1.dp, if (filled) themeLightAccent else CastleBorderHighlight.copy(alpha = 0.5f), CircleShape)
                            )
                            if (i < 3) Spacer(modifier = Modifier.width(3.dp))
                        }
                    }
                }
            }

            // Quick Stats: Active & Captures
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$activePiecesCount",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Active",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$capturesCount",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CastleIvory
                    )
                    Text(
                        text = "Captures",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
