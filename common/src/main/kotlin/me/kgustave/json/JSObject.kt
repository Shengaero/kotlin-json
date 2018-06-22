/*
 * Copyright 2018 Kaidan Gustave
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.kgustave.json

import me.kgustave.json.exceptions.JSException
import me.kgustave.json.internal.*

/**
 * ### JavaScript Object
 *
 * JSObjects are [MutableMap] sub-interfaces that provide null-safe accessors for
 * various [JSON data types](https://www.w3schools.com/js/js_json_datatypes.asp).
 *
 * @author Kaidan Gustave
 * @since  1.0
 */
interface JSObject: MutableMap<String, Any?>, JSString {
    /**
     * Gets the [Boolean] value for the [key] specified.
     *
     * @param key The key to get a value for.
     *
     * @return A [Boolean] value.
     *
     * @throws JSException If the value for the [key] is `null` or is not a [Boolean].
     */
    fun boolean(key: String): Boolean {
        val value = getValueFor(key)
        return checkNotNullJson(convertBoolean(value)) { isNotType(key, value, Boolean::class) }
    }

    /**
     * Gets the [String] value for the [key] specified.
     *
     * @param key The key to get a value for.
     *
     * @return A [String] value.
     *
     * @throws JSException If the value for the [key] is `null` or is not a [String].
     */
    fun string(key: String): String {
        val value = getValueFor(key)
        return checkNotNullJson(value as? String) { isNotType(key, value, String::class) }
    }

    /**
     * Gets the [Long] value for the [key] specified.
     *
     * If the value is stored as an [Int], this will convert it
     * into a [Long] and return it instead.
     *
     * @param key The key to get a value for.
     *
     * @return A [Long] value.
     *
     * @throws JSException If the value for the [key] is `null` or is not a [Long].
     */
    fun long(key: String): Long {
        val value = getValueFor(key)
        convertLong(value)?.let { return it }
        throw JSException(isNotType(key, value, Long::class))
    }

    /**
     * Gets the [Int] value for the [key] specified.
     *
     * @param key The key to get a value for.
     *
     * @return A [Int] value.
     *
     * @throws JSException If the value for the [key] is `null` or is not a [Int].
     */
    fun int(key: String): Int {
        val value = getValueFor(key)
        return checkNotNullJson(convertInt(value)) { isNotType(key, value, Int::class) }
    }

    /**
     * Gets the [Double] value for the [key] specified.
     *
     * If the value is stored as an [Float], this will convert it
     * into a [Double] and return it instead.
     *
     * @param key The key to get a value for.
     *
     * @return A [Double] value.
     *
     * @throws JSException If the value for the [key] is `null` or is not a [Double].
     */
    fun double(key: String): Double {
        val value = getValueFor(key)
        convertDouble(value)?.let { return it }
        throw JSException(isNotType(key, value, Double::class))
    }

    /**
     * Gets the [Float] value for the [key] specified.
     *
     * @param key The key to get a value for.
     *
     * @return A [Float] value.
     *
     * @throws JSException If the value for the [key] is `null` or is not a [Float].
     */
    fun float(key: String): Float {
        val value = getValueFor(key)
        return checkNotNullJson(convertFloat(value)) { isNotType(key, value, Float::class) }
    }

    /**
     * Gets the [JSObject] value for the [key] specified.
     *
     * @param key The key to get a value for.
     *
     * @return A [JSObject] value.
     *
     * @throws JSException If the value for the [key] is `null` or is not a [JSObject].
     */
    fun obj(key: String): JSObject {
        val value = getValueFor(key)
        return checkNotNullJson(value as? JSObject) { isNotType(key, value, JSObject::class) }
    }

    /**
     * Gets the [Array] value for the [key] specified.
     *
     * @param key The key to get a value for.
     *
     * @return A [JSArray] value.
     *
     * @throws JSException If the value for the [key] is `null` or is not a [JSArray].
     */
    fun array(key: String): JSArray {
        val value = getValueFor(key)
        return checkNotNullJson(value as? JSArray) { isNotType(key, value, JSArray::class) }
    }

    fun optString(key: String): String? = convertString(this[key])

    fun optBoolean(key: String): Boolean? = convertBoolean(this[key], fromString = true)

    fun optLong(key: String): Long? = convertLong(this[key], fromString = true)

    fun optInt(key: String): Int? = convertInt(this[key], fromString = true)

    fun optDouble(key: String): Double? = convertDouble(this[key], fromString = true)

    fun optFloat(key: String): Float? = convertFloat(this[key], fromString = true)

    fun optObj(key: String): JSObject? = this[key] as? JSObject

    fun optArray(key: String): JSArray? = this[key] as? JSArray

    /**
     * Returns `true` if the value with the [key] specified is `null`.
     *
     * @param key The key to check for.
     *
     * @return `true` if the value with the [key] specified is `null`.
     */
    fun isNull(key: String): Boolean

    /**
     * Adds an entry in this [JSObject] where the
     * receiver is the key, and the [item] is the
     * value.
     *
     * @receiver The key.
     * @param item The value.
     */
    @JSONDsl
    infix fun String.to(item: Any?) {
        this@JSObject[this] = item
    }

    private fun getValueFor(key: String): Any {
        if(key !in this) {
            throw JSException("Key '$key' does not correspond to any value.")
        }
        val value = this[key]
        if(value === null) {
            throw JSException("Value with key '$key' was null")
        }
        return value
    }
}
