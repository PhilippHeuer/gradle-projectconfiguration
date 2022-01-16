package me.philippheuer.projectcfg.domain

interface IProjectLibrary {
    fun value(): String

    fun valueEquals(other: Any?): Boolean {
        if (other is IProjectLibrary) {
            return value() == other.value()
        }

        return this == other
    }
}