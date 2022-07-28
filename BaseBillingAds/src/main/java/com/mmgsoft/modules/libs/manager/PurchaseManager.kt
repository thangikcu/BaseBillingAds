package com.mmgsoft.modules.libs.manager

import android.content.Context
import com.mmgsoft.modules.libs.activity.GooglePurchaseActivity
import com.mmgsoft.modules.libs.amzbiling.AmazonIapActivity
import com.mmgsoft.modules.libs.helpers.AmazonScreenType
import com.mmgsoft.modules.libs.helpers.BillingType
import com.mmgsoft.modules.libs.utils.AdsComponentConfig

object PurchaseManager {
    fun purchase(ctx: Context) {
        if (AdsComponentConfig.billingType == BillingType.AMAZON) {
            AmazonIapActivity.open(ctx, AmazonScreenType.SUBSCRIPTION)
        } else {
            GooglePurchaseActivity.open(ctx)
        }
    }

    fun purchaseWithRefund(ctx: Context) {
        if (AdsComponentConfig.billingType == BillingType.AMAZON) {
            AmazonIapActivity.open(ctx, AmazonScreenType.BUY_GOLD)
        } else {
            GooglePurchaseActivity.open(ctx, true)
        }
    }
}