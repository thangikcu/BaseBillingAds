package com.mmgsoft.modules.libs

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.mmgsoft.modules.libs.ads.AdsManager
import com.mmgsoft.modules.libs.amzbiling.AppConstant
import com.mmgsoft.modules.libs.billing.GoogleBillingManager
import com.mmgsoft.modules.libs.data.local.db.AppDatabase
import com.mmgsoft.modules.libs.data.local.db.AppDbHelper
import com.mmgsoft.modules.libs.helpers.AdsPrefs
import com.mmgsoft.modules.libs.helpers.BillingType
import com.mmgsoft.modules.libs.manager.BackgroundManager
import com.mmgsoft.modules.libs.models.BillingMapper
import com.mmgsoft.modules.libs.models.BillingMapper.Companion.mapping
import com.mmgsoft.modules.libs.utils.AdsComponentConfig
import kotlin.properties.Delegates

class AdsComponents private constructor(
    val application: Application,
    private val adsComponentConfig: AdsComponentConfig
) {

    companion object {

        @JvmStatic
        var INSTANCE: AdsComponents by Delegates.notNull()

        @JvmStatic
        fun initialize(application: Application, adsComponentConfig: AdsComponentConfig) {
            synchronized(this) {
                INSTANCE = AdsComponents(application, adsComponentConfig)

                BackgroundManager.attach(application)

                if (adsComponentConfig.billingType == BillingType.GOOGLE) {
                    val billingId = INSTANCE.billingId
                    GoogleBillingManager.init(
                        application,
                        billingId.toList(),
                        listOf((billingId as BillingId.Google).noneConsume1)
                    )
                }
            }
        }
    }

    val billingId: BillingId by lazy {
        val prefixAppId = application.packageName.removePrefix("com.")

        when (adsComponentConfig.billingType) {
            BillingType.GOOGLE -> BillingId.Google(prefixAppId)
            BillingType.AMAZON -> BillingId.Amazon(prefixAppId)
        }
    }

    val billingMappers: List<BillingMapper> by lazy {
        val productIds = arrayListOf(
            billingId.consume1,
            billingId.consume2,
            billingId.consume3,
        ).apply {
            addAll(
                when (val billingId = billingId) {
                    is BillingId.Google -> {
                        listOf(
                            billingId.noneConsume1
                        )
                    }
                    is BillingId.Amazon -> {
                        listOf(
                            billingId.entitleDiscount1
                        )
                    }
                }
            )
        }

        productIds.zip(adsComponentConfig.refundMoneys).map { it.first mapping it.second }
    }

    internal val adsPrefs by lazy { AdsPrefs(application) }

    val adsManager by lazy {
        AdsManager().initAdsManager(
            application,
            adsComponentConfig.testDevices.toMutableList()
        )
    }

    val dbHelper by lazy {
        val appDatabase: AppDatabase = Room.databaseBuilder(
            application,
            AppDatabase::class.java, AppConstant.DB_NAME
        ).fallbackToDestructiveMigration().build()
        AppDbHelper(appDatabase)
    }

    fun logDebugBillingInfo() {
        if (BuildConfig.DEBUG) {
            Log.i("BILLING_ID", billingId.toString())

            Log.i("BILLING_MAPPER", buildString {
                billingMappers.forEach {
                    appendLine("${it.productId}: ${it.refundMoney}")
                }
            })
        }
    }
}