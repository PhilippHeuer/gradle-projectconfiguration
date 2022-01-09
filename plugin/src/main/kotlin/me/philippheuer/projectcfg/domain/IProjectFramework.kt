package me.philippheuer.projectcfg.domain

interface IProjectFramework {
    fun value(): String

    fun valueEquals(other: Any?): Boolean {
        if (other is IProjectFramework) {
            return value() == other.value()
        }

        return this == other
    }
}