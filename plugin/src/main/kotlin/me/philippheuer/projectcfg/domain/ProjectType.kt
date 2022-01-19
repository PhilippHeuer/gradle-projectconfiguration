package me.philippheuer.projectcfg.domain

enum class ProjectType : IProjectType {
    // app
    APP {
        override fun value(): String {
            return "APP"
        }
    },
    // batch
    BATCH {
        override fun value(): String {
            return "BATCH"
        }
    },
    // public library
    LIBRARY {
        override fun value(): String {
            return "LIBRARY"
        }
    },
    // internal library component
    LIBRARY_INTERNAL {
        override fun value(): String {
            return "LIBRARY"
        }
    },
    // default (unknown)
    DEFAULT {
        override fun value(): String {
            return "DEFAULT"
        }
    }
}