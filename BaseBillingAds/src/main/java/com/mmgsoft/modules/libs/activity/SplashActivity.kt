package com.mmgsoft.modules.libs.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import com.mmgsoft.modules.libs.AdsComponents
import com.mmgsoft.modules.libs.databinding.ActivitySplashLayoutBinding
import com.mmgsoft.modules.libs.helpers.BillingType
import com.mmgsoft.modules.libs.utils.AdsComponentConfig.setBillingType


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashLayoutBinding
    private lateinit var fullscreenContent: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AdsComponents.initialize(
            application,
            setBillingType(BillingType.GOOGLE)
                .setRefundMoneys("3000", "5000", "7000", "13000")
        )

        /**
         * Log thông tin BILLING chỉ với DEBUG mode
         */
        AdsComponents.INSTANCE.logDebugBillingInfo()

        AdsComponents.INSTANCE.adsManager.forceShowInterstitial(this) {
            startActivity(supportParentActivityIntent)
            finish()
        }

        binding = ActivitySplashLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fullscreenContent = binding.fullscreenContent
        setFullscreen()
    }

    @Suppress("DEPRECATION")
    private fun setFullscreen() {
        // Hide UI first
        supportActionBar?.hide()

        // Delayed removal of status and navigation bar
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