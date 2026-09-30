package com.example.castlerace.model

data class GridPosition(
    val row: Int,
    val col: Int
) {
    fun isValid(gridSize: Int = 7): Boolean {
        return row in 0 until gridSize && col in 0 until gridSize
    }

    fun offset(dRow: Int, dCol: Int): GridPosition {
        return GridPosition(row + dRow, col + dCol)
    }

    override fun toString(): String = "($row, $col)"
}
