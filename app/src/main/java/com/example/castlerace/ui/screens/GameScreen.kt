package com.example.castlerace.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.castlerace.model.Player
import com.example.castlerace.ui.components.BoardView
import com.example.castlerace.ui.components.PlayerPanel
import com.example.castlerace.ui.theme.CastleIvory
import com.example.castlerace.ui.theme.Player1Bronze
import com.example.castlerace.ui.theme.Player1BronzeLight
import com.example.castlerace.ui.theme.Player2Teal
import com.example.castlerace.ui.theme.Player2TealLight
import com.example.castlerace.ui.viewmodel.GameViewModel
import kotlinx.coroutines.delay

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onNavigateToResult: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val activeAnimation by viewModel.activeAnimation.collectAsState()
    val view = LocalView.current

    var showRestartDialog by remember { mutableStateOf(false) }

    // When game finishes, transition to Result screen after a brief delay so player sees final move
    LaunchedEffect(state.winner) {
        if (state.winner != null) {
            delay(1200)
            onNavigateToResult()
        }
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (isLandscape) {
            // Landscape / Tablet Side-by-Side Layout
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Board on Left
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    BoardView(
                        state = state,
                        settings = settings,
                        activeAnimation = activeAnimation,
                        onCellClick = { pos -> viewModel.onCellClicked(pos, view) },
                        modifier = Modifier.fillMaxHeight(0.95f)
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                // Status & Controls on Right
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PlayerPanel(
                        player = Player.PLAYER_TWO,
                        isCurrentTurn = state.currentPlayer == Player.PLAYER_TWO,
                        castleEntries = state.player2CastleEntries,
                        activePiecesCount = state.activePieces(Player.PLAYER_TWO).size,
                        capturesCount = state.player2Captures
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    TurnIndicatorBanner(currentPlayer = state.currentPlayer)

                    Spacer(modifier = Modifier.height(10.dp))

                    PlayerPanel(
                        player = Player.PLAYER_ONE,
                        isCurrentTurn = state.currentPlayer == Player.PLAYER_ONE,
                        castleEntries = state.player1CastleEntries,
                        activePiecesCount = state.activePieces(Player.PLAYER_ONE).size,
                        capturesCount = state.player1Captures
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    GameControlButtons(
                        canUndo = viewModel.canUndo,
                        onUndo = { viewModel.undo(view) },
                        onRestart = { showRestartDialog = true },
                        onHome = onNavigateToHome
                    )
                }
            }
        } else {
            // Portrait Vertical Layout
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top: Player 2 Panel
                PlayerPanel(
                    player = Player.PLAYER_TWO,
                    isCurrentTurn = state.currentPlayer == Player.PLAYER_TWO,
                    castleEntries = state.player2CastleEntries,
                    activePiecesCount = state.activePieces(Player.PLAYER_TWO).size,
                    capturesCount = state.player2Captures
                )

                // Turn Indicator
                TurnIndicatorBanner(currentPlayer = state.currentPlayer)

                // Center: 7x7 Board
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    contentAlignment = Alignment.Center
                ) {
                    BoardView(
                        state = state,
                        settings = settings,
                        activeAnimation = activeAnimation,
                        onCellClick = { pos -> viewModel.onCellClicked(pos, view) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 440.dp)
                    )
                }

                // Bottom: Player 1 Panel
                PlayerPanel(
                    player = Player.PLAYER_ONE,
                    isCurrentTurn = state.currentPlayer == Player.PLAYER_ONE,
                    castleEntries = state.player1CastleEntries,
                    activePiecesCount = state.activePieces(Player.PLAYER_ONE).size,
                    capturesCount = state.player1Captures
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Bottom Controls: Undo, Restart, Home
                GameControlButtons(
                    canUndo = viewModel.canUndo,
                    onUndo = { viewModel.undo(view) },
                    onRestart = { showRestartDialog = true },
                    onHome = onNavigateToHome
                )
            }
        }

        // Castle Reached Transient Banner
        AnimatedVisibility(
            visible = state.castleReachedAnnouncement != null,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            val scoringPlayer = state.castleReachedAnnouncement
            val accentColor = if (scoringPlayer == Player.PLAYER_ONE) Player1Bronze else Player2Teal
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xE614171E))
                    .border(2.dp, accentColor, RoundedCornerShape(16.dp))
                    .padding(horizontal = 24.dp, vertical = 14.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "CASTLE REACHED!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = CastleIvory,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${scoringPlayer?.displayName ?: ""} scores +1 castle entry",
                        style = MaterialTheme.typography.bodySmall,
                        color = accentColor
                    )
                }
            }
        }
    }

    // Restart Confirmation Dialog
    if (showRestartDialog) {
        AlertDialog(
            onDismissRequest = { showRestartDialog = false },
            title = {
                Text(
                    text = "Restart this race?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "The current board position and scores will be reset to the beginning.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRestartDialog = false
                        viewModel.restartGame()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Player1Bronze,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Restart", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestartDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun TurnIndicatorBanner(currentPlayer: Player) {
    val isP1 = currentPlayer == Player.PLAYER_ONE
    val turnText = if (isP1) "PLAYER 1'S TURN" else "PLAYER 2'S TURN"
    val accentColor = if (isP1) Player1BronzeLight else Player2TealLight
    val subtitleText = if (isP1) "Advance toward Top Castle (Row 0)" else "Advance toward Bottom Castle (Row 6)"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = turnText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = accentColor,
            letterSpacing = 1.2.sp
        )
        Text(
            text = subtitleText,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun GameControlButtons(
    canUndo: Boolean,
    onUndo: () -> Unit,
    onRestart: () -> Unit,
    onHome: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Undo
        FilledTonalButton(
            onClick = onUndo,
            enabled = canUndo,
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Undo,
                contentDescription = "Undo previous move",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Undo", style = MaterialTheme.typography.labelLarge)
        }

        // Restart
        OutlinedButton(
            onClick = onRestart,
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Restart game",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Restart", style = MaterialTheme.typography.labelLarge)
        }

        // Home
        OutlinedButton(
            onClick = onHome,
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Return to Home",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Home", style = MaterialTheme.typography.labelLarge)
        }
    }
}
