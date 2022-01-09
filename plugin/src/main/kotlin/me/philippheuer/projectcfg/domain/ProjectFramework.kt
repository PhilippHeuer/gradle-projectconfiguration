package me.philippheuer.projectcfg.domain

enum class ProjectFramework : IProjectFramework {
    NONE {
        override fun value(): String {
            return "NONE"
        }
    },
    SPRINGBOOT {
        override fun value(): String {
            return "SPRINGBOOT"
        }
    },
    QUARKUS {
        override fun value(): String {
            return "QUARKUS"
        }
    }
}