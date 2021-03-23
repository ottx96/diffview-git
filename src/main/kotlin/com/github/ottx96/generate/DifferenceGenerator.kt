package com.github.ottx96.generate

import com.github.ottx96.parse.DifferenceView

enum class GenerationFormat {
    HTML
}

class DifferenceGenerator(val data: DifferenceView) {

    fun generate(format: GenerationFormat = GenerationFormat.HTML) {
        when(format) {
            GenerationFormat.HTML -> return generateHTML()
        }
    }

    private fun generateHTML() {

    }

}