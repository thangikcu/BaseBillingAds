package com.mmgsoft.modules.libs.helpers

import java.lang.reflect.Type
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

internal class GetSetPreferenceDelegate<T>(
    private val key: String,
    private val defaultValue: T,
    private val type: Type
) : ReadWriteProperty<AdsPrefs, T> {

    private var value: T? = null

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: AdsPrefs, property: KProperty<*>): T {
        return value ?: thisRef.get(
            key,
            defaultValue,
            type
        ).also {
            value = it
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun setValue(thisRef: AdsPrefs, property: KProperty<*>, value: T) {
        this.value = value
        thisRef.put(key, value)
    }
}