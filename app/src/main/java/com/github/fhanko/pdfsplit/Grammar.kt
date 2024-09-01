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

class PdfNotFoundException(id: Int): Exception("Pdf with id $id not found.")

class ExpressionGrammar(private val pdfs: List<PdfFile>) : Grammar<PdfFile>() {
    private val ws by regexToken("[\\t\\f\\cK ]", ignore = true)
    private val nl by regexToken("\\n")
    private val intToken by regexToken("[0-9]+")

    private val to by literalToken("-")
    private val select by literalToken(":")

    private val int by intToken use { text.toInt() }

    private val term: Parser<PdfFile> by
    int * -select * int * -to * int use {
        val match = pdfs.find { pdf -> pdf.id == t1 } ?: throw PdfNotFoundException(t1)
        val document = PdfFile.scratch()
        document.import(match, (if (t2 <= t3) t2..t3 else t2 downTo t3).toList())
        document
    }

    override val rootParser by leftAssociative(term, nl) { l, _, r ->
        l.merge(r)
        l
    }
}