package com.github.fhanko.pdfsplit

import android.content.Context
import android.net.Uri
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import java.io.File
import java.io.FileInputStream

sealed class Document {
    class PdfFile private constructor(private val document: PDDocument, val id: Int, val name: String): Document() {
        val pages = document.numberOfPages

        fun import(from: PdfFile, pages: List<Int>) =
            from.document.pages.forEachIndexed { i, page ->
                if (pages.contains(i)) document.addPage(page)
            }

        fun merge(with: PdfFile) =
            with.document.pages.forEach { document.addPage(it) }

        fun save() = document.save(name)

        fun cachePreview(context: Context): File {
            val file = File(context.cacheDir, "preview.pdf")
            document.save(file)
            return file
        }

        companion object {
            fun init(context: Context) {
                PDFBoxResourceLoader.init(context)
            }

            fun load(context: Context, id: Int, uri: Uri): Document {
                val name = uri.path?.split('/')?.last() ?: "Error"
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