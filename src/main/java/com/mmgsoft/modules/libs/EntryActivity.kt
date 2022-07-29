package com.mmgsoft.modules.libs

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import com.mmgsoft.modules.libs.databinding.ActivitySplashLayoutBinding
import com.mmgsoft.modules.libs.helpers.BillingAdsHelper
import com.mmgsoft.modules.libs.utils.AdsComponentConfig.setBillingType

@SuppressLint("CustomSplashScreen")
class EntryActivity : AppCompatActivity() {

    companion object {
        private const val BILLING_ADS = "BillingAds"
    }

    private lateinit var binding: ActivitySplashLayoutBinding
    private lateinit var fullscreenContent: View
    private lateinit var activityLifeCycleCallbacks: Application.ActivityLifecycleCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fullscreenContent = binding.fullscreenContent
        setFullscreen()

        AdsComponents.initialize(
            application,
            setBillingType(BuildConfig.BILLING_TYPE)
                .setRefundMoneys(BuildConfig.REFUND_MONEY)
        )

        AdsComponents.INSTANCE.adsManager.forceShowInterstitial(this) {
            application.registerActivityLifecycleCallbacks(activityLifeCycleCallbacks)
            startActivity(supportParentActivityIntent!!.apply { putExtra(BILLING_ADS, true) })
            finish()
        }

        activityLifeCycleCallbacks = object : Application.ActivityLifecycleCallbacks {

            override fun onActivityResumed(activity: Activity) {
                application.unregisterActivityLifecycleCallbacks(activityLifeCycleCallbacks)
                if (activity.intent?.getBooleanExtra(BILLING_ADS, false) == true
                    && activity is AppCompatActivity
                ) {
                    BillingAdsHelper.inject(activity)
                }
            }

            override fun onActivityCreated(activity: Activity, bunddle: Bundle?) = Unit
            override fun onActivityStarted(p0: Activity) = Unit
            override fun onActivityPaused(p0: Activity) = Unit
            override fun onActivityStopped(p0: Activity) = Unit
            override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) = Unit
            override fun onActivityDestroyed(p0: Activity) = Unit
        }

        /**
         * Log thông tin BILLING chỉ với DEBUG mode
         */
        AdsComponents.INSTANCE.logDebugBillingInfo()
    }

    @Suppress("DEPRECATION")
    private fun setFullscreen() {
        if (Build.VERSION.SDK_INT >= 30) {
            fullscreenContent.windowInsetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            fullscreenContent.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
    }
}