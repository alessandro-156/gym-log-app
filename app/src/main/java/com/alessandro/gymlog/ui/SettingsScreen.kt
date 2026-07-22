package com.alessandro.gymlog.ui

import android.content.Context
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
    onThemeChange: (ThemeMode) -> Unit,
    context: Context
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Настройки", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))
        
        Text("Тема оформления", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))

        ThemeMode.values().forEach { mode ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (currentTheme == mode),
                    onClick = { onThemeChange(mode) }
                )
                Text(
                    text = when (mode) {
                        ThemeMode.LIGHT -> "Светлая"
                        ThemeMode.DARK -> "Темная"
                        ThemeMode.SYSTEM -> "Системная"
                    },
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
