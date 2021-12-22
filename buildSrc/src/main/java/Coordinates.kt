object PluginCoordinates {
    const val ID = "me.philippheuer.projectcfg"
    const val GROUP = "me.philippheuer.projectcfg"
    const val VERSION = "1.0.0"
    const val IMPLEMENTATION_CLASS = "me.philippheuer.projectcfg.ProjectConfigurationPlugin"
}

object PluginBundle {
    const val VCS = "https://github.com/PhilippHeuer/gradle-projectconfiguration"
    const val WEBSITE = "https://github.com/PhilippHeuer/gradle-projectconfiguration"
    const val DESCRIPTION = "A gradle plugin to share best-practice project configurations easily"
    const val DISPLAY_NAME = "Project Configuration Plugin"
    val TAGS = listOf(
        "setup",
        "buildscript",
        "best-practice"
    )
}