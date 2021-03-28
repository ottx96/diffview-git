package com.github.ottx96

import com.github.ottx96.test.base.JUnitTestBase
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File

class EntrypointTest: JUnitTestBase() {

    @Test
    fun `test missing argument`() {
        assertThrows<RuntimeException> { runApplication() }
    }

    @Test
    fun `test without parameters`() {
        val output = runApplication("README.md")
        assertTrue(output.contains("""Everything finished! Exiting .."""))
    }

    @Test
    fun `test parameter -v`() {
        val output = runApplication("-v", "README.md")
        assertTrue(output.contains(Regex("""Verbose:[ ]+true""")))
    }

    @Test
    fun `test parameter --verbose`() {
        val output = runApplication("--verbose", "README.md")
        assertTrue(output.contains(Regex("""Verbose:[ ]+true""")))
    }

    @Test
    fun `test parameter --debug`() {
        val output = runApplication("--debug", "README.md")
        assertTrue(output.contains(Regex("""Verbose:[ ]+false""")))
        assertTrue(output.contains(Regex("""Debug:[ ]+true""")))
    }

    @Test
    fun `test parameter --action DIFF one file only`() {
        assertThrows<java.lang.RuntimeException> {
            runApplication("--action", "DIFF", "README.md")
        }
    }

    @Test
    fun `test parameter --action DIFF`() {
        val output = runApplication("--debug", "--action", "DIFF", "README.md", "README.md")
        assertTrue(output.contains(Regex("""Action:[ ]+DIFF""")))
    }

    @Test
    fun `test parameter --action LOG`() {
        val output = runApplication("--debug", "--action", "LOG", "README.md")
        assertTrue(output.contains(Regex("""Action:[ ]+LOG""")))
    }

    @Test
    fun `test parameter --action not given`() {
        val output = runApplication("--debug", "README.md")
        assertTrue(output.contains(Regex("""Action:[ ]+LOG""")))
    }

    @Test
    fun testUsage() {
        val output = runApplication("--help")
        assertTrue {output.contains("Usage: ")}
    }

    @Test
    fun `test parameter --directory-out`() {
        val outputDir = File("build/test/dir-out/")
        val output = runApplication("--debug", "--directory-out", outputDir.absolutePath, "README.md")
        assertTrue { output.contains(outputDir.absolutePath) }
        assertTrue { outputDir.exists() && outputDir.list()?.isNotEmpty()?:false }
    }

    @Test
    fun `test parameter --directory-out missing`() {
        val defaultOutputDir = File("diffview-generated")
        val output = runApplication("--debug", "README.md")
        assertTrue { output.contains("diffview-generated") }
        assertTrue { defaultOutputDir.exists() && defaultOutputDir.list()?.isNotEmpty()?:false }
    }

    @Test
    fun `test parameter --repository`() {
        val repo = File(".")
        val output = runApplication("--debug", "--repository", repo.absolutePath, "README.md")
        assertTrue { output.contains(repo.absolutePath) }
    }

    @Test
    fun `test parameter --no-original-extension`() {
        val outputDir = File("build/test/out/")
        runApplication("--debug", "--no-original-extension", "-o", outputDir.absolutePath, "README.md")
        assertTrue { outputDir.listFiles()?.any { it.name == "README.html" }?:false }
    }

    @Test
    fun `test parameter --no-original-extension missing`() {
        val outputDir = File("build/test/out/")
        runApplication("--debug", "-o", outputDir.absolutePath, "README.md")
        assertTrue { outputDir.listFiles()?.any { it.name == "README.md.html" }?:false }
    }

}
