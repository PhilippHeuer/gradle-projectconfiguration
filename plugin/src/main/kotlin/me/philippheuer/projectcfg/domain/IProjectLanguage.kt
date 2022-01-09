package me.philippheuer.projectcfg.domain

interface IProjectLanguage {
    fun value(): String

    fun valueEquals(other: Any?): Boolean {
        if (other is IProjectLanguage) {
            return value() == other.value()
        }

        return this == other
    }
}