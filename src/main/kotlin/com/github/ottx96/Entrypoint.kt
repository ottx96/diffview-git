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

    companion object {
        @Option(names = ["-v", "--verbose"], description = ["Sets the output to verbose."])
        var verbose: Boolean = false

        fun verbose(f: () -> Unit) {
            if(!verbose) return
            f()
        }

        @JvmStatic
        fun main(args: Array<String>) {
            PicocliRunner.run(Entrypoint::class.java, *args)
        }
    }

    @Option(names = ["-d", "--directory-in"], description = ["Sets the directory root to read from.", "Has to be inside of a valid git repository."],
        showDefaultValue = Help.Visibility.ALWAYS, defaultValue = "",
        arity = "0..1")
    lateinit var inputDirectory: File

    @Option(names = ["-o", "--directory-out"], description = ["Sets the directory to output .html files to.", "Files wll be created as [file name].html", "e.g.: README.md.html"],
        showDefaultValue = Help.Visibility.ALWAYS, defaultValue = "diffview-generated/",
        arity = "0..1")
    lateinit var outputDirectory: File

    @Parameters(index = "0", description = ["The file whose history/diffviews to generate."], arity = "1..*")
    lateinit var files: List<File>

    override fun run() {
        (Styles.BOLD withStyle Styles.UNDERLINE withStyle Styles.REVERSE withColor Colors.GREEN)
            .println("Hello, Formatted World!")

        files.forEach {
            (Styles.BOLD withColor Colors.MAGENTA).println("Processing file ${it.absolutePath} ..")
            val output = ShellCommandExecutor(it, inputDirectory).execute()
            val views = DifferenceParser(output).parse()
            DifferenceGenerator(views).generate(File("Test.html"))
            (Styles.BOLD withColor Colors.MAGENTA).println("Writing output file to ${it.absolutePath} ..")
        }
    }
}
