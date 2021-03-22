package com.github.ottx96.logging

import picocli.CommandLine

enum class Styles {
    BOLD, FAINT, UNDERLINE, ITALIC, BLINK, REVERSE, RESET;

    infix fun withStyle(other: Styles): OutputFormatStyle {
        val res = OutputFormatStyle()
        res.styles += this
        res.styles += other
        return res
    }

    infix fun withColor(color: Colors): OutputFormat {
        return OutputFormat(OutputFormatStyle(listOf(this)), color = OutputFormatColor.fromColor(color))
    }
}

enum class Colors {
    BLACK, RED, GREEN, YELLOW, BLUE, MAGENTA, CYAN, WHITE
}

data class OutputFormatStyle(var styles: List<Styles> = mutableListOf()) {
    infix fun withStyle(other: Styles): OutputFormatStyle {
        styles += other
        return this
    }
    infix fun withColor(color: Colors): OutputFormat {
        return OutputFormat(this, color = OutputFormatColor.fromColor(color))
    }
}
data class OutputFormatColor(val color: Colors = Colors.WHITE) {
    companion object {
        fun fromColor(color: Colors): OutputFormatColor {
            return OutputFormatColor(color)
        }
    }
}

data class OutputFormat(val style: OutputFormatStyle = OutputFormatStyle(), val color: OutputFormatColor = OutputFormatColor()) {
    fun format(text: String): String {
        return CommandLine.Help.Ansi.ON.string(String.format("@|${style.styles.joinToString(separator = ","){it.name}},${color.color.name} %s|@", text))
    }
    fun println(text: String) {
        kotlin.io.println(format(text))
    }
}