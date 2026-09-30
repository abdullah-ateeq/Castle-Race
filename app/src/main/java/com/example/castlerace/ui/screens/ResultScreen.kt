package com.example.castlerace.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.castlerace.model.CastleRaceState
import com.example.castlerace.model.Player
import com.example.castlerace.model.WinReason
import com.example.castlerace.ui.theme.CastleBorderHighlight
import com.example.castlerace.ui.theme.CastleIvory
import com.example.castlerace.ui.theme.Player1Bronze
import com.example.castlerace.ui.theme.Player1BronzeLight
import com.example.castlerace.ui.theme.Player2Teal
import com.example.castlerace.ui.theme.Player2TealLight
import java.util.Locale

@Composable
fun ResultScreen(
    state: CastleRaceState,
    onPlayAgainClick: () -> Unit,
    onHomeClick: () -> Unit
) {
    val winner = state.winner ?: Player.PLAYER_ONE
    val winReason = state.winReason ?: WinReason.CASTLE_ENTRIES
    val isP1Winner = winner == Player.PLAYER_ONE
    val winnerAccent = if (isP1Winner) Player1Bronze else Player2Teal
    val winnerLightAccent = if (isP1Winner) Player1BronzeLight else Player2TealLight

    val minutes = state.matchDurationSeconds / 60
    val seconds = state.matchDurationSeconds % 60
    val durationText = String.format(Locale.US, "%02d:%02d", minutes, seconds)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .widthIn(max = 500.dp)
        ) {
            // Victory Emblem
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(winnerAccent.copy(alpha = 0.15f))
                    .border(2.dp, winnerAccent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isP1Winner) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Player1Bronze, shape = RoundedCornerShape(4.dp))
                            .border(1.5.dp, CastleIvory, RoundedCornerShape(4.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Player2Teal, shape = CircleShape)
                            .border(1.5.dp, CastleIvory, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Winner Announcement
            Text(
                text = "${winner.displayName.uppercase()} WINS",
                style = MaterialTheme.typography.headlineLarge,
                color = winnerLightAccent,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Win Reason Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(winnerAccent.copy(alpha = 0.2f))
                    .border(1.dp, winnerAccent.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = winReason.displayTitle,
                    style = MaterialTheme.typography.labelLarge,
                    color = CastleIvory,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = winReason.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Match Summary Statistics Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "MATCH SUMMARY",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.2.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    StatRow(
                        title = "Castle Entries",
                        p1Value = "${state.player1CastleEntries} / 3",
                        p2Value = "${state.player2CastleEntries} / 3"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    StatRow(
                        title = "Captures Made",
                        p1Value = "${state.player1Captures}",
                        p2Value = "${state.player2Captures}"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    StatRow(
                        title = "Pieces Remaining",
                        p1Value = "${state.activePieces(Player.PLAYER_ONE).size}",
                        p2Value = "${state.activePieces(Player.PLAYER_TWO).size}"
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Turns Played",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${state.moveCount}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = CastleIvory
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Match Duration",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = durationText,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = CastleIvory
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Primary PLAY AGAIN Button
            Button(
                onClick = onPlayAgainClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = winnerAccent,
                    contentColor = Color.Black
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Replay,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "PLAY AGAIN",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Secondary HOME Button
            OutlinedButton(
                onClick = onHomeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "HOME",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp
                )
            }
        }
    }
}

@Composable
private fun StatRow(
    title: String,
    p1Value: String,
    p2Value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "P1: $p1Value",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Player1BronzeLight
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = "P2: $p2Value",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Player2TealLight
            )
        }
    }
}
