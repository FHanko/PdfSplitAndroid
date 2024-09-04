package com.github.fhanko.pdfsplit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.serialization.Serializable
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.ui.unit.dp

@Serializable
object HelpScreen

@Composable
fun HelpContent() {
    Surface(
        Modifier
            .background(colorScheme.background)
            .fillMaxSize()
    ) {
        Column(Modifier.padding(12.dp)) {
            Text("PdfSplit Tutorial")
        }
    }
}