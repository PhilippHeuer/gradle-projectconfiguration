package me.philippheuer.projectcfg.modules.check

import me.philippheuer.projectcfg.util.PluginTestUtils
import org.gradle.api.plugins.quality.CheckstyleExtension
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CheckstyleFeatureTest {

    @Test
    fun `checkstyle plugin is being applied to java projects`() {
        val (project, config) = PluginTestUtils.getJavaProject()
        CheckstyleFeature.applyPlugin(project, config)

        assertNotNull(project.pluginManager.findPlugin("checkstyle"), "checkstyle plugin was not applied")
        assertEquals(0, project.extensions.getByType(CheckstyleExtension::class.java).maxErrors, "checkstyle maxErrors should be 0")
        assertEquals(0, project.extensions.getByType(CheckstyleExtension::class.java).maxWarnings, "checkstyle maxWarnings should be 0")
    }

    @Test
    fun `checkstyle plugin with custom ruleSet`() {
        val (project, config) = PluginTestUtils.getJavaProject()
        config.checkstyleRuleSet.set("google")
        CheckstyleFeature.applyPlugin(project, config)

        assertNotNull(project.pluginManager.findPlugin("checkstyle"), "checkstyle plugin was not applied")
        assertTrue(project.extensions.getByType(CheckstyleExtension::class.java).configFile.absolutePath.endsWith("build\\tmp\\checkstyle.xml"), "config path should end with ")
    }

}
