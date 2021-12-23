package me.philippheuer.projectcfg.util

import java.security.MessageDigest

class HashUtils {

    companion object {
        /**
         * hasDependency can be used to check if a project contains a certain dependency
         */
        fun stringToSha256Hash(input: String): String {
            val digest = MessageDigest.getInstance("SHA-1").digest(input.toByteArray())
            return digest.map { "%02x".format(it) }.joinToString("")
        }
    }
}