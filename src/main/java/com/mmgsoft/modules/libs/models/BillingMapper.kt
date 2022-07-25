package com.mmgsoft.modules.libs.models

data class BillingMapper(
    val productId: String,
    var refundMoney: String
) {
    companion object {
        fun create(productId: String, price: String) =
            BillingMapper(productId, price)

        @JvmStatic
        infix fun String.mapping(price: String) = create(this, price)
    }
}