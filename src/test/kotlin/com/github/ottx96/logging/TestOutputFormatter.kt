package com.github.ottx96.logging

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import picocli.CommandLine

class TestOutputFormatter {

    @Test
    fun testFormatting() {
        assertTrue {(Styles.BOLD withStyle Styles.ITALIC withColor Colors.RED).format("TEST") == CommandLine.Help.Ansi.ON.string("@|BOLD,ITALIC,RED TEST|@")}
        assertTrue {(Styles.BOLD withColor Colors.RED).format("TEST") == CommandLine.Help.Ansi.ON.string("@|BOLD,RED TEST|@")}
        assertTrue {(Styles.BOLD withStyle Styles.ITALIC withColor Colors.BLACK).format("TEST") == CommandLine.Help.Ansi.ON.string("@|BOLD,ITALIC,BLACK TEST|@")}
        assertTrue {(Styles.BOLD withStyle Styles.ITALIC withColor Colors.NONE).format("TEST") == CommandLine.Help.Ansi.ON.string("@|BOLD,ITALIC TEST|@")}
        assertTrue {(Styles.ITALIC withColor Colors.WHITE).format("TEST") == CommandLine.Help.Ansi.ON.string("@|ITALIC,WHITE TEST|@")}
    }

}