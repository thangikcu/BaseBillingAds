package com.mmgsoft.modules.libs.models

import android.graphics.Bitmap
import androidx.annotation.Keep
import com.google.gson.annotations.Expose

@Suppress("EqualsOrHashCode")
@Keep
data class Background(
    @Expose
    val price: String,
    @Expose
    val productId: String,
    @Expose
    val description: String,
    @Expose
    val backgroundPath: String,
    @Expose
    var isBuy: Boolean,
    var isSelected: Boolean = false,
    var isTriggerLoadBitmap: Boolean = false,
    var bm: Bitmap? = null
) {
    override fun equals(other: Any?): Boolean {
        return (other as? Background)?.let {
            productId == productId
        } ?: false
    }
}