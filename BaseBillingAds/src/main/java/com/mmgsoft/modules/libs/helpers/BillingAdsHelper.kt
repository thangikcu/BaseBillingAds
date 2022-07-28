package com.mmgsoft.modules.libs.helpers

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.mmgsoft.modules.libs.widgets.BannerAds

object BillingAdsHelper {

    fun provideMenu(activity: ComponentActivity) {
        activity.addMenuProvider(BillingAdsMenuProvider(activity))
    }

    fun showBannerAds(activity: Activity) {
        val contentView: View = activity.window.decorView.findViewById(android.R.id.content)

        val viewParent = contentView.parent as ViewGroup
        val index = viewParent.indexOfChild(contentView)
        viewParent.removeView(contentView)

        val constraintLayout = ConstraintLayout(activity).apply {
            layoutParams = ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        val bannerAds = BannerAds(activity)
        bannerAds.id = View.generateViewId()
        bannerAds.layoutParams =
            ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomToBottom = ConstraintSet.PARENT_ID
                startToStart = ConstraintSet.PARENT_ID
            }
        constraintLayout.addView(bannerAds)

        contentView.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            0
        ).apply {
            topToTop = ConstraintSet.PARENT_ID
            startToStart = ConstraintSet.PARENT_ID
            bottomToTop = bannerAds.id
        }
        constraintLayout.addView(contentView)
        viewParent.addView(constraintLayout, index)
        constraintLayout.requestLayout()
        bannerAds.load()
    }
}