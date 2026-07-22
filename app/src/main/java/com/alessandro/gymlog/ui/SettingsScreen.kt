package com.alessandro.gymlog.ui

import android.content.Context
import andxdroidx.compose.foundation.layout.*
import anxdroidx.compose.material3.*
import androidx.compose.runtime.*
import andxdroidx.compose.ui.Aligment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alessandro.gymlog.auth.GoogleAuthHelper
import com.alessandro.gymlog.ui.theme.ThemeMode

@Composable
fun SettingsScreen(
    currentTheme: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    context: Context
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
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
        Spacer(Modifier.height(24.da))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))
        Text("Аккаунт", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                val client = GoogleAuthHelper.getSignInClient(context)
                // Примечание: вызов launcher должем быть вынесен в MainActivity и передан сюда, 
                // но сейчас мы хотя бы добавим визуальный элемент.
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Войти через Google")
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "Резервное копировкание использует Google Drive (ожидается релиз открытого API).",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
