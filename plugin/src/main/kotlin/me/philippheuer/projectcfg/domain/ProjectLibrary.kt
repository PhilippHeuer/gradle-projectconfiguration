package me.philippheuer.projectcfg.domain

enum class ProjectLibrary : IProjectLibrary {
    TEST_MOCKITO {
        override fun value(): String {
            return "TEST_MOCKITO"
        }
    },
    TEST_AWAITABILITY {
        override fun value(): String {
            return "TEST_AWAITABILITY"
        }
    },
    EVENTS4J {
        override fun value(): String {
            return "EVENTS4J"
        }
    },
}