package com.github.fhanko.pdfsplit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import my.nanihadesuka.compose.LazyColumnScrollbar
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import java.io.File

fun previewFile(context: Context): File = File("${context.cacheDir}/preview")

@Serializable
object PreviewScreen

@Composable
fun PreviewContent() {
    val context = LocalContext.current
    val screenWidth = context.resources.displayMetrics.widthPixels

    val fileDescriptor = ParcelFileDescriptor.open(previewFile(context), ParcelFileDescriptor.MODE_READ_ONLY)
    val renderer = PdfRenderer(fileDescriptor)

    val listState = rememberLazyListState()
    LazyColumnScrollbar(
        state = listState
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .zoomable(rememberZoomState())
                .background(Color.White),
            state = listState
        ) {
            items(renderer.pageCount) { pageIndex ->
                val page = renderer.openPage(pageIndex)
                val img = Bitmap.createBitmap(
                    screenWidth,
                    (screenWidth.toFloat() / page.width * page.height).toInt(),
                    Bitmap.Config.ARGB_8888
                )
                page.render(img, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                Image(img.asImageBitmap(), "Pdf preview page $pageIndex")
                Text(
                    text = "Page ${pageIndex + 1}",
                    modifier = Modifier
                        .padding(bottom = 6.dp, top = 2.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
                HorizontalDivider(thickness = 3.dp)
                page.close()
            }
        }
    }
}