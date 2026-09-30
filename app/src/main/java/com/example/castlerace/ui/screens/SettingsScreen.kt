package com.example.castlerace.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.castlerace.model.BoardTheme
import com.example.castlerace.model.ThemeMode
import com.example.castlerace.ui.theme.CastleIvory
import com.example.castlerace.ui.theme.Player1Bronze
import com.example.castlerace.ui.theme.Player1BronzeLight
import com.example.castlerace.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBackClick: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    val stats by viewModel.stats.collectAsState()

    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SETTINGS",
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
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Section: App Theme
                SettingsCard(title = "App Theme") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeMode.entries.forEach { mode ->
                            val selected = settings.themeMode == mode
                            FilterChip(
                                selected = selected,
                                onClick = { viewModel.setThemeMode(mode) },
                                label = { Text(mode.title) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Player1Bronze,
                                    selectedLabelColor = Color.Black
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Section: Board Theme
                SettingsCard(title = "Board Theme") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BoardTheme.entries.forEach { board ->
                            val selected = settings.boardTheme == board
                            FilterChip(
                                selected = selected,
                                onClick = { viewModel.setBoardTheme(board) },
                                label = { Text(board.title) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Player1Bronze,
                                    selectedLabelColor = Color.Black
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Section: Audio & Feedback
                SettingsCard(title = "Audio & Haptics") {
                    SettingsToggle(
                        title = "Sound Effects",
                        subtitle = "Tactile clicks, captures, and victory chimes",
                        checked = settings.soundEffects,
                        onCheckedChange = { viewModel.setSoundEffects(it) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SettingsToggle(
                        title = "Haptic Feedback",
                        subtitle = "Vibrations on piece moves and game events",
                        checked = settings.hapticFeedback,
                        onCheckedChange = { viewModel.setHapticFeedback(it) }
                    )
                }

                // Section: Gameplay Aids
                SettingsCard(title = "Gameplay Aids") {
                    SettingsToggle(
                        title = "Show Legal Moves",
                        subtitle = "Highlight valid destinations and captures on piece tap",
                        checked = settings.showLegalMoves,
                        onCheckedChange = { viewModel.setShowLegalMoves(it) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SettingsToggle(
                        title = "Show Last Move",
                        subtitle = "Highlight origin and destination of previous move",
                        checked = settings.showLastMove,
                        onCheckedChange = { viewModel.setShowLastMove(it) }
                    )
                }

                // Section: Statistics & Reset
                SettingsCard(title = "Career Statistics") {
                    StatRowSimple("Games Played", "${stats.gamesPlayed}")
                    StatRowSimple("Player 1 Wins", "${stats.player1Wins}")
                    StatRowSimple("Player 2 Wins", "${stats.player2Wins}")
                    StatRowSimple(
                        "Fastest Castle Victory",
                        if (stats.fastestCastleVictoryTurns > 0) "${stats.fastestCastleVictoryTurns} turns" else "None"
                    )
                    StatRowSimple("Most Captures in Match", "${stats.mostCaptures}")

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = { showResetDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Reset Statistics")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    text = "Reset Career Statistics?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "This will permanently clear all recorded match counts, wins, and records.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetDialog = false
                        viewModel.resetStats()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = Color.White
                    )
                ) {
                    Text("Reset", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingsCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Player1BronzeLight,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun SettingsToggle(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = CastleIvory
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = Player1Bronze
            )
        )
    }
}

@Composable
private fun StatRowSimple(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = CastleIvory
        )
    }
}
