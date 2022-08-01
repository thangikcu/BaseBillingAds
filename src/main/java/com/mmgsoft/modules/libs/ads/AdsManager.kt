package com.mmgsoft.modules.libs.ads

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.view.View
import android.widget.FrameLayout
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.mmgsoft.modules.libs.AdsComponents
import com.mmgsoft.modules.libs.BuildConfig
import com.mmgsoft.modules.libs.dialog.PrepareLoadingAdsDialog
import com.mmgsoft.modules.libs.etx.gone
import com.mmgsoft.modules.libs.etx.visible
import com.mmgsoft.modules.libs.utils.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AdsManager {
    var currentClicked = 0
    var numberOfClicksToShowAds = 2

    /**
     * Tìm DeviceID bằng cách check log dưới đây
     * I/Ads: Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("1A2EFF3812BA12129322AFC444A1EABE") to get test ads on this device.
     */
    fun initAdsManager(ctx: Context, testDevices: MutableList<String>): AdsManager {
        testDevices.add(AdRequest.DEVICE_ID_EMULATOR)
        MobileAds.initialize(ctx)
        val requestConfiguration = RequestConfiguration.Builder()
            .setTestDeviceIds(testDevices)
            .build()
        MobileAds.setRequestConfiguration(requestConfiguration)

        return this
    }

    private fun showInterstitialWithCount(act: Activity, closeAd: () -> Unit) {
        if (AdsComponents.INSTANCE.adsPrefs.isBillingInterstitial || !NetworkUtils.isNetworkAvailable(
                act
            )
        ) {
            closeAd.invoke()
            return
        }
        val dialog = createDialogFullScreen(act)
        dialog.show()

        currentClicked++
        if (currentClicked >= numberOfClicksToShowAds) {
            forceShowInterstitial(act) {
                dialog.hide()
                closeAd.invoke()
            }
            currentClicked = 0
        } else {
            dialog.hide()
            closeAd.invoke()
        }
    }

    @JvmOverloads
    fun forceShowInterstitial(act: Activity, adClosed: (() -> Unit)? = null) {
        if (BuildConfig.DEBUG || BuildConfig.ROBO_TEST) {
            /**
             * Bypass show interstitial for Debug
             */
            adClosed?.invoke()
            return
        }

        if (AdsComponents.INSTANCE.adsPrefs.isBillingInterstitial
            || !NetworkUtils.isNetworkAvailable(act)
        ) {
            adClosed?.invoke()
            return
        }

        val dialog = createDialogFullScreen(act)
        dialog.show()

        var isTimeout = false
        var loadSuccess = false

        CoroutineScope(Dispatchers.Main).launch {
            delay(5000)

            if (!loadSuccess) {
                isTimeout = true
                adClosed?.invoke()
                dialog.dismiss()
            }
        }

        InterstitialAd.load(
            act,
            BuildConfig.INTERSTITIAL_AD_UNIT_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    loadSuccess = true
                    if (!isTimeout) {
                        dialog.dismiss()
                        adClosed?.invoke()
                    }
                }

                override fun onAdLoaded(p0: InterstitialAd) {
                    super.onAdLoaded(p0)
                    loadSuccess = true
                    if (!isTimeout) {
                        p0.fullScreenContentCallback = object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent()
                                dialog.dismiss()
                                adClosed?.invoke()
                            }
                        }
                        p0.show(act)
                    }
                }
            })
    }

    private fun createDialogFullScreen(ctx: Context): Dialog = PrepareLoadingAdsDialog(ctx)

    fun showAdModBanner(
        context: Context,
        adContainer: FrameLayout,
        shimmerFrameLayout: ShimmerFrameLayout,
        onLoadFailed: () -> Unit
    ) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            onLoadFailed.invoke()
            return
        }
        shimmerFrameLayout.visible()
        shimmerFrameLayout.startShimmer()

        val adView = AdView(context)
        adView.adUnitId = BuildConfig.BANNER_AD_UNIT_ID
        adContainer.addView(adView)
        val adSize: AdSize = getAdBannerSize(context)
        adView.setAdSize(adSize)
        adView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        adView.loadAd(AdRequest.Builder().build())
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                shimmerFrameLayout.stopShimmer()
                shimmerFrameLayout.gone()
                adContainer.visible()
                // Code to be executed when an ad finishes loading.
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                shimmerFrameLayout.stopShimmer()
                adContainer.gone()
                shimmerFrameLayout.gone()
                onLoadFailed.invoke()
            }
        }
    }

    private fun getAdBannerSize(ctx: Context): AdSize {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        val displayMetrics = Resources.getSystem().displayMetrics
        val widthPixels = displayMetrics.widthPixels.toFloat()
        val density = displayMetrics.density
        val adWidth = (widthPixels / density).toInt()

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getPortraitAnchoredAdaptiveBannerAdSize(ctx, adWidth)
    }

    fun rewardedAd(ctx: Context, adsUnitId: String) {
        RewardedAd.load(
            ctx,
            adsUnitId,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {

            })
    }
}
