package com.github.ottx96.generate

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

class DifferenceGenerator(val data: List<DifferenceView>) {

    companion object {
        val TEMPLATE_HTML = File("src/main/resources/template/html/html.template")
        val TEMPLATE_COMPARISON = File("src/main/resources/template/html/comparison.template")
        val TEMPLATE_ROW = File("src/main/resources/template/html/row.template")
    }

    fun generate(output: File, format: GenerationFormat = GenerationFormat.HTML): File {
        when(format) {
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

        data.forEach {
            verbose {
                (Styles.ITALIC withColor Colors.WHITE).println("Generating HTML for commit ${it.commit}..")
                (Styles.ITALIC withColor Colors.WHITE).println("File name: ${it.name}")
                (Styles.ITALIC withColor Colors.WHITE).println("File hunk old: ${it.old.range}")
                (Styles.ITALIC withColor Colors.WHITE).println("File hunk new: ${it.new.range}")
            }
            // create rows for comparison
            val tableRows = mutableListOf<String>()

            it.unified.toSortedMap().forEach{ (idx, data) ->
                val new = it.new.lines.find { it.index == data.index }
                val old = it.old.lines.find { it.index == data.index }
                val type = if(new?.marked == true) RowType.ADDED else if(old?.marked == true) RowType.REMOVED else RowType.NEUTRAL

                tableRows += rowTemplate
                    .replace("@@line.class@@", type.classname)
                    .replace("@@line.icon@@", type.icon)
                    .replace("@@line.number.old@@", it.old.lines.getLineNumber(data))
                    .replace("@@line.number.new@@", it.new.lines.getLineNumber(data))
                    .replace("@@line.content@@", "${data.value.replace(" ", "&nbsp;")}")
            }

        comparisons += comparisonTemplate
            .replace("@@file.name@@", it.name)
            .replace("@@file.commit@@", it.commit)
            .replace("@@rows.start@@", tableRows.joinToString(separator = "\n"))
        }

        // create html
        output.writer().use {
            it.write(htmlTemplate.replace("@@comparisons.start@@", comparisons.joinToString(separator = "\n")))
        }
        if(!output.exists() || output.length() == 0L) throw IOException()
        return output
    }

    private fun List<DifferenceView.FileHunk.IdentifiableLine>.getLineNumber(other: DifferenceView.FileHunk.IdentifiableLine): String {
        val i = indexOf(other)
        return if(i == -1) "" else "$i"
    }

}