package com.example.castlerace.model

enum class Player {
    PLAYER_ONE,
    PLAYER_TWO;

    fun opponent(): Player = when (this) {
        PLAYER_ONE -> PLAYER_TWO
        PLAYER_TWO -> PLAYER_ONE
    }

    val displayName: String
        get() = when (this) {
            PLAYER_ONE -> "Player 1"
            PLAYER_TWO -> "Player 2"
        }
}
