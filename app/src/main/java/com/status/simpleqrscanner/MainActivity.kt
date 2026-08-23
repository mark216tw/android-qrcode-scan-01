package com.status.simpleqrscanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import com.status.simpleqrscanner.data.AppRepository
import com.status.simpleqrscanner.ui.SimpleQrApp
import com.status.simpleqrscanner.ui.theme.SimpleQrTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val repository = remember { AppRepository(applicationContext) }
            SimpleQrTheme(
                accent = repository.accent,
                mode = repository.mode,
                context = this,
            ) {
                Surface {
                    SimpleQrApp(repository)
                }
            }
        }
    }
}
