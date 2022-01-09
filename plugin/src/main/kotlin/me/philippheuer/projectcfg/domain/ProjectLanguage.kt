package me.philippheuer.projectcfg.domain

enum class ProjectLanguage : IProjectLanguage {
    JAVA {
        override fun value(): String {
            return "JAVA"
        }
    },
    KOTLIN {
        override fun value(): String {
            return "KOTLIN"
        }
    }
}