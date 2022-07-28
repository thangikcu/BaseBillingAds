package com.mmgsoft.modules.libs.customview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.mmgsoft.modules.libs.R

class BillingAdsToolbar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : FrameLayout(context, attrs) {
    init {
        View.inflate(context, R.layout.appbar, this)
    }
}