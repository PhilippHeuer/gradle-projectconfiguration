package me.philippheuer.projectcfg.features

import io.freefair.gradle.plugins.lombok.LombokExtension
import io.freefair.gradle.plugins.lombok.tasks.Delombok
import me.philippheuer.projectcfg.util.PluginTestUtils
import org.gradle.api.tasks.javadoc.Javadoc
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class LombokFeatureTest {

    @Test
    fun `lombok plugin is being applied to java projects`() {
        val (project, config) = PluginTestUtils.getJavaProject()

        val feature = LombokFeature(project, config)
        feature.configurePlugin(project, config)

        assertNotNull(project.pluginManager.findPlugin("io.freefair.lombok"), "lombok plugin was not applied")
        assertTrue(project.extensions.getByType(LombokExtension::class.java).disableConfig.get(), "lombok config file generation should be disabled")
        assertEquals(config.lombokVersion.get(), project.extensions.getByType(LombokExtension::class.java).version.get(), "lombok config file generation should be disabled")

        project.afterEvaluate {
            assertTrue(project.tasks.withType(Delombok::class.java).size > 0, "delombok task should be present")
        }
    }

    @Test
    fun `javadoc is using delombok as source`() {
        val (project, config) = PluginTestUtils.getJavaProject()

        val feature = LombokFeature(project, config)
        feature.configurePlugin(project, config)
        feature.configureJavadoc(project)

        project.afterEvaluate {
            val delombok = project.tasks.getByName("delombok")
            project.tasks.withType(Javadoc::class.java).forEach { task ->
                assertEquals(delombok, task.source(), "javadoc source should be delombok")
                assertEquals(delombok, task.dependsOn(), "javadoc dependsOn should be delombok")

            }
        }
    }
}