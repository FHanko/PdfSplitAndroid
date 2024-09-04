package com.github.fhanko.pdfsplit

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.github.fhanko.pdfsplit.Document.PdfFile
import com.github.fhanko.pdfsplit.ui.theme.Typography
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
object HomeScreen

@Composable
fun MainActivity.HomeContent(
    paddingValues: PaddingValues,
    context: Context,
    pdfs: MutableList<PdfFile>,
    navController: NavController
) {
    var expressionInput by rememberSaveable { mutableStateOf("") }
    val docLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) {
        it?.let { uri ->
            when (val pdf = PdfFile.load(context, (pdfs.maxOfOrNull { doc -> doc.id } ?: 0) + 1, uri)) {
                Document.Invalid -> { /* TODO */ }
                is PdfFile -> {
                    pdfs.add(pdf)
                    expressionInput += "${if (expressionInput.isNotEmpty()) "\n" else ""}${pdf.id}:1-${pdf.pages}"
                }
            }
        }
    }

    Column {
        // File Picker
        Button(
            onClick = {
                docLauncher.launch(arrayOf("application/pdf"))
            },
            shape = RectangleShape,
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding())
                .fillMaxWidth()
                .height(48.dp)
        ) { Text(text = "Select PDF", style = Typography.bodyLarge) }
        // Document List
        Surface {
            DocTable(content = pdfs)
        }
        // Expression Input
        TextField(
            value = expressionInput,
            onValueChange = { expressionInput = it },
            label = { Text("Select pages") },
            modifier = Modifier
                .fillMaxWidth()
        )
        // Preview & Save
        Row(modifier = Modifier.height(48.dp)) {
            Button(
                onClick = {
                    processPdf(ProcessType.Preview, expressionInput, pdfs, navController)
                },
                colors = ButtonColors(colorScheme.primaryContainer, colorScheme.primary, colorScheme.errorContainer, colorScheme.error),
                shape = RectangleShape,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight(),
            ) {
                Text("Preview", style = Typography.bodyLarge)
            }
            VerticalDivider()
            Button(
                onClick = {
                    processPdf(ProcessType.Open, expressionInput, pdfs, navController)
                },
                colors = ButtonColors(colorScheme.primaryContainer, colorScheme.primary, colorScheme.errorContainer, colorScheme.error),
                shape = RectangleShape,
                modifier = Modifier.fillMaxSize(),
            ) {
                Text("Open", style = Typography.bodyLarge)
            }
        }
    }
}

@Composable
fun DocTable(content: MutableList<PdfFile>) {
    Column {
        if (content.isNotEmpty()) {
            Row(modifier = Modifier
                .height(32.dp)
                .padding(top = 2.dp)) {
                TableText("ID", 0.1f, TableTextStyle.Header)
                VerticalDivider()
                TableText("Filename", 0.6f, TableTextStyle.Header)
                VerticalDivider()
                TableText("Pages", 0.5f, TableTextStyle.Header)
            }
            HorizontalDivider()
        }
        content.forEach { pdf ->
            Row(modifier = Modifier.height(48.dp), verticalAlignment = Alignment.CenterVertically) {
                TableText("${pdf.id}", 0.1f, TableTextStyle.Content)
                VerticalDivider()
                TableText(pdf.name, 0.6f, TableTextStyle.Content)
                VerticalDivider()
                TableText("${pdf.pages}", 0.5f, TableTextStyle.Content)
                VerticalDivider()
                TextButton(
                    onClick = {
                        content.remove(pdf)
                    },
                    colors = ButtonColors(colorScheme.errorContainer, colorScheme.error, colorScheme.errorContainer, colorScheme.error),
                    shape = RectangleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Close, "Remove pdf", tint = Color.Red,
                        modifier = Modifier.fillMaxSize())
                }
            }
            HorizontalDivider()
        }
    }
}

enum class TableTextStyle(val style: TextStyle) { Header(Typography.titleMedium), Content(Typography.bodyLarge) }
@Composable
fun TableText(text: String, width: Float, style: TableTextStyle) {
    Text(
        text = text, style = style.style, overflow = TextOverflow.Ellipsis, maxLines = 1,
        modifier = Modifier
            .fillMaxWidth(width)
            .padding(start = 6.dp, end = 6.dp)
    )
}

enum class ProcessType { Preview, Open }
private fun MainActivity.processPdf(
    type: ProcessType,
    expression: String,
    pdfs: MutableList<PdfFile>,
    navController: NavController
) {
    if (pdfs.size < 1) {
        Toast.makeText(applicationContext, "Please select a pdf first.", Toast.LENGTH_SHORT).show()
        return
    }
    val preparedExpression = prepareExpression(expression)
    val pdf = ExpressionGrammar(pdfs).parseCatching(applicationContext, preparedExpression)

    if (pdf is PdfFile) {
        when (type) {
            ProcessType.Preview -> {
                pdf.save(previewFile(applicationContext))
                navController.navigate(PreviewScreen)
            }

            ProcessType.Open -> {
                val file = File(filesDir.path + "/PdfSplit.pdf")
                pdf.save(file)
                val uri =
                    FileProvider.getUriForFile(applicationContext, "$packageName.provider", file)
                val intent = Intent.createChooser(Intent(), "Open Pdf")
                intent.setAction(Intent.ACTION_VIEW)
                intent.setDataAndType(uri, "application/pdf")
                intent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                            Intent.FLAG_ACTIVITY_NO_HISTORY
                )
                startActivity(intent)
            }
        }
    }
}

private fun prepareExpression(expression: String): String {
    var result = expression.trim()
    result = result.replace(",\n", "\n")
    while (result.contains("\n\n")) result = result.replace("\n\n", "\n")
    return result
}