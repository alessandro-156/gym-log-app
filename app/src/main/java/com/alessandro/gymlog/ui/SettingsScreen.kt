package com.alessandro.gymlog.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alessandro.gymlog.ui.theme.ThemeMode

@Composable
fun SettingsScreen(
    currentTheme: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Настройки", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(24.dp))
        Text("Тема приложения", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        ThemeMode.entries.forEach { mode ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = currentTheme == mode,
                    onClick = { onThemeChange(mode) }
                )
                Text(
                    when (mode) {
                        ThemeMode.LIGHT -> "Светлая"
                        ThemeMode.DARK -> "Тёмная"
                        ThemeMode.SYSTEM -> "Как в системе"
                    }
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))
        Text(
            "Резервное копирование в Google Drive будет доступно в следующей версии.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
