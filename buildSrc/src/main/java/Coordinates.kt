object PluginCoordinates {
    const val ID = "io.github.philippheuer.setup"
    const val GROUP = "io.github.philippheuer.gradle-setup-plugin"
    const val VERSION = "1.0.0"
    const val IMPLEMENTATION_CLASS = "io.github.philippheuer.gradleprojectsetup.ProjectSetupPlugin"
}

object PluginBundle {
    const val VCS = "https://github.com/PhilippHeuer/gradle-projectsetup"
    const val WEBSITE = "https://github.com/PhilippHeuer/gradle-projectsetup"
    const val DESCRIPTION = "A gradle plugin to share best-practice project configurations"
    const val DISPLAY_NAME = "Project Setup Plugin"
    val TAGS = listOf(
        "setup",
        "buildscript",
        "best-practice"
    )
}