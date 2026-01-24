package ru.fefu.splitmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.fefu.splitmate.ui.navigation.AppNavigation
import ru.fefu.splitmate.ui.theme.SplitMateTheme
import ru.fefu.splitmate.ui.viewmodel.SplitViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplitMateTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: SplitViewModel = viewModel()
                    AppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}