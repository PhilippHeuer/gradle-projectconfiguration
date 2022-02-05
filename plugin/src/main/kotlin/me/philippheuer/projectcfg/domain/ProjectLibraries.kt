package me.philippheuer.projectcfg.domain

enum class ProjectLibraries : IProjectLibrary {
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
    SENTRYIO {
        override fun value(): String {
            return "SENTRYIO"
        }
    },
    JACKSON {
        override fun value(): String {
            return "JACKSON"
        }
    };

    companion object {
        fun default(): List<IProjectLibrary> {
            return listOf(TEST_MOCKITO, TEST_AWAITABILITY)
        }
    }
}