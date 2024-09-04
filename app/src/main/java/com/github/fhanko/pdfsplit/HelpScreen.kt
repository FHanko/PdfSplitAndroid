package com.github.fhanko.pdfsplit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.serialization.Serializable
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.unit.dp
import com.github.fhanko.pdfsplit.ui.theme.Typography

@Serializable
object HelpScreen

val step1 = """
    Select one or more pdfs from your device to work on. 
    They will be shown on the home screen together with their id, 
    use this id to reference this pdf. 
""".trimIndent().replace("\n", "")

val lineBreak = LineBreak.Paragraph.copy(strategy = LineBreak.Strategy.Balanced)

@Composable
fun HelpContent() {

    Surface(
        Modifier
            .background(colorScheme.background)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(Modifier.padding(12.dp)) {
            Text("PdfSplit Tutorial", style = Typography.titleLarge)
            Text("Step 1", style = Typography.titleMedium)
            Text(step1, style = Typography.bodyLarge.copy(lineBreak = lineBreak))
            Step2()
            Text("Step 3", style = Typography.titleMedium, modifier = Modifier.padding(top = 18.dp))
            Text("Preview the resulting pdf to make sure it is generated how you want and then export " +
                    "it to a pdf viewer by pressing Open. From there you can share and/or download it."
                , style = Typography.bodyLarge.copy(lineBreak = lineBreak))
        }
    }
}

@Composable
fun Step2() {
    Text("Step 2", style = Typography.titleMedium, modifier = Modifier.padding(top = 18.dp))
    Text("Edit the selection text to select the pages in the order you want.\n" +
              "To select pages from a pdf input its id followed by a ':' and ranges of pages " +
              "you want to select separated by a ','.", style = Typography.bodyLarge.copy(lineBreak = lineBreak))
    TextField(value = "1:1-5, 7-9, 13", { }, readOnly = true)
    Text("To merge two pdfs together place them in different lines following each other. " +
            "They don't need to be in order of their ids.", modifier = Modifier.padding(top = 12.dp)
        , style = Typography.bodyLarge.copy(lineBreak = lineBreak))
    TextField(value = "1:1-5, 7-9\n3:1-10\n2", { }, readOnly = true)
    Text("Pdfs can be reversed by reversing the page range.", modifier = Modifier.padding(top = 12.dp))
    TextField(value = "1:10-1", { }, readOnly = true)
    Text("When selecting the same page ranges from multiple pdfs they can be put together before the ':'."
        , modifier = Modifier.padding(top = 12.dp), style = Typography.bodyLarge.copy(lineBreak = lineBreak))
    TextField(value = "1,2,3:1-4", { }, readOnly = true)
}

