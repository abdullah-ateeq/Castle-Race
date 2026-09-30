package com.example.castlerace.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.castlerace.model.BoardTheme
import com.example.castlerace.model.ThemeMode

val LocalBoardTheme = staticCompositionLocalOf { BoardTheme.CLASSIC_STONE }

private val DarkColorScheme = darkColorScheme(
    primary = Player1Bronze,
    onPrimary = Color.Black,
    secondary = Player2Teal,
    onSecondary = Color.Black,
    tertiary = CastleIvory,
    onTertiary = Color.Black,
    background = DarkBackground,
    onBackground = Color(0xFFECEFF4),
    surface = DarkSurface,
    onSurface = Color(0xFFECEFF4),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFC7CDD8),
    outline = DarkGridBorder,
    error = CaptureWarning,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Player1BronzeDark,
    onPrimary = Color.White,
    secondary = Player2TealDark,
    onSecondary = Color.White,
    tertiary = Color(0xFF5D4037),
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = Color(0xFF191C20),
    surface = LightSurface,
    onSurface = Color(0xFF191C20),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF434750),
    outline = LightGridBorder,
    error = CaptureWarning,
    onError = Color.White
)

@Composable
fun CastleRaceTheme(
    themeMode: ThemeMode = ThemeMode.DARK,
    boardTheme: BoardTheme = BoardTheme.CLASSIC_STONE,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalBoardTheme provides boardTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}