package com.github.fhanko.pdfsplit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.github.fhanko.pdfsplit.ui.theme.PdfSplitTheme
import com.github.fhanko.pdfsplit.ui.theme.Typography
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
                    MainContent(innerPadding)
                }
            }
        }
    }

@Composable
fun MainContent(paddingValues: PaddingValues) {
    val docList = remember { mutableStateListOf<PdfFile>() }
    val docLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) {
        it?.let { uri ->
            when (val pdf = PdfFile.load(applicationContext, (docList.maxOfOrNull { doc -> doc.id } ?: 0) + 1, uri)) {
                Document.Invalid -> { /* TODO */ }
                is PdfFile -> { docList.add(pdf) }
            }
        }
    }
    var expressionInput by remember { mutableStateOf("") }

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
            DocTable(content = docList)
        }
        // Expression Input
        TextField(
            value = expressionInput,
            onValueChange = { expressionInput = it },
            label = { Text("Expression") },
            modifier = Modifier
                .fillMaxWidth()
        )
        // Preview & Save
        Row(modifier = Modifier.height(48.dp)) {
            Button(
                onClick = { /*TODO*/ },
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
                onClick = { /*TODO*/ },
                colors = ButtonColors(colorScheme.primaryContainer, colorScheme.primary, colorScheme.errorContainer, colorScheme.error),
                shape = RectangleShape,
                modifier = Modifier.fillMaxSize(),
            ) {
                Text("Save", style = Typography.bodyLarge)
            }
        }
    }
}

@Composable
fun DocTable(content: List<PdfFile>) {
    Column {
        if (content.isNotEmpty()) {
            Row(modifier = Modifier.height(32.dp).padding(top = 2.dp)) {
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
                Button(
                    onClick = { /*TODO*/ },
                    colors = ButtonColors(colorScheme.errorContainer, colorScheme.error, colorScheme.errorContainer, colorScheme.error),
                    shape = RectangleShape,
                    modifier = Modifier.fillMaxWidth().padding(6.dp)
                ) {
                    Text(text = "X")
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
        text = text, style = style.style,
        modifier = Modifier
            .fillMaxWidth(width)
            .padding(start = 6.dp, end = 6.dp)
    )
}
}