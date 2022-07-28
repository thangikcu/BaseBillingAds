package com.mmgsoft.modules.libs.activity

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mmgsoft.modules.libs.R
import com.mmgsoft.modules.libs.base.BaseActivity
import com.mmgsoft.modules.libs.billing.GoogleBillingManager
import com.mmgsoft.modules.libs.etx.launchActivity
import com.mmgsoft.modules.libs.etx.setStatusBarColor
import com.mmgsoft.modules.libs.etx.setStatusBarTextColorDark
import com.mmgsoft.modules.libs.helpers.ActionBarTheme
import com.mmgsoft.modules.libs.helpers.BillingLoadingState
import com.mmgsoft.modules.libs.helpers.BillingLoadingStateEvent
import com.mmgsoft.modules.libs.helpers.StateAfterBuy
import com.mmgsoft.modules.libs.utils.AdsComponentConfig
import com.mmgsoft.modules.libs.widgets.PurchaseView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class GooglePurchaseActivity : BaseActivity() {
    private val tvTitle by lazy {
        findViewById<TextView>(R.id.tvPurchaseTitle)
    }

    private val imBack by lazy {
        findViewById<ImageView>(R.id.imBack)
    }

    private val rlHeader by lazy {
        findViewById<RelativeLayout>(R.id.rlHeader)
    }

    private val purchaseView by lazy {
        findViewById<PurchaseView>(R.id.purchaseView)
    }

    private val frBillingLoading by lazy {
        findViewById<FrameLayout>(R.id.frBillingLoading)
    }

    private val rvPurchase by lazy {
        findViewById<RecyclerView>(R.id.rvPurchase)
    }

    companion object {
        const val EXTRAS_COLOR_TOOLBAR = "EXTRAS_COLOR_TOOLBAR"
        const val EXTRAS_COLOR_TITLE_TOOLBAR = "EXTRAS_COLOR_TITLE_TOOLBAR"
        const val EXTRAS_THEME_STYLE = "EXTRAS_THEME_STYLE"
        const val EXTRAS_HEADER_TITLE = "EXTRAS_HEADER_TITLE"
        const val EXTRAS_LAYOUT_SUBS = "EXTRAS_LAYOUT_SUBS"
        const val EXTRAS_LAYOUT_IN_APP = "EXTRAS_LAYOUT_IN_APP"
        const val EXTRAS_IS_BUY_GOLD = "EXTRAS_IS_BUY_GOLD"

        fun open(ctx: Context, isBuyGold: Boolean = false) {
            ctx.launchActivity<GooglePurchaseActivity> {
                putExtra(EXTRAS_IS_BUY_GOLD, isBuyGold)
                putExtra(EXTRAS_THEME_STYLE, ActionBarTheme.DARK_MODE.s)
                putExtra(EXTRAS_COLOR_TOOLBAR, R.color.white)
                putExtra(EXTRAS_COLOR_TITLE_TOOLBAR, R.color.black)
                putExtra(EXTRAS_LAYOUT_SUBS, R.layout.item_purchase_subs_default)
                putExtra(EXTRAS_LAYOUT_IN_APP, R.layout.item_purchase_inapp_default)
            }
        }

    }

    override val layoutResId: Int
        get() = R.layout.activity_libs_purchase

    override fun initViews() {
        registerEvents()
        setupHeaderColor()
        setupHeaderTitle()
        setupPurchaseLayout()
        observablePurchase()
        initActions()
        reInitPurchaseItems()
    }

    private fun observablePurchase() {
        GoogleBillingManager.listAvailableObserver.observe(this) {
            reInitPurchaseItems()
            if (GoogleBillingManager.state == StateAfterBuy.REMOVE) {
                rvPurchase.postInvalidate()
            }
        }
    }

    private fun reInitPurchaseItems() {
        val isBuyGold = intent?.getBooleanExtra(EXTRAS_IS_BUY_GOLD, false) ?: false

        var listAvailable = GoogleBillingManager.listAvailable

        if (isBuyGold) {
            listAvailable = listAvailable.filter {
                it.productDetails.productId.contains(AdsComponentConfig.consumeKey)
            }.toMutableList()
        }

        purchaseView.setup(listAvailable) { productDetails ->
            GoogleBillingManager.launchBillingFlow(this@GooglePurchaseActivity, productDetails)
        }
    }

    private fun initActions() {
        imBack.setOnClickListener {
            finish()
        }
    }

    private fun setupHeaderColor() {
        val color = intent?.getIntExtra(EXTRAS_COLOR_TOOLBAR, R.color.colorAds) ?: R.color.colorAds
        val colorTitle =
            intent?.getIntExtra(EXTRAS_COLOR_TITLE_TOOLBAR, R.color.white) ?: R.color.white
        val theme = intent?.getStringExtra(EXTRAS_THEME_STYLE) ?: ActionBarTheme.LIGHT_MODE.s
        setStatusBarColor(color)
        rlHeader.setBackgroundColor(ContextCompat.getColor(this, color))
        tvTitle.setTextColor(ContextCompat.getColor(this, colorTitle))
        imBack.setColorFilter(ContextCompat.getColor(this, colorTitle))

        if (theme == ActionBarTheme.DARK_MODE.s) {
            setStatusBarTextColorDark()
        }
    }

    private fun setupHeaderTitle() {
        val title =
            intent?.getStringExtra(EXTRAS_HEADER_TITLE) ?: getString(R.string.purchase_title)
        tvTitle.text = title
    }

    private fun setupPurchaseLayout() {
        val resLayoutSubsId =
            intent?.getIntExtra(EXTRAS_LAYOUT_SUBS, R.layout.item_purchase_subs_default)
                ?: R.layout.item_purchase_subs_default
        val resLayoutInAppId =
            intent?.getIntExtra(EXTRAS_LAYOUT_IN_APP, R.layout.item_purchase_inapp_default)
                ?: R.layout.item_purchase_inapp_default

        purchaseView.setupResLayout(resLayoutSubsId, resLayoutInAppId)
    }

    @Subscribe
    fun onBillingLoadingStateChanged(event: BillingLoadingStateEvent) {
        runOnUiThread {
            frBillingLoading.visibility =
                if (event.state == BillingLoadingState.SHOW_LOADING) View.VISIBLE
                else View.GONE
        }
    }

    private fun registerEvents() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    private fun unRegisterEvents() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    override fun onDestroy() {
        unRegisterEvents()
        super.onDestroy()
    }
}