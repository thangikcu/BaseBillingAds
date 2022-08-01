package com.mmgsoft.modules.libs.helpers

import android.app.Activity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuProvider
import com.mmgsoft.modules.libs.BuildConfig
import com.mmgsoft.modules.libs.R
import com.mmgsoft.modules.libs.activity.ChangeBackgroundActivity
import com.mmgsoft.modules.libs.manager.PurchaseManager

class BillingAdsMenuProvider(private val activity: Activity) : MenuProvider {
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_purchase -> {
                if (BuildConfig.ROBO_TEST) {
                    return false
                }
                PurchaseManager.purchase(activity)
                true
            }
            R.id.action_background -> {
                ChangeBackgroundActivity.open(activity)
                true
            }
            else -> {
                false
            }
        }
    }
}