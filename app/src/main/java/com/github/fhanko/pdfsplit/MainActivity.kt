package com.github.fhanko.pdfsplit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.fhanko.pdfsplit.ui.theme.PdfSplitTheme
import com.github.fhanko.pdfsplit.Document.PdfFile

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PdfFile.init(applicationContext)

        enableEdgeToEdge()
        setContent {
            PdfSplitTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { TopAppBar(title = { Text("PDF Split") }) }
                ) { innerPadding ->
                    val navController = rememberNavController()
                    val pdfs = remember { mutableStateListOf<PdfFile>() }
                    var previewPdf by remember { mutableStateOf<PdfFile?>(null) }
                    NavHost(navController = navController, startDestination = HomeScreen) {
                        composable<HomeScreen> {
                            HomeContent(innerPadding, applicationContext, pdfs) { preview ->
                                previewPdf = preview
                                navController.navigate(PreviewScreen)
                            }
                        }
                        composable<PreviewScreen> {
                            PreviewContent(innerPadding, applicationContext, previewPdf)
                        }
                    }
                }
            }
        }
    }
}