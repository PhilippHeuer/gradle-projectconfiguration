package me.philippheuer.projectcfg.domain

interface IProjectType {
    fun value(): String

    fun valueEquals(other: Any?): Boolean {
        if (other is IProjectType) {
            return value() == other.value()
        }

        return this == other
    }
}