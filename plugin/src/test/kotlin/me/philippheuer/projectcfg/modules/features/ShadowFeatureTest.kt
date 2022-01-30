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
        ShadowFeature.applyPlugin(project)

        assertNotNull(project.pluginManager.findPlugin("com.github.johnrengelman.shadow"), "com.github.johnrengelman.shadow plugin was not applied")
        project.afterEvaluate {
            assertTrue(project.tasks.withType(ShadowJar::class.java).size > 0, "shadowJar task should be present")
        }
    }

}
