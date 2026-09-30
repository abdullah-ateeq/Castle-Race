package com.example.castlerace.model

enum class ThemeMode(val title: String) {
    SYSTEM("System Default"),
    LIGHT("Light"),
    DARK("Dark")
}

enum class BoardTheme(val title: String) {
    CLASSIC_STONE("Classic Stone"),
    MIDNIGHT("Midnight Obsidian")
}

data class GameSettings(
    val themeMode: ThemeMode = ThemeMode.DARK,
    val boardTheme: BoardTheme = BoardTheme.CLASSIC_STONE,
    val soundEffects: Boolean = true,
    val hapticFeedback: Boolean = true,
    val showLegalMoves: Boolean = true,
    val showLastMove: Boolean = true
)
