package com.mmgsoft.modules.libs.models

import androidx.annotation.Keep
import com.android.billingclient.api.ProductDetails

@Keep
data class PurchaseProductDetails(
    var isBuy: Boolean = false,
    val productDetails: ProductDetails
)
