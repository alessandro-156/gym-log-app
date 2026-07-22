package com.alessandro.gymlog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.alessandro.gymlog.ui.theme.GymLogTheme
import com.alessandro.gymlog.ui.theme.ThemeMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val mode by Settings.themeMode(this).collectAsState(initial = ThemeMode.SYSTEM)
            GymLogTheme(mode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    // TODO: навигация и экраны — следующая порция
                    Text("GymLog: каркас работает")
                }
            }
        }
    }
}
