package me.philippheuer.projectcfg.modules.features

import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.util.applyPlugin
import me.philippheuer.projectcfg.util.isRootProjectWithoutSubprojectsOrSubproject
import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

/**
 * JaCoCo - Java Code Coverage
 */
class JacocoFeature(override var ctx: IProjectContext) : PluginModule {
    override fun check(): Boolean {
        return ctx.project.isRootProjectWithoutSubprojectsOrSubproject() && ctx.isProjectSourceModule()
    }

    override fun run() {
        configureJacoco(ctx)
    }

    companion object {
        private fun configureJacoco(ctx: IProjectContext) {
            // plugin
            ctx.project.applyPlugin("jacoco")

            // test task finalizedBy jacocoTestReport
            ctx.project.tasks.withType(Test::class.java).configureEach { test ->
                // generate test report after running tests
                test.finalizedBy("jacocoTestReport")
            }

            // jacoco extension config
            ctx.project.extensions.configure(JacocoPluginExtension::class.java) {
                it.toolVersion = ctx.config.jacocoVersion.get()
            }

            // jacocoTestReport task
            ctx.project.tasks.withType(JacocoReport::class.java).configureEach { report ->
                // require xml report
                report.reports.xml.required.set(true)

                // depend on test
                report.dependsOn("test")
            }
        }
    }
}
