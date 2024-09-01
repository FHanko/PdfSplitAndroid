package com.github.fhanko.pdfsplit

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import com.github.fhanko.pdfsplit.Document.PdfFile
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@Serializable
object PreviewScreen

@Composable
fun PreviewContent(paddingValues: PaddingValues, pdf: PdfFile?) {
    if (pdf == null) return

    LazyColumn(modifier = Modifier
        .padding(top = paddingValues.calculateTopPadding())
        .fillMaxHeight()
        .zoomable(rememberZoomState())
    ) {
        items(pdf.pages) {
            Image(pdf.image(it).asImageBitmap(), "Pdf preview page $it")
            Text(
                text = "Page $it",
                modifier = Modifier.padding(bottom = 6.dp, top = 2.dp).fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
            HorizontalDivider(thickness = 3.dp)
        }
    }
}