package com.github.ottx96

import com.github.ottx96.generate.DifferenceGenerator
import com.github.ottx96.parse.DifferenceParser
import com.github.ottx96.logging.Colors
import com.github.ottx96.logging.Styles
import com.github.ottx96.system.ShellCommandExecutor
import io.micronaut.configuration.picocli.PicocliRunner
import picocli.CommandLine.*
import java.io.File

@Command(
    name = "diffview",
    mixinStandardHelpOptions = true
)
class Entrypoint : Runnable {

    enum class Action {LOG, DIFF}

    init {
        verbose = false
        debug = false
    }

    companion object {
        @Option(names = ["-v", "--verbose"], description = ["Sets the output to verbose."])
        var verbose: Boolean = false
        @Option(names = ["--debug"], description = ["Sets the output to debug."])
        var debug: Boolean = false

        fun verbose(func: () -> Unit) {
            if(!verbose && !debug) return
            func()
        }

        fun debug(func: () -> Unit) {
            if(!debug) return
            func()
        }

        @JvmStatic
        fun main(args: Array<String>) {
            PicocliRunner.run(Entrypoint::class.java, *args)
        }
    }

    @Option(names = ["-a", "--action"], description = ["Which action to execute.", "Possible values: (LOG|DIFF)"],
        showDefaultValue = Help.Visibility.ALWAYS, defaultValue = "LOG",
        arity = "0..1")
    lateinit var action: Action

    @Option(names = ["--no-original-extension"], description = ["Omits the original extension for output files.", "e.g.: README.md --> README.html instead of README.md.html", "or build.gradle --> build.html"],
        showDefaultValue = Help.Visibility.ALWAYS, defaultValue = "false",
        arity = "0..1")
    var omitOriginalExtensions: Boolean = false

    @Option(names = ["-R", "--repository", "--directory-in"], description = ["Sets the directory root to read from.", "Has to be inside of a valid git repository."],
        showDefaultValue = Help.Visibility.ALWAYS, defaultValue = "",
        arity = "0..1")
    lateinit var repository: File

    @Option(names = ["-o", "--directory-out"], description = ["Sets the directory to output .html files to.", "Files wll be created as [file name].html", "e.g.: README.md.html"],
        showDefaultValue = Help.Visibility.ALWAYS, defaultValue = "diffview-generated/",
        arity = "0..1")
    lateinit var outputDir: File

    @Parameters(index = "0", description = ["The file whose history/diffviews to generate."], arity = "1..*")
    lateinit var files: List<File>

    override fun run() {
        outputDir.mkdirs()
        verbose {
            (Styles.ITALIC withColor Colors.BLUE).println("""
                    Debug: $debug
                    Verbose: $verbose
                    Action: $action
                    Input directory: ${repository.absolutePath}
                    Output directory: ${outputDir.absolutePath}
                    Files: ${files.joinToString()}
                """.trimIndent())
        }

        when(action) {
            Action.DIFF -> executeDiff()
            Action.LOG -> executeLog()
        }

        (Styles.BOLD withColor Colors.GREEN).println("Everything finished! Exiting ..")
    }

    private fun executeDiff() {
        if(files.size != 2 || checkDirectories()) {
            (Styles.BOLD withColor Colors.RED).errorln("Please provide exactly 2 files!")
            return
        }
        (Styles.BOLD withColor Colors.MAGENTA).println("Processing files ..")
        val output = ShellCommandExecutor(files[1], repository, action, files[0]).execute()
        val views = DifferenceParser(files[0].toString().replace('\\', '/'), output, action).parse()
        val target = File("${outputDir.absolutePath}/${if (omitOriginalExtensions) files[0].nameWithoutExtension else files[0].name}.html")
        (Styles.BOLD withColor Colors.MAGENTA).println("Writing output file to ${target.absolutePath} ..")
        DifferenceGenerator(views).generate(target)
    }

    private fun executeLog() {
        if(checkDirectories()) return
        files.map { File("${repository.absolutePath}/$it") }.forEach {
            (Styles.BOLD withColor Colors.MAGENTA).println("Processing file ${it.absolutePath} ..")
            val output = ShellCommandExecutor(it, repository, action).execute()
            val views = DifferenceParser(it.toString().replace('\\', '/'), output, action).parse()
            val target =
                File("${outputDir.absolutePath}/${if (omitOriginalExtensions) it.nameWithoutExtension else it.name}.html")
            (Styles.BOLD withColor Colors.MAGENTA).println("Writing output file to ${target.absolutePath} ..")
            DifferenceGenerator(views).generate(target)
        }
    }

    private fun checkDirectories(): Boolean {
        if(files.any { it.isDirectory }) {
            (Styles.BOLD withColor Colors.RED).println("Please specify only files! Directories are not allowed.")
            return true
        }
        return false
    }
}
