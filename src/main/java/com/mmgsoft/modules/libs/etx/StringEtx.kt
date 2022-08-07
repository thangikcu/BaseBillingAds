@file:Suppress("SameParameterValue")

package com.mmgsoft.modules.libs.etx

import com.mmgsoft.modules.libs.helpers.AmazonCurrency
import com.mmgsoft.modules.libs.models.Currency
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

internal fun String.toCurrency() = Currency(this)

internal fun AmazonCurrency.toCurrency() = Currency(this.c)

fun String.md5() = encrypt(this, "MD5")

private fun encrypt(string: String?, type: String): String {
    if (string.isNullOrEmpty()) {
        return ""
    }
    val md5: MessageDigest
    return try {
        md5 = MessageDigest.getInstance(type)
        val bytes = md5.digest(string.toByteArray())
        bytes2Hex(bytes)
    } catch (e: NoSuchAlgorithmException) {
        ""
    }
}

private fun bytes2Hex(bts: ByteArray): String {
    var des = ""
    var tmp: String
    for (i in bts.indices) {
        tmp = Integer.toHexString(bts[i].toInt() and 0xFF)
        if (tmp.length == 1) {
            des += "0"
        }
        des += tmp
    }
    return des
}