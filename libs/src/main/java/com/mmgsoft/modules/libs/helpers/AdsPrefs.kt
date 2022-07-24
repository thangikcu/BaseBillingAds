package com.mmgsoft.modules.libs.helpers

import android.content.Context
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.reflect.TypeToken
import com.mmgsoft.modules.libs.manager.BackgroundManager.getWasPaidBackgrounds
import com.mmgsoft.modules.libs.models.Background
import com.mmgsoft.modules.libs.utils.ADS_PREFS_NAME
import java.lang.reflect.Type


const val PREFS_BILLING_INTERSTITIAL = "PREFS_BILLING_INTERSTITIAL"
const val PREFS_BILLING_ADMOB_BANNER = "PREFS_BILLING_ADMOB_BANNER"
const val PREFS_BACKGROUND_WAS_PAID = "PREFS_BACKGROUND_WAS_PAID"
const val PREFS_CURRENT_BACKGROUND_SELECTED = "PREFS_CURRENT_BACKGROUND_SELECTED"
const val PREFS_PRODUCTS_ID_WAS_PAID = "PREFS_PRODUCTS_ID_WAS_PAID"
const val PREFS_MONEY = "PREFS_MONEY"

internal class AdsPrefs(private val applicationContext: Context) {

    private val mSharedPrefs by lazy {
        applicationContext.getSharedPreferences(ADS_PREFS_NAME, Context.MODE_PRIVATE)
    }

    private val gson by lazy {
        GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()
    }

    var isBillingInterstitial: Boolean by GetSetPreferenceDelegate(
        PREFS_BILLING_INTERSTITIAL,
        false,
        Boolean::class.java
    )

    var isBillingAdmobBanner: Boolean by GetSetPreferenceDelegate(
        PREFS_BILLING_ADMOB_BANNER,
        false,
        Boolean::class.java
    )

    var money: Double by GetSetPreferenceDelegate(
        PREFS_MONEY,
        0.0,
        Double::class.java
    )

    var selectedBackground: Background? by GetSetPreferenceDelegate(
        PREFS_CURRENT_BACKGROUND_SELECTED,
        null,
        Background::class.java
    )

    var wasPaidBackgrounds: MutableList<Background> by GetSetPreferenceDelegate(
        PREFS_BACKGROUND_WAS_PAID,
        mutableListOf(),
        object : TypeToken<ArrayList<Background>>() {}.type
    )

    fun <T> put(key: String, data: T) {
        with(mSharedPrefs.edit()) {
            when (data) {
                is String? -> putString(key, data)
                is Boolean -> putBoolean(key, data)
                is Float -> putFloat(key, data)
                is Double -> putString(key, data.toString())
                is Int -> putInt(key, data)
                is Long -> putLong(key, data)
                else -> putString(key, gson.toJson(data))
            }
            apply()
        }
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> get(key: String, defaultValue: T): T {
        val kClass = T::class
        val type = when (kClass.simpleName?.lowercase()) {
            String::class.simpleName!!.lowercase() -> String::class.java
            Boolean::class.simpleName!!.lowercase() -> Boolean::class.java
            Float::class.simpleName!!.lowercase() -> Float::class.java
            Double::class.simpleName!!.lowercase() -> Double::class.java
            Int::class.simpleName!!.lowercase() -> Int::class.java
            Long::class.simpleName!!.lowercase() -> Long::class.java
            else -> kClass.java
        }
        return get(key, defaultValue, type)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String, defaultValue: T, type: Type): T {
        return with(mSharedPrefs) {
            when (type) {
                String::class.java -> getString(key, defaultValue as? String) as T
                Boolean::class.java -> getBoolean(key, defaultValue as Boolean) as T
                Float::class.java -> getFloat(key, defaultValue as Float) as T
                Double::class.java -> getString(key, defaultValue.toString())!!.toDouble() as T
                Int::class.java -> getInt(key, defaultValue as Int) as T
                Long::class.java -> getLong(key, defaultValue as Long) as T
                else -> {
                    val jsonString = getString(key, null)

                    if (jsonString.isNullOrEmpty()) {
                        defaultValue
                    } else {
                        try {
                            gson.fromJson(jsonString, type)
                        } catch (e: Exception) {
                            defaultValue
                        }
                    }
                }
            }
        }
    }

    fun remove(key: String) {
        with(mSharedPrefs.edit()) {
            remove(key)
            apply()
        }
    }

    fun clear() {
        with(mSharedPrefs.edit()) {
            clear()
            apply()
        }
    }
}