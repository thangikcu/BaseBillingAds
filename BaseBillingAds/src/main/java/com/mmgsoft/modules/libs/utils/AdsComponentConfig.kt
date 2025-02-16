package com.mmgsoft.modules.libs.utils

import com.mmgsoft.modules.libs.helpers.AmazonCurrency
import com.mmgsoft.modules.libs.helpers.BackgroundLoadOn
import com.mmgsoft.modules.libs.helpers.BillingType
import com.mmgsoft.modules.libs.manager.MoneyManager

const val DEFAULT_CURRENCY = "GOLD"
const val DEFAULT_EXCHANGE_RATE = 1.2
const val DEFAULT_EXCHANGE_RATE_OTHER = 1.6
const val DEFAULT_ADS_CONSUME = "inapp.consume"
const val DEFAULT_ASSETS_PATH = "backgrounds"
const val ADS_PREFS_NAME = "ADS_PREFS_NAME"
const val START_WITH_PRODUCT_ID = "item_"
const val START_WITH_DESCRIPTION = "background "

object AdsComponentConfig {
    internal var packageNameLoadBackground = ""
    internal val activitiesNonLoadBackground = mutableListOf<String>()
    internal val testDevices = mutableListOf<String>()
    internal const val weightingPrice = 100
    internal var currency = DEFAULT_CURRENCY
    internal var exchangeRate = DEFAULT_EXCHANGE_RATE
    internal var consumeKey = DEFAULT_ADS_CONSUME
    internal var assetsPath = DEFAULT_ASSETS_PATH
    internal var billingType = BillingType.GOOGLE
    internal var loadBackgroundOn = BackgroundLoadOn.ON_RESUME
    internal val backgroundPrices = mutableListOf<String>()
    internal val refundMoneys = mutableListOf<String>()

    /**
     * Cập nhật loại Billing
     * @param billingType: [GOOGLE, AMAZON]
     */
    fun setBillingType(billingType: BillingType): AdsComponentConfig {
        this.billingType = billingType
        return this
    }

    /**
     * @param newCurrency
     * Tên loại tiền của app (GOLD, POINT,...)
     */
    fun updateCurrency(newCurrency: String): AdsComponentConfig {
        this.currency = newCurrency
        return this
    }

    /**
     * @param newExchangeRate
     * Tỉ lệ quy đổi từ tiền nạp sang tiền app
     * (default 1.2 => 1$ = 1.2 {@link $currency}
     */
    fun updateExchangeRate(newExchangeRate: Double): AdsComponentConfig {
        this.exchangeRate = newExchangeRate
        return this
    }


    /**
     * @param newConsume Path consumeID để phân biệt với INAPP của Google Billing
     */
    fun updateConsumeKey(newConsume: String): AdsComponentConfig {
        this.consumeKey = newConsume
        return this
    }


    /**
     * Thêm giá tiền cho từng loại ảnh
     * @param price: danh sách giá tiền
     * _______________________________________
     * Khi giá tiền truyền vào thừa hoặc thiếu (so với số lượng ảnh trong assets của myApp):
     * - Nếu số lượng prices thêm thiếu so với ảnh trong assets của app, sẽ lấy default (position * weightingPrice)
     * - Danh sách tiền này sẽ được map với danh sách ảnh trong assets, chỉ lấy đủ số lượng
     */
    fun addBackgroundPrice(vararg price: String): AdsComponentConfig {
        backgroundPrices.addAll(price)
        return this
    }

    /**
     * @param devices danh sách deviceID thêm vào Admob, tránh trường hợp bị leak và ăn gậy từ Google
     */
    fun addTestDevices(vararg devices: String): AdsComponentConfig {
        testDevices.addAll(devices)
        return this
    }


    /**
     * Fix số tiền cộng vào sau khi thanh toán các gói
     */
    fun setRefundMoneys(refunds: Array<String>): AdsComponentConfig {
        this.refundMoneys.clear()
        this.refundMoneys.addAll(refunds.map(::standardizedData))
        return this
    }


    /**
     * @param packageName: PackageName so sánh để load background
     */
    fun updatePackageNameLoadBackground(packageName: String): AdsComponentConfig {
        this.packageNameLoadBackground = packageName
        return this
    }

    /**
     * @param activitiesName: Danh sách tên activity cấu hình để loại bỏ background
     */
    fun addActivitiesNonLoadBackground(vararg activitiesName: String): AdsComponentConfig {
        this.activitiesNonLoadBackground.addAll(activitiesName)
        return this
    }

    /**
     * @param backgroundLoadOn: Cấu hình background được load ở function nào [ON_CREATED, ON_RESUME]
     * Default: ON_RESUME
     */
    fun updateLoadBackgroundOn(backgroundLoadOn: BackgroundLoadOn): AdsComponentConfig {
        this.loadBackgroundOn = backgroundLoadOn
        return this
    }

    /**
     * @version: 0.1.5
     * @param money Là dữ liệu chưa được chuẩn hóa
     * @return Dữ liệu đã được chuẩn hóa số tiền nhận sau
     *          khi được chỉnh sửa từ danh sách {@link AdsComponentConfig#updateBillingMapper}
     */
    private fun standardizedData(money: String): String {
        var isNotStandardized = true
        @Suppress("NAME_SHADOWING") var money = money
        MoneyManager.amazonCurrencies.map {
            if (money.contains(it.symbol)) {
                isNotStandardized = false
                return@map
            }
        }
        if (isNotStandardized) {
            money = "${AmazonCurrency.US.c}$money"
        }
        return money
    }
}