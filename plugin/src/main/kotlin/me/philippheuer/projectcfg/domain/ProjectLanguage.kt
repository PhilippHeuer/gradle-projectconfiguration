package me.philippheuer.projectcfg.domain

enum class ProjectLanguage : IProjectLanguage {
    DEFAULT {
        override fun value(): String {
            return "DEFAULT"
        }
    },
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