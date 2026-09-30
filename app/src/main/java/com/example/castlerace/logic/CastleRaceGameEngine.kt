package com.example.castlerace.logic

import com.example.castlerace.model.CastlePiece
import com.example.castlerace.model.CastleRaceState
import com.example.castlerace.model.GridPosition
import com.example.castlerace.model.Player
import com.example.castlerace.model.WinReason

object CastleRaceGameEngine {
    const val GRID_SIZE = 7
    const val TARGET_CASTLE_ENTRIES = 3

    val PLAYER_1_CASTLE = GridPosition(6, 3)
    val PLAYER_2_CASTLE = GridPosition(0, 3)

    // Player 1 moves generally upward (-row) or sideways
    private val PLAYER_1_DIRECTIONS = listOf(
        Pair(-1, 0),  // Up
        Pair(0, -1),  // Left
        Pair(0, 1),   // Right
        Pair(-1, -1), // Up-Left (Forward Diagonal)
        Pair(-1, 1)   // Up-Right (Forward Diagonal)
    )

    // Player 2 moves generally downward (+row) or sideways
    private val PLAYER_2_DIRECTIONS = listOf(
        Pair(1, 0),   // Down
        Pair(0, -1),  // Left
        Pair(0, 1),   // Right
        Pair(1, -1),  // Down-Left (Forward Diagonal)
        Pair(1, 1)    // Down-Right (Forward Diagonal)
    )

    fun initialPieces(): List<CastlePiece> {
        return listOf(
            // Player 1 (Warm bronze)
            CastlePiece(id = 1, player = Player.PLAYER_ONE, position = GridPosition(5, 1)),
            CastlePiece(id = 2, player = Player.PLAYER_ONE, position = GridPosition(5, 2)),
            CastlePiece(id = 3, player = Player.PLAYER_ONE, position = GridPosition(5, 4)),
            CastlePiece(id = 4, player = Player.PLAYER_ONE, position = GridPosition(5, 5)),

            // Player 2 (Deep teal)
            CastlePiece(id = 5, player = Player.PLAYER_TWO, position = GridPosition(1, 1)),
            CastlePiece(id = 6, player = Player.PLAYER_TWO, position = GridPosition(1, 2)),
            CastlePiece(id = 7, player = Player.PLAYER_TWO, position = GridPosition(1, 4)),
            CastlePiece(id = 8, player = Player.PLAYER_TWO, position = GridPosition(1, 5))
        )
    }

    fun initialGameState(): CastleRaceState {
        return CastleRaceState(
            pieces = initialPieces(),
            currentPlayer = Player.PLAYER_ONE,
            selectedPieceId = null,
            legalMoves = emptySet(),
            player1CastleEntries = 0,
            player2CastleEntries = 0,
            player1Captures = 0,
            player2Captures = 0,
            lastMove = null,
            winner = null,
            winReason = null,
            moveCount = 0,
            isAnimating = false,
            matchDurationSeconds = 0L,
            castleReachedAnnouncement = null
        )
    }

    fun getOwnCastle(player: Player): GridPosition {
        return when (player) {
            Player.PLAYER_ONE -> PLAYER_1_CASTLE
            Player.PLAYER_TWO -> PLAYER_2_CASTLE
        }
    }

    fun getOpponentCastle(player: Player): GridPosition {
        return when (player) {
            Player.PLAYER_ONE -> PLAYER_2_CASTLE
            Player.PLAYER_TWO -> PLAYER_1_CASTLE
        }
    }

    fun isOwnCastle(player: Player, destination: GridPosition): Boolean {
        return destination == getOwnCastle(player)
    }

    fun isOpponentCastle(player: Player, destination: GridPosition): Boolean {
        return destination == getOpponentCastle(player)
    }

    fun getDirectionsForPlayer(player: Player): List<Pair<Int, Int>> {
        return when (player) {
            Player.PLAYER_ONE -> PLAYER_1_DIRECTIONS
            Player.PLAYER_TWO -> PLAYER_2_DIRECTIONS
        }
    }

    fun getLegalMoves(piece: CastlePiece, pieces: List<CastlePiece>): Set<GridPosition> {
        val directions = getDirectionsForPlayer(piece.player)
        val ownCastle = getOwnCastle(piece.player)
        val legalMoves = mutableSetOf<GridPosition>()

        for ((dRow, dCol) in directions) {
            val destination = piece.position.offset(dRow, dCol)

            // 1. Verify inside board
            if (!destination.isValid(GRID_SIZE)) {
                continue
            }

            // 2. Reject own castle
            if (destination == ownCastle) {
                continue
            }

            // 3. Check what's at destination
            val pieceAtDest = pieces.find { it.position == destination }
            if (pieceAtDest != null) {
                if (pieceAtDest.player == piece.player) {
                    // Cannot move onto friendly piece
                    continue
                } else {
                    // Opponent piece: Legal capture
                    legalMoves.add(destination)
                }
            } else {
                // Empty destination (including opponent castle cell)
                legalMoves.add(destination)
            }
        }

        return legalMoves
    }

