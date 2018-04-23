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
import me.kgustave.json.internal.JSArrayImpl
import me.kgustave.json.internal.JSONDsl
import me.kgustave.json.internal.checkNotNullJson
import kotlin.reflect.KClass

/**
 * ### JavaScript Array
 *
 * JSArrays are [MutableList] sub-interfaces that provide null-safe accessors for
 * various [JSON data types](https://www.w3schools.com/js/js_json_datatypes.asp).
 *
 * Creation of JSArrays is supported through the following functions/accessors:
 *
 * - **[jsonArray]**: Functional block builder or a function call with several pairs.
 * - **[emptyJSArray]**: Creates an new, empty, JSArray.
 * - **[readJSArray]**: Various extension functions for IO operations that involve reading JSArrays from files.
 * - **[parseJsonArray]**: Raw string JSArray parsing.
 *
 * @author Kaidan Gustave
 * @since  1.0
 */
@JSONDsl
@SinceKotlin("1.2")
interface JSArray : MutableList<Any?>, JSString {
    /**
     * Gets a [Boolean] at the [index] specified.
     *
     * @param index The index to get a value at.
     *
     * @return A [Boolean] value.
     *
     * @throws IndexOutOfBoundsException If `[index] < 0 || [index] >= [size]`
     * @throws JSException If the value at [index] is `null` or is not a [Boolean].
     */
    fun boolean(index: Int): Boolean {
        val value = getValueFor(index)
        return checkNotNullJson(value as? Boolean) { isNotType(index, value, Boolean::class) }
    }

    /**
     * Gets a [String] at the [index] specified.
     *
     * @param index The index to get a value at.
     *
     * @return A [String] value.
     *
     * @throws IndexOutOfBoundsException If `[index] < 0 || [index] >= [size]`
     * @throws JSException If the value at [index] is `null` or is not a [String].
     */
    fun string(index: Int): String {
        val value = getValueFor(index)
        return checkNotNullJson(value as? String) { isNotType(index, value, String::class) }
    }

    /**
     * Gets a [Long] at the [index] specified.
     *
     * If the value is stored as an [Int], this will convert it
     * into a [Long] and return it instead.
     *
     * @param index The index to get a value at.
     *
     * @return A [Long] value.
     *
     * @throws IndexOutOfBoundsException If `[index] < 0 || [index] >= [size]`
     * @throws JSException If the value at [index] is `null` or is not a [Long].
     */
    fun long(index: Int): Long {
        val value = getValueFor(index)
        if(value is Number) {
            (value as? Long ?: (value as? Int)?.toLong())?.let { return it }
        }
        throw JSException(isNotType(index, value, Long::class))
    }

    /**
     * Gets a [Int] at the [index] specified.
     *
     * @param index The index to get a value at.
     *
     * @return A [Int] value.
     *
     * @throws IndexOutOfBoundsException If `[index] < 0 || [index] >= [size]`
     * @throws JSException If the value at [index] is `null` or is not a [Int].
     */
    fun int(index: Int): Int {
        val value = getValueFor(index)
        return checkNotNullJson(value as? Int) { isNotType(index, value, Int::class) }
    }

    /**
     * Gets a [Double] at the [index] specified.
     *
     * If the value is stored as an [Float], this will convert it
     * into a [Double] and return it instead.
     *
     * @param index The index to get a value at.
     *
     * @return A [Double] value.
     *
     * @throws IndexOutOfBoundsException If `[index] < 0 || [index] >= [size]`
     * @throws JSException If the value at [index] is `null` or is not a [Double].
     */
    fun double(index: Int): Double {
        val value = getValueFor(index)
        if(value is Number) {
            (value as? Double ?: (value as? Float)?.toDouble())?.let { return it }
        }
        throw JSException(isNotType(index, value, Double::class))
    }

    /**
     * Gets a [Float] at the [index] specified.
     *
     * @param index The index to get a value at.
     *
     * @return A [Float] value.
     *
     * @throws IndexOutOfBoundsException If `[index] < 0 || [index] >= [size]`
     * @throws JSException If the value at [index] is `null` or is not a [Float].
     */
    fun float(index: Int): Float {
        val value = getValueFor(index)
        return checkNotNullJson(value as? Float) { isNotType(index, value, Float::class) }
    }

    /**
     * Gets a [JSObject] at the [index] specified.
     *
     * @param index The index to get a value at.
     *
     * @return A [JSObject] value.
     *
     * @throws IndexOutOfBoundsException If `[index] < 0 || [index] >= [size]`
     * @throws JSException If the value at [index] is `null` or is not a [JSObject].
     */
    fun obj(index: Int): JSObject {
        val value = getValueFor(index)
        return checkNotNullJson(value as? JSObject) { isNotType(index, value, JSObject::class) }
    }

    /**
     * Gets a [JSArray] at the [index] specified.
     *
     * @param index The index to get a value at.
     *
     * @return A [JSArray] value.
     *
     * @throws IndexOutOfBoundsException If `[index] < 0 || [index] >= [size]`
     * @throws JSException If the value at [index] is `null` or is not a [JSArray].
     */
    fun array(index: Int): JSArray {
        val value = getValueFor(index)
        return checkNotNullJson(value as? JSArray) { isNotType(index, value, JSArray::class) }
    }

    /**
     * Gets a value at the [index] specified, or `null` if it is not type [T].
     *
     * @param [T] The type of value to get.
     * @param index The index to get a value at.
     *
     * @return A value of type [T], or `null` if no value exists,
     * or if the value is not the type [T].
     */
    fun <T> opt(index: Int): T? = this[index] as? T

    /**
     * Returns `true` if the value at the [index] specified is `null`.
     *
     * @param index The index to check for.
     *
     * @return `true` if the value with the [index] specified is `null`.
     */
    fun isNull(index: Int): Boolean

    private fun getValueFor(index: Int): Any {
        if(index < 0 || index >= size) {
            // Important that we throw an IndexOutOfBoundsException
            //here and not a JSException
            throw IndexOutOfBoundsException("Index '$index' is outside of indexed bounds.")
        }
        val value = this[index]
        if(value === null) {
            throw JSException("Value at index '$index' was null.")
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
         * Creates a [JSArray] with an array-like syntax:
         * ```kotlin
         * val jsonArray = JSArray["foo", 124, null]
         * ```
         *
         * @param elements The elements to create the [JSArray] with.
         *
         * @return A [JSArray] with the provided elements.
         */
        @Deprecated(
            message = "Unnecessary/inferior method for creation of JSObjects.",
            replaceWith = ReplaceWith(
                expression = "jsonObject(JSObject.() -> Unit)",
                imports = ["me.kgustave.json.jsonObject"]
            ),
            level = DeprecationLevel.WARNING
        )
        operator fun get(vararg elements: Any?): JSArray {
            return JSArrayImpl(elements)
        }


        private fun isNotType(index: Int, value: Any, type: KClass<*>): String {
            return "Value with key '$index' was ${value::class} instead of $type"
        }
    }
}
