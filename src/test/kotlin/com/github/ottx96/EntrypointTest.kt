package com.github.ottx96

import com.github.ottx96.test.base.JUnitTestBase
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

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

}
