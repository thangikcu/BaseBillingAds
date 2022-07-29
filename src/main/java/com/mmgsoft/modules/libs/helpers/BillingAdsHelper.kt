package com.mmgsoft.modules.libs.helpers

import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ActionBarOverlayLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.mmgsoft.modules.libs.R
import com.mmgsoft.modules.libs.customview.BillingAdsToolbar
import com.mmgsoft.modules.libs.widgets.BannerAds

object BillingAdsHelper {
    private val myId = View.generateViewId()

    fun inject(activity: AppCompatActivity) {
        val decorView = activity.window.decorView
        if (decorView.findViewById<View>(myId) != null) {
            return
        }

        activity.addMenuProvider(BillingAdsMenuProvider(activity))

        val addToolbar = activity.supportActionBar == null

        val contentView: View = decorView.findViewById(android.R.id.content)
        val viewParent = contentView.parent as ViewGroup
        showFullScreen(activity)

        if (viewParent is ActionBarOverlayLayout) {
            // Does not support Activity with Theme Actionbar
            return
        }

        val index = viewParent.indexOfChild(contentView)
        viewParent.removeView(contentView)

        val constraintLayout = ConstraintLayout(activity).apply {
            id = myId
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val toolbarId = View.generateViewId()
        if (addToolbar) {
            val toolbar = BillingAdsToolbar(activity)
            toolbar.id = toolbarId
            toolbar.layoutParams =
                ConstraintLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topToTop = ConstraintSet.PARENT_ID
                    startToStart = ConstraintSet.PARENT_ID
                }
            constraintLayout.addView(toolbar)
            activity.setSupportActionBar(toolbar.findViewById(R.id.toolbar))
        }

        val bannerAds = BannerAds(activity).apply {
            id = View.generateViewId()
            layoutParams =
                ConstraintLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomToBottom = ConstraintSet.PARENT_ID
                    startToStart = ConstraintSet.PARENT_ID
                }
            load()
        }
        constraintLayout.addView(bannerAds)

        contentView.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            0
        ).apply {
            if (addToolbar) {
                topToBottom = toolbarId
            } else {
                topToTop = ConstraintSet.PARENT_ID
            }
            bottomToTop = bannerAds.id
        }
        constraintLayout.addView(contentView)

        viewParent.addView(constraintLayout, index)
        constraintLayout.requestLayout()
    }

    @Suppress("DEPRECATION")
    private fun showFullScreen(activity: AppCompatActivity) {
        if (Build.VERSION.SDK_INT >= 30) {
            activity.window.decorView.windowInsetsController?.hide(WindowInsets.Type.statusBars()/* or WindowInsets.Type.navigationBars()*/)
        } else {
            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
/*            activity.window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                        View.SYSTEM_UI_FLAG_FULLSCREEN
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY*/

        }
    }
}