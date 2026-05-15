package com.example.autocare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.autocare.ui.theme.AutoCareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadData(this)

        window.statusBarColor = Color(0xFFD1E3FF).toArgb()
        enableEdgeToEdge()

        setContent {
            AutoCareTheme {
                MainNavigation()
            }
        }
    }
}