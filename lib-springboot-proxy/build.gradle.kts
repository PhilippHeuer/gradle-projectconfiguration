import me.philippheuer.projectcfg.domain.*

plugins {
    kotlin("jvm")
    id("me.philippheuer.configuration") version "+"
}

// setup
projectConfiguration {
    language.set(ProjectLanguage.KOTLIN)
    type.set(ProjectType.LIBRARY)
    framework.set(ProjectFramework.SPRINGBOOT)
    javaVersion.set(JavaVersion.VERSION_1_8)

    artifactGroupId.set("me.philippheuer.projectcfg.lib")
}
