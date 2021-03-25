package com.github.ottx96.generate

import com.github.ottx96.Entrypoint.Companion.debug
import com.github.ottx96.Entrypoint.Companion.verbose
import com.github.ottx96.logging.Colors
import com.github.ottx96.logging.Styles
import com.github.ottx96.parse.DifferenceView
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter

enum class GenerationFormat {
    HTML
}

enum class RowType(val classname: String, val icon: String) {
    NEUTRAL("row", "⚪"), ADDED("row-created", "🟢"), REMOVED("row-removed", "🔴")
}

class DifferenceGenerator(val data: MutableList<DifferenceView>) {

    fun generate(output: File, format: GenerationFormat = GenerationFormat.HTML): File {
        when (format) {
            GenerationFormat.HTML -> return generateHTML(output)
        }
    }

    private fun generateHTML(output: File): File {
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

                    tableRows += """
                        <tr class="${type.classname}">
                            <td class="line-icon">${type.icon}</td>
                            <td class="line-number">${it.old.getLineNumber(data)}</td>
                            <td class="line-number">${it.new.getLineNumber(data)}</td>
                            <td class="context">${data.value.replace(" ", "&nbsp;").replace(">", "&gt;").replace("<", "&lt;")}</td>
                        </tr>
                    """.trimIndent()
                }
                tables += tableRows
            }
            readCommits[it.commit] = true
            comparisons += """
                <div class="comparison">
                    <h1>${it.name} (${it.commit})</h1>
                    ${tables.joinToString(separator = "\n<hr>\n<hr>\n"){
                        """<table>
                            |  <tbody>
                            |    ${it.joinToString(separator = "\n")}
                            |  </tbody>
                            |</table>
                        """.trimMargin()
                    }}
                </div>
            """.trimIndent()
        }

        // create html
        if(!output.exists()) {
            output.parentFile.mkdirs()
            output.createNewFile()
        }
        output.writer().use {
            it.write(createFromTemplate(comparisons))
        }
        if (!output.exists() || output.length() == 0L) throw IOException()
        return output
    }

    private fun createFromTemplate(comparisons: MutableList<String>): String {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>Diffview (Git) by ottx96</title>
                    <link rel="preconnect" href="https://fonts.gstatic.com">
                    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@300&display=swap" rel="stylesheet">
                    <style>
                        body {
                            margin: 0;
                            padding: 0;
                            overflow-x: hidden;
                        }

                        * {
                            font-family: 'Courier New', Courier, monospace;
                            font-size: 1rem;
                            color: #282c34;
                        }

                        .App {
                            background-color: #282c34;
                            min-height: 100vh;
                            max-width: 100%;
                            min-width: 100%;
                        }

                        .comparison {
                            padding-top: 3vh;
                            padding-bottom: 2vh;
                            display: flex;
                            flex-direction: column;
                            align-items: center;
                            justify-content: center;
                            color: white;
                        }

                        h1 {
                            width: 100%;
                            margin-left: 6%;
                            color: white;
                            text-align: start;
                            font-family: 'Montserrat', sans-serif;
                        }

                        table {
                            background-color: white;
                            width: 95%;
                            display: flex;
                            justify-content: left;
                            align-items: center;
                            border-radius: 7px;
                            border: white 1px solid;
                            overflow: hidden;
                        }

                        tbody {
                            padding-top: 5px;
                            width: 100%;
                            display: flex;
                            flex-direction: column;
                        }

                        .row *, .row-created *, .row-removed * {
                            word-break: break-all;
                        }

                        .row {
                            width: 100%;
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            background-color: rgba(255, 255, 255, 0.8);
                        }

                        .row-created {
                            width: 100%;
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            background-color: rgba(0, 255, 0, 0.35);
                        }

                        .row-removed {
                            width: 100%;
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            background-color: rgba(255, 0, 0, 0.32);
                        }

                        .line-number {
                            color: #282c34;
                            font-size: 0.7rem;
                            width: 60px;
                            text-align: center;
                            border-right: #282c34 1px solid;
                        }

                        .line-icon {
                            font-size: 0.4rem;
                            width: 60px;
                            text-align: center;
                            border-right: #282c34 1px solid;
                        }

                        .context, .created, .deleted {
                            width: 100%;
                            margin-left: 2%;
                        }
                    </style>
                </head>
                <body>
                <div class="App">
                    ${comparisons.joinToString(separator = "\n")}
                </div>
                </body>
                </html>
            """.trimIndent()
    }

    private fun DifferenceView.FileHunk.getLineNumber(other: DifferenceView.FileHunk.IdentifiableLine): String {
        val i = this.lines.indexOf(other)
        return if (i == -1) "" else "${i + this.range.first}"
    }

}