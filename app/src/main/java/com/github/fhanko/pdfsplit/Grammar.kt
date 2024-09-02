package com.github.fhanko.pdfsplit

import com.github.h0tk3y.betterParse.combinators.leftAssociative
import com.github.h0tk3y.betterParse.combinators.times
import com.github.h0tk3y.betterParse.combinators.unaryMinus
import com.github.h0tk3y.betterParse.combinators.use
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser
import com.github.fhanko.pdfsplit.Document.PdfFile
import com.github.h0tk3y.betterParse.combinators.map
import com.github.h0tk3y.betterParse.combinators.or
import com.github.h0tk3y.betterParse.combinators.separated

class PdfNotFoundException(id: Int): Exception("Pdf with id $id not found.")
class PageOutOfBounds(id: Int, page: Int): Exception("Pdf with id $id does not contain page $page.")

class ExpressionGrammar(private val pdfs: List<PdfFile>) : Grammar<PdfFile>() {
    private val ws by regexToken("[\\t\\f\\cK ]", ignore = true)
    private val nl by regexToken("\\n")
    private val intToken by regexToken("[0-9]+")

    private val sep by literalToken(",")
    private val to by literalToken("-")
    private val select by literalToken(":")

    private val int by intToken use { text.toInt() }
    private val page by int map { it - 1 }

    private val pageCut: Parser<List<Int>> by
        (page * -to * page use { (if (t1 <= t2) t1..t2 else t1 downTo t2).toList() }) or
        (page map { listOf(it) })

    private val line: Parser<PdfFile> by
        separated(int, sep) * -select * separated(pageCut, sep) use {
            val document = PdfFile.scratch()
            t1.terms.forEach { id ->
                val match = pdfs.find { pdf -> pdf.id == id } ?: throw PdfNotFoundException(id)
                println(t2.terms.flatten())
                document.import(match, t2.terms.flatten())
            }
            document
        }

    override val rootParser by leftAssociative(line, nl) { l, _, r ->
        l.merge(r)
        l
    }
}