package com.mmgsoft.modules.libs.manager

import com.mmgsoft.modules.libs.AdsComponents
import com.mmgsoft.modules.libs.etx.toCurrency
import com.mmgsoft.modules.libs.helpers.AmazonCurrency
import com.mmgsoft.modules.libs.helpers.BillingType
import com.mmgsoft.modules.libs.models.Background
import com.mmgsoft.modules.libs.utils.AdsComponentConfig
import java.text.NumberFormat
import java.util.*

object MoneyManager {
    private val prefs by lazy {
        AdsComponents.INSTANCE.adsPrefs
    }

    internal val amazonCurrencies = listOf(
        AmazonCurrency.US.toCurrency(),
        AmazonCurrency.CA.toCurrency(),
        AmazonCurrency.BR.toCurrency(),
        AmazonCurrency.MX.toCurrency(),
        AmazonCurrency.GB.toCurrency(),
        AmazonCurrency.DE.toCurrency(),
        AmazonCurrency.ES.toCurrency(),
        AmazonCurrency.FR.toCurrency(),
        AmazonCurrency.IT.toCurrency(),
        AmazonCurrency.IN.toCurrency(),
        AmazonCurrency.JP.toCurrency(),
        AmazonCurrency.AU.toCurrency(),
    )

    /**
     * Chuyển từ tiền DOLLAR sang loại tiền của app
     */
    private fun exchange(money: String, rate: Double = AdsComponentConfig.exchangeRate): Double {
        val m = if(AdsComponentConfig.billingType == BillingType.GOOGLE) money else getMoneyWithAmazonCurrency(money)
        val numberFormat = NumberFormat.getInstance()
        numberFormat.maximumFractionDigits = 0
        numberFormat.currency = Currency.getInstance(Locale.US)
        return numberFormat.parse(m)?.let {
            it.toDouble() * rate
        } ?: 0.0
    }

    private fun getMoneyWithAmazonCurrency(money: String): String {
        var m = ""
        amazonCurrencies.map { currency ->
            if(money.contains(currency.symbol)) {
                m = money.substring(currency.symbol.length, money.length)
                return@map
            }
        }

        return m
    }

    /**
     * Lấy số tiền hiện tại
     */
    fun getCurrentGoldStr(): String {
        val money = prefs.money
        return "$money ${AdsComponentConfig.currency}"
    }

    /**
     * Thực hiện cộng tiền khi billing thành công
     */
    fun addMoney(money: String, rate: Double = AdsComponentConfig.exchangeRate) {
        val newMoney = exchange(money, rate)
        prefs.money += newMoney
    }

    /**
     * Thực hiện trừ tiền khi mua backgrounds
     */
    fun buyBackground(background: Background): Boolean {
        val currentGold = prefs.money
        return if(currentGold < background.price.toDouble()) {
            false
        } else {
            prefs.money -=  background.price.toDouble()
            return true
        }
    }
}