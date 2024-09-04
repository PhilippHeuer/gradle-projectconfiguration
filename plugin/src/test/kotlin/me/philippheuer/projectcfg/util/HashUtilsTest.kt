package me.philippheuer.projectcfg.util

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class HashUtilsTest {

    @Test
    fun testStringToSha256Hash() {
        val hash = HashUtils.stringToSha256Hash("test")
        assertEquals("a94a8fe5ccb19ba61c4c0873d391e987982fbbd3", hash)
    }

}
