package com.example.castlerace.ui.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.castlerace.audio.HapticController
import com.example.castlerace.audio.SoundController
import com.example.castlerace.data.PreferencesManager
import com.example.castlerace.logic.CastleRaceGameEngine
import com.example.castlerace.model.CastleRaceState
import com.example.castlerace.model.GameSettings
import com.example.castlerace.model.GameStats
import com.example.castlerace.model.GridPosition
import com.example.castlerace.model.Player
import com.example.castlerace.model.WinReason
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application)
    private val soundController = SoundController()
    private val hapticController = HapticController(application)

    val settings: StateFlow<GameSettings> = preferencesManager.settingsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, GameSettings())

    val stats: StateFlow<GameStats> = preferencesManager.statsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, GameStats())

    private val _state = MutableStateFlow(CastleRaceGameEngine.initialGameState())
    val state: StateFlow<CastleRaceState> = _state.asStateFlow()

    private val undoStack = mutableListOf<CastleRaceState>()
    private var timerJob: Job? = null

    // For smooth UI animation of moving and captured pieces
    data class AnimationEvent(
        val movingPieceId: Int,
        val origin: GridPosition,
        val destination: GridPosition,
        val capturedPieceId: Int?,
        val isCastleEntry: Boolean
    )

    private val _activeAnimation = MutableStateFlow<AnimationEvent?>(null)
    val activeAnimation: StateFlow<AnimationEvent?> = _activeAnimation.asStateFlow()

    init {
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (!_state.value.isGameOver) {
                    _state.value = _state.value.copy(
                        matchDurationSeconds = _state.value.matchDurationSeconds + 1
                    )
                }
            }
        }
    }

    val canUndo: Boolean
        get() = undoStack.isNotEmpty() && !_state.value.isAnimating

    fun selectPiece(pieceId: Int, view: View? = null) {
        val current = _state.value
        if (current.isGameOver || current.isAnimating) return

        val piece = current.pieces.find { it.id == pieceId } ?: return

        // Cannot select opponent piece
        if (piece.player != current.currentPlayer) return

        // Tap again to deselect
        if (current.selectedPieceId == pieceId) {
            _state.value = current.copy(
                selectedPieceId = null,
                legalMoves = emptySet()
            )
            return
        }

        // Select and compute legal destinations
        val legalMoves = CastleRaceGameEngine.getLegalMoves(piece, current.pieces)
        _state.value = current.copy(
            selectedPieceId = pieceId,
            legalMoves = legalMoves
        )

        soundController.playSelect(settings.value.soundEffects)
        hapticController.vibrateSelect(view, settings.value.hapticFeedback)
    }

    fun deselectPiece() {
        val current = _state.value
        if (current.isGameOver || current.isAnimating) return
        if (current.selectedPieceId != null) {
            _state.value = current.copy(
                selectedPieceId = null,
                legalMoves = emptySet()
            )
        }
    }

    fun movePiece(pieceId: Int, destination: GridPosition, view: View? = null) {
        val current = _state.value
        if (current.isGameOver || current.isAnimating) return

        val piece = current.pieces.find { it.id == pieceId } ?: return
        if (piece.player != current.currentPlayer) return

        val legalMoves = CastleRaceGameEngine.getLegalMoves(piece, current.pieces)
        if (!legalMoves.contains(destination)) return

        // Save Undo snapshot before the move executes
        undoStack.add(current.copy(selectedPieceId = null, legalMoves = emptySet()))

        // Lock input and run resolution
        val resolution = CastleRaceGameEngine.executeMove(current, pieceId, destination) ?: return

        // Trigger appropriate sound/haptic based on move type
        val currentSettings = settings.value
        if (resolution.isCastleEntry) {
            soundController.playCastleEntry(currentSettings.soundEffects)
            hapticController.vibrateCastleEntry(view, currentSettings.hapticFeedback)
        } else if (resolution.capturedPiece != null) {
            soundController.playCapture(currentSettings.soundEffects)
            hapticController.vibrateCapture(view, currentSettings.hapticFeedback)
        } else {
            soundController.playMove(currentSettings.soundEffects)
            hapticController.vibrateMove(view, currentSettings.hapticFeedback)
        }

        viewModelScope.launch {
            // Set animation state
            _activeAnimation.value = AnimationEvent(
                movingPieceId = pieceId,
                origin = resolution.origin,
                destination = resolution.destination,
                capturedPieceId = resolution.capturedPiece?.id,
                isCastleEntry = resolution.isCastleEntry
            )
            _state.value = current.copy(
                isAnimating = true,
                selectedPieceId = null,
                legalMoves = emptySet()
            )

            // Animation duration 180ms
            delay(190)

            // Update to final resolved state
            _activeAnimation.value = null
            _state.value = resolution.nextState

            // If castle entry occurred, show announcement briefly
            if (resolution.isCastleEntry) {
                delay(700)
                _state.value = _state.value.copy(castleReachedAnnouncement = null)
            }

            // Check if game ended
            if (resolution.nextState.isGameOver) {
                val winner = resolution.nextState.winner ?: return@launch
                val isCastleWin = resolution.nextState.winReason == WinReason.CASTLE_ENTRIES
                val totalCaptures = resolution.nextState.player1Captures + resolution.nextState.player2Captures

                soundController.playWin(currentSettings.soundEffects)
                hapticController.vibrateWin(view, currentSettings.hapticFeedback)

                preferencesManager.recordGameResult(
                    winner = winner,
                    turns = resolution.nextState.moveCount,
                    totalCapturesInGame = totalCaptures,
                    isCastleVictory = isCastleWin
                )
            }
        }
    }

    fun onCellClicked(position: GridPosition, view: View? = null) {
        val current = _state.value
        if (current.isGameOver || current.isAnimating) return

        val selectedPiece = current.selectedPiece()

        // 1. If destination is a legal move for selected piece -> move
        if (selectedPiece != null && current.legalMoves.contains(position)) {
            movePiece(selectedPiece.id, position, view)
            return
        }

        // 2. If clicked on a friendly piece -> select/deselect it
        val clickedPiece = current.pieceAt(position)
        if (clickedPiece != null && clickedPiece.player == current.currentPlayer) {
            selectPiece(clickedPiece.id, view)
            return
        }

        // 3. Otherwise deselect
        if (selectedPiece != null) {
            deselectPiece()
        }
    }

    fun undo(view: View? = null) {
        if (!canUndo) return
        val previousState = undoStack.removeAt(undoStack.lastIndex)
        _state.value = previousState.copy(
            isAnimating = false,
            castleReachedAnnouncement = null
        )
        soundController.playMove(settings.value.soundEffects)
        hapticController.vibrateMove(view, settings.value.hapticFeedback)
    }

    fun restartGame() {
        undoStack.clear()
        _activeAnimation.value = null
        _state.value = CastleRaceGameEngine.initialGameState()
        startTimer()
    }

    fun startNewGame() {
        restartGame()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
