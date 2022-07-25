package com.mmgsoft.modules.libs.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.mmgsoft.modules.libs.AdsComponents
import com.mmgsoft.modules.libs.R
import com.mmgsoft.modules.libs.ads.AdsManager

class BannerAds @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    private var isAutoLoad = false
    private var isCloseAd = false

    init {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.BannerAds)
        isAutoLoad = typeArray.getBoolean(R.styleable.BannerAds_ba_autoLoad, false)
        typeArray.recycle()
        View.inflate(context, R.layout.view_banner_ads, this)
        initViews()
    }

    private fun initViews() {
        if(isAutoLoad && !isCloseAd) {
            loadBanner()
        }
    }

    fun load() {
        if(!isAutoLoad && !isCloseAd) {
            loadBanner()
        }
    }

    fun close() {
        isCloseAd = true
        this.visibility = View.GONE
    }

    private fun loadBanner() {
        if(AdsComponents.INSTANCE.adsPrefs.isBillingAdmobBanner) {
            close()
            return
        }
        AdsManager().showAdModBanner(context, findViewById(R.id.bannerContainer), findViewById(R.id.shimmerContainerBanner)) {
            this.visibility = View.GONE
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if(AdsComponents.INSTANCE.adsPrefs.isBillingAdmobBanner) {
            close()
        }
    }
}