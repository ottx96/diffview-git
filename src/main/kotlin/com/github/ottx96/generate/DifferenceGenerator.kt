package com.github.ottx96.generate

import com.github.ottx96.Entrypoint.Companion.debug
import com.github.ottx96.Entrypoint.Companion.verbose
import com.github.ottx96.logging.Colors
import com.github.ottx96.logging.Styles
import com.github.ottx96.parse.DifferenceView
import java.io.File
import java.io.IOException

enum class GenerationFormat {
    HTML
}

enum class RowType(val classname: String, val icon: String) {
    NEUTRAL("row", "⚪"), ADDED("row-created", "🟢"), REMOVED("row-removed", "🔴")
}

class DifferenceGenerator(val data: MutableList<DifferenceView>) {

    companion object {
        val TEMPLATE_HTML = File("src/main/resources/template/html/html.template")
        val TEMPLATE_COMPARISON = File("src/main/resources/template/html/comparison.template")
        val TEMPLATE_ROW = File("src/main/resources/template/html/row.template")
    }

    fun generate(output: File, format: GenerationFormat = GenerationFormat.HTML): File {
        when (format) {
            GenerationFormat.HTML -> return generateHTML(output)
        }
    }

    private fun generateHTML(output: File): File {
        // read template files
        val rowTemplate = TEMPLATE_ROW.readText()
        val comparisonTemplate = TEMPLATE_COMPARISON.readText()
        val htmlTemplate = TEMPLATE_HTML.readText()

        // create comparisons
        val comparisons = mutableListOf<String>()

        debug {
            data.reversed().forEach {
                File("build/new.txt").writeText(it.new.lines.joinToString("\n"))
                File("build/old.txt").writeText(it.old.lines.joinToString("\n"))
                File("build/unified.txt").writeText(it.unified.entries.joinToString(separator = "\n") { "${it.key} = ${it.value}" })
            }
        }

        val readCommits = mutableMapOf<String, Boolean>()
        data.forEach {
            if(readCommits[it.commit] == true) return@forEach
            verbose {
                (Styles.ITALIC withColor Colors.WHITE).println("Generating HTML for commit ${it.commit}..")
                (Styles.ITALIC withColor Colors.WHITE).println("File name: ${it.name}")
                (Styles.ITALIC withColor Colors.WHITE).println("File hunk old: ${it.old.range}")
                (Styles.ITALIC withColor Colors.WHITE).println("File hunk new: ${it.new.range}")
            }

            val tables = mutableListOf<List<String>>()
            data.filter { comp -> it.commit == comp.commit }.forEach {
                // create rows for comparison
                val tableRows = mutableListOf<String>()

                it.unified.toSortedMap().forEach { (_, data) ->
                    val new = it.new.lines.find { it.index == data.index }
                    val old = it.old.lines.find { it.index == data.index }
                    val type =
                        if (new?.marked == true) RowType.ADDED else if (old?.marked == true) RowType.REMOVED else RowType.NEUTRAL

                    tableRows += rowTemplate
                        .replace("@@line.class@@", type.classname)
                        .replace("@@line.icon@@", type.icon)
                        .replace("@@line.number.old@@", it.old.getLineNumber(data))
                        .replace("@@line.number.new@@", it.new.getLineNumber(data))
                        .replace(
                            "@@line.content@@",
                            data.value.replace(" ", "&nbsp;")
                                .replace("<", "&lt;")
                                .replace(">", "&gt;")
                        )
                }
                tables += tableRows
            }
            readCommits[it.commit] = true
            comparisons += comparisonTemplate
                .replace("@@file.name@@", it.name)
                .replace("@@file.commit@@", it.commit)
                .replace("@@tables.start@@", tables.joinToString(separator = "\n<hr>\n<hr>\n"){
                    """<table>
                        |  <tbody>
                        |    ${it.joinToString("\n")}
                        |  </tbody>
                        |</table>
                    """.trimMargin()
                })
        }


        // create html
        output.writer().use {
            it.write(htmlTemplate.replace("@@comparisons.start@@", comparisons.joinToString(separator = "\n")))
        }
        if (!output.exists() || output.length() == 0L) throw IOException()
        return output
    }

    private fun DifferenceView.FileHunk.getLineNumber(other: DifferenceView.FileHunk.IdentifiableLine): String {
        val i = this.lines.indexOf(other)
        return if (i == -1) "" else "${i + this.range.first}"
    }

}