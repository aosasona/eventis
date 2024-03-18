package com.trulyao.eventis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.trulyao.eventis.ui.theme.EventisTheme
import com.trulyao.eventis.views.Root

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EventisTheme {
                Root()
            }
        }
    }
}