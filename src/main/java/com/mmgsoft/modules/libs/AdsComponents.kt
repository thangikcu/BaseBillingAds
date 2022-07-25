package com.mmgsoft.modules.libs

import android.app.Application
import androidx.room.Room
import com.mmgsoft.modules.libs.ads.AdsManager
import com.mmgsoft.modules.libs.amzbiling.AppConstant
import com.mmgsoft.modules.libs.billing.GoogleBillingManager
import com.mmgsoft.modules.libs.data.local.db.AppDatabase
import com.mmgsoft.modules.libs.data.local.db.AppDbHelper
import com.mmgsoft.modules.libs.helpers.AdsPrefs
import com.mmgsoft.modules.libs.helpers.BillingType
import com.mmgsoft.modules.libs.manager.BackgroundManager
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

                INSTANCE.billingId.toList().map {

                }
                listOf<String>().map {

                }

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

}