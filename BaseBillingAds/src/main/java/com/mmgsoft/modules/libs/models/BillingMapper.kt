package com.mmgsoft.modules.libs.models

data class BillingMapper(
    val productId: String,
    var price: String
) {
    companion object {
        fun create(productId: String, price: String) =
            BillingMapper(productId, price)

        @JvmStatic
        infix fun String.mapping(price: String) = create(this, price)
    }
}