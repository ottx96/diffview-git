package com.github.ottx96.system

import com.github.ottx96.Entrypoint.Companion.verbose
import com.github.ottx96.logging.Colors
import com.github.ottx96.logging.Styles
import java.io.File
import java.util.concurrent.TimeUnit

class ShellCommandExecutor(private val file: File, private val directory: File) {

    private val err: File = File.createTempFile("DIFFVIEW.", ".TMP")
    private val out: File = File.createTempFile("DIFFVIEW.", ".TMP")

    fun execute(): String {
        if(verbose) {
            (Styles.ITALIC withColor Colors.WHITE).println("Starting directory: ${directory.absolutePath}")
            (Styles.ITALIC withColor Colors.WHITE).println("Creating temporary file (err): ${err.absolutePath}")
            (Styles.ITALIC withColor Colors.WHITE).println("Creating temporary file (out): ${out.absolutePath}")
        }

        val pb = ProcessBuilder().directory(directory.absoluteFile)
            .redirectError(err).redirectOutput(out)
            .command("git", "log", "-p", "--follow", file.absolutePath)

        if(verbose) (Styles.ITALIC withColor Colors.WHITE).println("Running system command: ${pb.command().joinToString(separator = " ")}")

            pb.start().waitFor(5, TimeUnit.SECONDS)

        if(err.length() > 0) throw FileSystemException(err, reason = err.readText())
        return out.readText()
    }

}