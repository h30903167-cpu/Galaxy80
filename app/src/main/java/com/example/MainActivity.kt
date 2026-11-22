package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ui.MainApp
import com.example.ui.theme.DasaniTheme

class MainActivity : ComponentActivity() {

    private var incomingLinkUri by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        incomingLinkUri = intent?.dataString
        enableEdgeToEdge()
        setContent {
            DasaniTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainApp(incomingLinkUri = incomingLinkUri)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        incomingLinkUri = intent.dataString
    }
}
