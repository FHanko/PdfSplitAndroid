package com.github.fhanko.pdfsplit

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.multipdf.PDFMergerUtility
import com.tom_roush.pdfbox.pdmodel.PDDocument
import java.io.File
import java.io.FileInputStream
import com.github.fhanko.pdfsplit.ParsingException.PageOutOfBoundsException


sealed class Document {
    class PdfFile private constructor(private val document: PDDocument, val id: Int, val name: String): Document() {
        val pages = document.numberOfPages

        fun import(from: PdfFile, pages: List<Int>) {
            val temp = scratch()
            pages.forEach {
                if (it < 0 || it >= from.document.numberOfPages) throw PageOutOfBoundsException(from.id, it)

                temp.document.importPage(from.document.getPage(it))
            }
            merge(temp)
        }

        fun merge(with: PdfFile) {
            val merger = PDFMergerUtility()
            merger.appendDocument(document, with.document)
        }

        fun save(file: File) = document.save(file)

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