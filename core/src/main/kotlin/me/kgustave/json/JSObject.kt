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
@file:Suppress("UNCHECKED_CAST")
package me.kgustave.json

import me.kgustave.json.exceptions.JSException
import me.kgustave.json.internal.JSONDsl
import me.kgustave.json.internal.checkNotNullJson
import kotlin.reflect.KClass

/**
 * ### JavaScript Object
 *
 * JSObjects are [MutableMap] sub-interfaces that provide null-safe accessors for
 * various [JSON data types](https://www.w3schools.com/js/js_json_datatypes.asp).
 *
 * Creation of JSObjects is supported through the following functions/accessors:
 *
 * - **[jsonObject]**: Functional block builder or a function call with several pairs.
 * - **[emptyJSObject]**: Creates an new, empty, JSObject.
 * - **[readJSObject]**: Various extension functions for IO operations that involve reading JSObjects from files.
 * - **[parseJsonObject]**: Raw string JSObject parsing.
 *
 * @author Kaidan Gustave
 * @since  1.0
 */
@JSONDsl
@SinceKotlin("1.2")
interface JSObject : MutableMap<String, Any?>, JSString {
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
        return checkNotNullJson(value as? Boolean) { isNotType(key, value, Boolean::class) }
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
        if(value is Number) {
            (value as? Long ?: (value as? Int)?.toLong())?.let { return it }
        }
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
        return checkNotNullJson(value as? Int) { isNotType(key, value, Int::class) }
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
        if(value is Number) {
            (value as? Double ?: (value as? Float)?.toDouble())?.let { return it }
        }
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
        return checkNotNullJson(value as? Float) { isNotType(key, value, Float::class) }
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

    /**
     * Gets a value with the [key] specified, or `null` if it is not type [T].
     *
     * @param [T] The type of value to get.
     * @param key The key to get a value for.
     *
     * @return A value of type [T], or `null` if no value exists,
     * or if the value is not the type [T].
     */
    fun <T> opt(key: String): T? = this[key] as? T

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

    @Deprecated(
        message = "JSObject creation through operator convention is " +
                  "deprecated and is scheduled for privatization by 1.5.0",
        level = DeprecationLevel.WARNING
    )
    companion object {
        /**
         * Creates a [JSObject] with a closure syntax:
         * ```kotlin
         * val jsonObject = JSObject {
         *     "key" to "value"
         * }
         * ```
         *
         * @param block The block to create the [JSObject] with.
         *
         * @return A [JSObject] with the provided block applied.
         */
        @Deprecated(
            message = "Unnecessary/inferior method for creation of JSObjects.",
            replaceWith = ReplaceWith(
                expression = "jsonObject(JSObject.() -> Unit)",
                imports = ["me.kgustave.json.jsonObject"]
            ),
            level = DeprecationLevel.WARNING
        )
        inline operator fun invoke(block: JSObject.() -> Unit): JSObject = jsonObject(block)

        private fun isNotType(key: String, value: Any, type: KClass<*>): String {
            return "Value with key '$key' was ${value::class} instead of $type"
        }
    }
}
