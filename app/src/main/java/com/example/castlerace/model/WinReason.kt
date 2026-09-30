package com.example.castlerace.model

enum class WinReason(val displayTitle: String, val description: String) {
    CASTLE_ENTRIES(
        displayTitle = "3 CASTLE ENTRIES",
        description = "Infiltrated the enemy fortress with 3 pieces!"
    ),
    ELIMINATION(
        displayTitle = "OPPONENT ELIMINATED",
        description = "Opponent cannot possibly reach 3 castle entries."
    ),
    BLOCKADE(
        displayTitle = "OPPONENT BLOCKED",
        description = "Opponent has no legal moves remaining."
    )
}
