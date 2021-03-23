package com.github.ottx96.parse

import com.github.ottx96.Entrypoint
import com.github.ottx96.logging.Colors
import com.github.ottx96.logging.Styles

class DifferenceParser(private val input: String) {

    private fun verbose(f: () -> Unit) {
        if(!Entrypoint.verbose) return
        f()
    }

    fun parse() {
        val entries = input.split(Regex("commit [a-z0-9]+\n")).filter { it.isNotBlank() }
        (Styles.BOLD withColor Colors.MAGENTA).println("Processing ${entries.size} entries..")
        entries.forEach { commit ->
            val lines = commit.lines().dropWhile { !it.matches(Regex("""@@ -\d{1}.*\d{1}.*@@.*""")) }
            // @@ from-file-range to-file-range @@ [header]
            var hunkID = lines[0]
            lines.drop(1)
            verbose{ (Styles.ITALIC withColor Colors.WHITE).println("Processing hunk identifier: $hunkID") }
            hunkID = hunkID.replace("@@ ", "").replace(" @@", "") // "-1,9 +1,15"

            verbose {(Styles.ITALIC withColor Colors.WHITE).println("Negative range: ${determineNegativeRange(hunkID)}, Positive range: ${determinePositiveRange(hunkID)}")}
            val negativeHunk = DifferenceView.FileHunk(determineNegativeRange(hunkID))
            val positiveHunk = DifferenceView.FileHunk(determinePositiveRange(hunkID))

            // Populate Lines
            lines.forEachIndexed { i, string ->
                when {
                    string.startsWith("-") -> negativeHunk.lines += DifferenceView.FileHunk.IdentifiableLine(i, true, string.substring(1))
                    string.startsWith("+") -> positiveHunk.lines += DifferenceView.FileHunk.IdentifiableLine(i, true, string.substring(1))
                    string.startsWith(" ") -> {
                        negativeHunk.lines += DifferenceView.FileHunk.IdentifiableLine(i, false, string.substring(1))
                        positiveHunk.lines += DifferenceView.FileHunk.IdentifiableLine(i, false, string.substring(1))
                    }
                }
            }

            val differenceView = DifferenceView("", "", negativeHunk, positiveHunk)

            verbose {
                println(differenceView)
            }
        }
    }

    private fun determineNegativeRange(rangeString: String): IntRange {
        val split = rangeString
            .replace("-", "")
            .split(' ')[0]
            .split(',')
        return split[0].toInt()..split[1].toInt()
    }
    private fun determinePositiveRange(rangeString: String): IntRange {
        val split = rangeString
            .replace("+", "")
            .split(' ')[1]
            .split(',')
        return split[0].toInt()..split[1].toInt()
    }
}

data class DifferenceView(val commit: String, val name: String, val old: FileHunk, val new: FileHunk, val unified:MutableMap<Int, FileHunk.IdentifiableLine> = mutableMapOf()) {

    init {
        old.lines.forEach { unified[it.index] = it }
        new.lines.forEach { unified[it.index] = it }
    }

    data class FileHunk(val range: IntRange, var lines: List<IdentifiableLine> = listOf()){
        data class IdentifiableLine(val index: Int, val marked: Boolean, val value: String)
    }

    override fun toString(): String {
        var result = ""
        unified.entries.sortedBy { it.key }.forEach {
            when {
                !it.value.marked -> result += (Styles.BOLD withColor Colors.NONE).format(" \t${it.value.value}\n")
                old.lines.contains(it.value) -> result += (Styles.BOLD withColor Colors.RED).format("-\t${it.value.value}\n")
                new.lines.contains(it.value) -> result += (Styles.BOLD withColor Colors.GREEN).format("+\t${it.value.value}\n")
            }
        }

        return result
    }
}
