package com.github.ottx96

import io.micronaut.configuration.picocli.PicocliRunner
import picocli.CommandLine.Command
import picocli.CommandLine.Help.Ansi
import picocli.CommandLine.Option

@Command(
    name = "diffview-git", description = ["..."],
    mixinStandardHelpOptions = true
)
class Entrypoint : Runnable {

    @Option(names = ["-v", "--verbose"], description = ["..."])
    private var verbose: Boolean = false

    override fun run() {
        val str: String = Ansi.ON.string("@|bold,green,underline Hello, colored world!|@")
        println(str)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            PicocliRunner.run(Entrypoint::class.java, *args)
        }
    }
}
