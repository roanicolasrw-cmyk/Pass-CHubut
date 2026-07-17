package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.data.ChubutDatabase
import com.example.data.ChubutRepository
import com.example.ui.ChubutViewModel
import com.example.ui.ChubutViewModelFactory
import com.example.ui.MainAppScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    // Instantiate Database, Repository, and ViewModel using standard Android architecture guidelines
    private val database by lazy { ChubutDatabase.getDatabase(this) }
    private val repository by lazy { ChubutRepository(database.chubutDao()) }
    
    private val viewModel: ChubutViewModel by viewModels {
        ChubutViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Full Edge-to-Edge display support as required by frontend-design guidelines
        enableEdgeToEdge()
        
        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}
