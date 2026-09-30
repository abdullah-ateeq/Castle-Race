package com.example.castlerace.model

data class GameStats(
    val gamesPlayed: Int = 0,
    val player1Wins: Int = 0,
    val player2Wins: Int = 0,
    val fastestCastleVictoryTurns: Int = 0,
    val mostCaptures: Int = 0
)
