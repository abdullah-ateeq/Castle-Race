package com.example.castlerace.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.castlerace.model.BoardTheme
import com.example.castlerace.model.GameSettings
import com.example.castlerace.model.GameStats
import com.example.castlerace.model.Player
import com.example.castlerace.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "castle_race_prefs")

class PreferencesManager(private val context: Context) {

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val BOARD_THEME = stringPreferencesKey("board_theme")
        val SOUND_EFFECTS = booleanPreferencesKey("sound_effects")
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        val SHOW_LEGAL_MOVES = booleanPreferencesKey("show_legal_moves")
        val SHOW_LAST_MOVE = booleanPreferencesKey("show_last_move")

        val GAMES_PLAYED = intPreferencesKey("games_played")
        val PLAYER_1_WINS = intPreferencesKey("player_1_wins")
        val PLAYER_2_WINS = intPreferencesKey("player_2_wins")
        val FASTEST_CASTLE_VICTORY = intPreferencesKey("fastest_castle_victory")
        val MOST_CAPTURES = intPreferencesKey("most_captures")
    }

    val settingsFlow: Flow<GameSettings> = context.dataStore.data.map { preferences ->
        val themeModeStr = preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.DARK.name
        val boardThemeStr = preferences[PreferencesKeys.BOARD_THEME] ?: BoardTheme.CLASSIC_STONE.name
        val themeMode = runCatching { ThemeMode.valueOf(themeModeStr) }.getOrDefault(ThemeMode.DARK)
        val boardTheme = runCatching { BoardTheme.valueOf(boardThemeStr) }.getOrDefault(BoardTheme.CLASSIC_STONE)

        GameSettings(
            themeMode = themeMode,
            boardTheme = boardTheme,
            soundEffects = preferences[PreferencesKeys.SOUND_EFFECTS] ?: true,
            hapticFeedback = preferences[PreferencesKeys.HAPTIC_FEEDBACK] ?: true,
            showLegalMoves = preferences[PreferencesKeys.SHOW_LEGAL_MOVES] ?: true,
            showLastMove = preferences[PreferencesKeys.SHOW_LAST_MOVE] ?: true
        )
    }

    val statsFlow: Flow<GameStats> = context.dataStore.data.map { preferences ->
        GameStats(
            gamesPlayed = preferences[PreferencesKeys.GAMES_PLAYED] ?: 0,
            player1Wins = preferences[PreferencesKeys.PLAYER_1_WINS] ?: 0,
            player2Wins = preferences[PreferencesKeys.PLAYER_2_WINS] ?: 0,
            fastestCastleVictoryTurns = preferences[PreferencesKeys.FASTEST_CASTLE_VICTORY] ?: 0,
            mostCaptures = preferences[PreferencesKeys.MOST_CAPTURES] ?: 0
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode.name
        }
    }

    suspend fun setBoardTheme(theme: BoardTheme) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.BOARD_THEME] = theme.name
        }
    }

    suspend fun setSoundEffects(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SOUND_EFFECTS] = enabled
        }
    }

    suspend fun setHapticFeedback(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAPTIC_FEEDBACK] = enabled
        }
    }

    suspend fun setShowLegalMoves(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_LEGAL_MOVES] = enabled
        }
    }

    suspend fun setShowLastMove(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_LAST_MOVE] = enabled
        }
    }

    suspend fun recordGameResult(
        winner: Player,
        turns: Int,
        totalCapturesInGame: Int,
        isCastleVictory: Boolean
    ) {
        context.dataStore.edit { preferences ->
            val currentGames = preferences[PreferencesKeys.GAMES_PLAYED] ?: 0
            preferences[PreferencesKeys.GAMES_PLAYED] = currentGames + 1

            when (winner) {
                Player.PLAYER_ONE -> {
                    val p1Wins = preferences[PreferencesKeys.PLAYER_1_WINS] ?: 0
                    preferences[PreferencesKeys.PLAYER_1_WINS] = p1Wins + 1
                }
                Player.PLAYER_TWO -> {
                    val p2Wins = preferences[PreferencesKeys.PLAYER_2_WINS] ?: 0
                    preferences[PreferencesKeys.PLAYER_2_WINS] = p2Wins + 1
                }
            }

            if (isCastleVictory && turns > 0) {
                val currentFastest = preferences[PreferencesKeys.FASTEST_CASTLE_VICTORY] ?: 0
                if (currentFastest == 0 || turns < currentFastest) {
                    preferences[PreferencesKeys.FASTEST_CASTLE_VICTORY] = turns
                }
            }

            val currentMostCaptures = preferences[PreferencesKeys.MOST_CAPTURES] ?: 0
            if (totalCapturesInGame > currentMostCaptures) {
                preferences[PreferencesKeys.MOST_CAPTURES] = totalCapturesInGame
            }
        }
    }

    suspend fun resetStats() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.GAMES_PLAYED] = 0
            preferences[PreferencesKeys.PLAYER_1_WINS] = 0
            preferences[PreferencesKeys.PLAYER_2_WINS] = 0
            preferences[PreferencesKeys.FASTEST_CASTLE_VICTORY] = 0
            preferences[PreferencesKeys.MOST_CAPTURES] = 0
        }
    }
}
