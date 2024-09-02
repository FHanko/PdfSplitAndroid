package com.github.fhanko.pdfsplit

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import java.io.File
import java.io.FileInputStream


sealed class Document {
    class PdfFile private constructor(private val document: PDDocument, val id: Int, val name: String): Document() {
        val pages = document.numberOfPages

        fun import(from: PdfFile, pages: List<Int>) {
            from.document.pages.forEachIndexed { i, page ->
                if (pages.contains(i)) document.importPage(page)
            }
        }

        fun merge(with: PdfFile) =
            with.document.pages.forEach { document.importPage(it) }

        fun save(file: File) = document.save(file)

        fun pageWidth(page: Int) = document.getPage(page).bBox.width
        fun pageHeight(page: Int) = document.getPage(page).bBox.height

        companion object {
            fun init(context: Context) {
                PDFBoxResourceLoader.init(context)
            }

            fun load(context: Context, id: Int, uri: Uri): Document {
                val name = queryName(context.contentResolver, uri)
                context.contentResolver.openFileDescriptor(uri, "r")?.use {
                    return PdfFile(PDDocument.load(FileInputStream(it.fileDescriptor)), id, name)
                }
                return Invalid
            }

            fun scratch() = PdfFile(PDDocument(), 0, "")
        }
    }

    data object Invalid: Document()
}

private fun queryName(resolver: ContentResolver, uri: Uri): String {
    val returnCursor =
        resolver.query(uri, null, null, null, null) ?: return "Error"
    val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    returnCursor.moveToFirst()
    val name = returnCursor.getString(nameIndex)
    returnCursor.close()
    return name
}