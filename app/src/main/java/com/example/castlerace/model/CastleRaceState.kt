package com.example.castlerace.model

data class CastleRaceState(
    val pieces: List<CastlePiece>,
    val currentPlayer: Player = Player.PLAYER_ONE,
    val selectedPieceId: Int? = null,
    val legalMoves: Set<GridPosition> = emptySet(),
    val player1CastleEntries: Int = 0,
    val player2CastleEntries: Int = 0,
    val player1Captures: Int = 0,
    val player2Captures: Int = 0,
    val lastMove: Pair<GridPosition, GridPosition>? = null,
    val winner: Player? = null,
    val winReason: WinReason? = null,
    val moveCount: Int = 0,
    val isAnimating: Boolean = false,
    val matchDurationSeconds: Long = 0L,
    val castleReachedAnnouncement: Player? = null
) {
    val isGameOver: Boolean
        get() = winner != null

    fun activePieces(player: Player): List<CastlePiece> =
        pieces.filter { it.player == player }

    fun castleEntries(player: Player): Int =
        if (player == Player.PLAYER_ONE) player1CastleEntries else player2CastleEntries

    fun captures(player: Player): Int =
        if (player == Player.PLAYER_ONE) player1Captures else player2Captures

    fun pieceAt(position: GridPosition): CastlePiece? =
        pieces.find { it.position == position }

    fun selectedPiece(): CastlePiece? =
        pieces.find { it.id == selectedPieceId }
}
