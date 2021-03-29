package com.github.ottx96.test.base

import com.github.ottx96.Entrypoint
import io.micronaut.configuration.picocli.PicocliRunner
import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.Environment
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.lang.RuntimeException

open class JUnitTestBase {

    companion object {

        fun runApplication(vararg args: String): String {
            val stdout = ByteArrayOutputStream()
            val errout = ByteArrayOutputStream()

            System.setOut(PrintStream(stdout))
            System.setErr(PrintStream(errout))

            ApplicationContext.builder(Environment.TEST, Environment.CLI).build().use {
                PicocliRunner.run(Entrypoint::class.java, it, *args)
            }

            val err = "$errout"
            val std = "$stdout"
            if(err.isNotBlank()) throw RuntimeException(err)

            return std
        }

    }

}