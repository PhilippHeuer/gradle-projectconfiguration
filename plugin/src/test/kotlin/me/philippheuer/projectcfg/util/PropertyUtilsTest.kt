package me.philippheuer.projectcfg.util

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertContains
import kotlin.test.assertEquals

class PropertyUtilsTest {

    @Test
    fun testProcessPropertie() {
        // prepare temp property file
        val tempFile = File.createTempFile("test", ".properties")
        tempFile.deleteOnExit()

        // example content
        tempFile.writeText("example.hello=world")

        // process properties
        PropertyUtils.processProperties(tempFile, mapOf(
            "example.hello" to "mum",
            "example.property" to "example2",
        ), false)

        // assert
        val result = tempFile.readText()
        assertContains(result, "example.hello=world")
        assertContains(result, "example.property=example2")
    }

    @Test
    fun testProcessPropertiesOverride() {
        // prepare temp property file
        val tempFile = File.createTempFile("test", ".properties")
        tempFile.deleteOnExit()

        // example content
        tempFile.writeText("example.hello=world")

        // process properties
        PropertyUtils.processProperties(tempFile, mapOf(
            "example.hello" to "mum",
            "example.property" to "example2",
        ), true)

        // assert
        val result = tempFile.readText()
        assertContains(result, "example.hello=mum")
        assertContains(result, "example.property=example2")
    }

    @Test
    fun testProcessYamlProperties() {
        // prepare temp property file
        val tempFile = File.createTempFile("test", ".yaml")
        tempFile.deleteOnExit()

        // example content
        tempFile.writeText("""
        |example:
        |  hello: world
        """.trimMargin())

        // process properties
        PropertyUtils.processYamlProperties(tempFile, mapOf(
            "example.hello" to "mum",
            "example.property" to "example2",
        ), false)

        // assert
        val result = tempFile.readText()
        assertEquals("""
        |example:
        |  hello: "world"
        |  property: "example2"
        """.trimMargin().trim(), result.trim())
    }

    @Test
    fun testProcessYamlPropertiesOverride() {
        // prepare temp property file
        val tempFile = File.createTempFile("test", ".yaml")
        tempFile.deleteOnExit()

        // example content
        tempFile.writeText("""
        |example:
        |  hello: world
        """.trimMargin())

        // process properties
        PropertyUtils.processYamlProperties(tempFile, mapOf(
            "example.hello" to "mum",
            "example.property" to "example2",
        ), true)

        // assert
        val result = tempFile.readText()
        assertEquals("""
        |example:
        |  hello: "mum"
        |  property: "example2"
        """.trimMargin().trim(), result.trim())
    }
}