    fun hasAnyLegalMove(player: Player, pieces: List<CastlePiece>): Boolean {
        return pieces
            .filter { it.player == player }
            .any { piece -> getLegalMoves(piece, pieces).isNotEmpty() }
    }

    data class MoveResolution(
        val nextState: CastleRaceState,
        val movedPiece: CastlePiece,
        val origin: GridPosition,
        val destination: GridPosition,
        val capturedPiece: CastlePiece?,
        val isCastleEntry: Boolean
    )

    fun executeMove(
        currentState: CastleRaceState,
        pieceId: Int,
        destination: GridPosition
    ): MoveResolution? {
        if (currentState.isGameOver || currentState.isAnimating) {
            return null
        }

        val movingPiece = currentState.pieces.find { it.id == pieceId } ?: return null
        if (movingPiece.player != currentState.currentPlayer) {
            return null
        }

        val legalMoves = getLegalMoves(movingPiece, currentState.pieces)
        if (!legalMoves.contains(destination)) {
            return null
        }

        val origin = movingPiece.position
        val capturedPiece = currentState.pieces.find {
            it.position == destination && it.player == currentState.currentPlayer.opponent()
        }
        val isCastleEntry = isOpponentCastle(currentState.currentPlayer, destination)

        // Calculate new captures
        val newP1Captures = if (currentState.currentPlayer == Player.PLAYER_ONE && capturedPiece != null) {
            currentState.player1Captures + 1
        } else {
            currentState.player1Captures
        }

        val newP2Captures = if (currentState.currentPlayer == Player.PLAYER_TWO && capturedPiece != null) {
            currentState.player2Captures + 1
        } else {
            currentState.player2Captures
        }

        // Calculate new castle entries
        val newP1Entries = if (currentState.currentPlayer == Player.PLAYER_ONE && isCastleEntry) {
            currentState.player1CastleEntries + 1
        } else {
            currentState.player1CastleEntries
        }

        val newP2Entries = if (currentState.currentPlayer == Player.PLAYER_TWO && isCastleEntry) {
            currentState.player2CastleEntries + 1
        } else {
            currentState.player2CastleEntries
        }

        // Update pieces list
        val updatedPieces = currentState.pieces
            .filter { it.id != capturedPiece?.id } // remove captured piece
            .filter { !(isCastleEntry && it.id == movingPiece.id) } // remove moving piece if castle entry
            .map { piece ->
                if (piece.id == movingPiece.id && !isCastleEntry) {
                    piece.copy(position = destination)
                } else {
                    piece
                }
            }

        // Check Victory Conditions
        var winner: Player? = null
        var winReason: WinReason? = null

        val currentPlayerEntries = if (currentState.currentPlayer == Player.PLAYER_ONE) newP1Entries else newP2Entries
        if (currentPlayerEntries >= TARGET_CASTLE_ENTRIES) {
            winner = currentState.currentPlayer
            winReason = WinReason.CASTLE_ENTRIES
        } else {
            // Check opponent maximum possible entries
            val opponent = currentState.currentPlayer.opponent()
            val opponentEntries = if (opponent == Player.PLAYER_ONE) newP1Entries else newP2Entries
            val opponentActivePieces = updatedPieces.count { it.player == opponent }
            val opponentMaxPossible = opponentEntries + opponentActivePieces

            if (opponentMaxPossible < TARGET_CASTLE_ENTRIES) {
                winner = currentState.currentPlayer
                winReason = WinReason.ELIMINATION
            } else {
                // Check blockade for next player
                val nextPlayer = opponent
                if (!hasAnyLegalMove(nextPlayer, updatedPieces)) {
                    // Next player has no legal moves -> previous player wins by blockade
                    winner = currentState.currentPlayer
                    winReason = WinReason.BLOCKADE
                }
            }
        }

        val nextPlayer = if (winner == null) {
            currentState.currentPlayer.opponent()
        } else {
            currentState.currentPlayer
        }

        val nextState = currentState.copy(
            pieces = updatedPieces,
            currentPlayer = nextPlayer,
            selectedPieceId = null,
            legalMoves = emptySet(),
            player1CastleEntries = newP1Entries,
            player2CastleEntries = newP2Entries,
            player1Captures = newP1Captures,
            player2Captures = newP2Captures,
            lastMove = Pair(origin, destination),
            winner = winner,
            winReason = winReason,
            moveCount = currentState.moveCount + 1,
            isAnimating = false,
            castleReachedAnnouncement = if (isCastleEntry) currentState.currentPlayer else null
        )

        return MoveResolution(
            nextState = nextState,
            movedPiece = movingPiece,
            origin = origin,
            destination = destination,
            capturedPiece = capturedPiece,
            isCastleEntry = isCastleEntry
        )
    }
}
