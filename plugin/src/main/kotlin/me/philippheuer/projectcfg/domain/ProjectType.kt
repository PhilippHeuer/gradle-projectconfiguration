package me.philippheuer.projectcfg.domain

enum class ProjectType : IProjectType {
    APP {
        override fun value(): String {
            return "APP"
        }
    },
    LIBRARY {
        override fun value(): String {
            return "LIBRARY"
        }
    },
    DEFAULT {
        override fun value(): String {
            return "DEFAULT"
        }
    }
}