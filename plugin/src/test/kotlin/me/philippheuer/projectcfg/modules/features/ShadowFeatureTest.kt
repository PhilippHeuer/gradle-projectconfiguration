package me.philippheuer.projectcfg.modules.features

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import me.philippheuer.projectcfg.util.PluginTestUtils
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ShadowFeatureTest {

    @Test
    fun `shadow plugin is being applied`() {
        val (project, config) = PluginTestUtils.getJavaProject()
        config.shadow.set(true)
        project.pluginManager.apply("com.gradleup.shadow")

        assertNotNull(project.pluginManager.findPlugin("com.gradleup.shadow"), "com.gradleup.shadow plugin was not applied")
        project.afterEvaluate {
            assertTrue(project.tasks.withType(ShadowJar::class.java).size > 0, "shadowJar task should be present")
        }
    }

}