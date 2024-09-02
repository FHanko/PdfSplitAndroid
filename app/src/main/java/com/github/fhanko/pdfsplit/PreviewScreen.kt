package com.github.fhanko.pdfsplit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import com.github.fhanko.pdfsplit.Document.PdfFile
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import java.io.File

@Serializable
object PreviewScreen

@Composable
fun PreviewContent(paddingValues: PaddingValues, context: Context, pdf: PdfFile?) {
    if (pdf == null) return

    val file = File("${context.cacheDir}/preview")
    val screenWidth = context.resources.displayMetrics.widthPixels
    pdf.save(file)

    LazyColumn(modifier = Modifier
        .padding(top = paddingValues.calculateTopPadding())
        .fillMaxHeight()
        .zoomable(rememberZoomState())
        .background(Color.White)
    ) {
        items(pdf.pages) { page ->
            val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            val img = Bitmap.createBitmap(
                screenWidth,
                (screenWidth.toFloat() / pdf.pageWidth(page) * pdf.pageHeight(page)).toInt(),
                Bitmap.Config.ARGB_8888
            )
            PdfRenderer(fileDescriptor).openPage(page).render(img, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            Image(img.asImageBitmap(), "Pdf preview page $page")
            Text(
                text = "Page ${page + 1}",
                modifier = Modifier
                    .padding(bottom = 6.dp, top = 2.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
            HorizontalDivider(thickness = 3.dp)
        }
    }
}