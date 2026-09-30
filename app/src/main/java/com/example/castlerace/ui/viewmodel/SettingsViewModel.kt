package com.example.castlerace.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.castlerace.data.PreferencesManager
import com.example.castlerace.model.BoardTheme
import com.example.castlerace.model.GameSettings
import com.example.castlerace.model.GameStats
import com.example.castlerace.model.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application)

    val settings: StateFlow<GameSettings> = preferencesManager.settingsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, GameSettings())

    val stats: StateFlow<GameStats> = preferencesManager.statsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, GameStats())

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            preferencesManager.setThemeMode(mode)
        }
    }

    fun setBoardTheme(theme: BoardTheme) {
        viewModelScope.launch {
            preferencesManager.setBoardTheme(theme)
        }
    }

    fun setSoundEffects(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setSoundEffects(enabled)
        }
    }

    fun setHapticFeedback(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setHapticFeedback(enabled)
        }
    }

    fun setShowLegalMoves(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setShowLegalMoves(enabled)
        }
    }

    fun setShowLastMove(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setShowLastMove(enabled)
        }
    }

    fun resetStats() {
        viewModelScope.launch {
            preferencesManager.resetStats()
        }
    }
}
