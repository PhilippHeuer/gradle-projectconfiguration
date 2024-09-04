package me.philippheuer.projectcfg.util

import java.security.MessageDigest

class HashUtils {

    companion object {
        /**
         * Converts a string to an SHA-256 hash
         */
        fun stringToSha256Hash(input: String): String {
            val digest = MessageDigest.getInstance("SHA-1").digest(input.toByteArray())
            return digest.joinToString("") { "%02x".format(it) }
        }
    }
}
