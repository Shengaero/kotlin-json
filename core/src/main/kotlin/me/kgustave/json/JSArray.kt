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
import me.kgustave.json.internal.*
import kotlin.reflect.KClass

/**
 * ### JavaScript Array
 *
 * JSArrays are [MutableList] sub-interfaces that provide null-safe accessors for
 * various [JSON data types](https://www.w3schools.com/js/js_json_datatypes.asp).
 *
 * @author Kaidan Gustave
 * @since  1.0
 */
@JSONDsl
interface JSArray: MutableList<Any?>, JSString {
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
        return checkNotNullJson(convertBoolean(value)) { isNotType(index, value, Boolean::class) }
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
        convertLong(value)?.let { return it }
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
        return checkNotNullJson(convertInt(value)) { isNotType(index, value, Int::class) }
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
        convertDouble(value)
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
        return checkNotNullJson(convertFloat(value)) { isNotType(index, value, Float::class) }
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
     * Returns `true` if the value at the [index] specified is `null`.
     *
     * @param index The index to check for.
     *
     * @return `true` if the value with the [index] specified is `null`.
     */
    fun isNull(index: Int): Boolean

    fun optString(index: Int): String? = convertString(optValueFor(index))

    fun optBoolean(index: Int): Boolean? = convertBoolean(optValueFor(index), fromString = true)

    fun optLong(index: Int): Long? = convertLong(optValueFor(index), fromString = true)

    fun optInt(index: Int): Int? = convertInt(optValueFor(index), fromString = true)

    fun optDouble(index: Int): Double? = convertDouble(optValueFor(index), fromString = true)

    fun optFloat(index: Int): Float? = convertFloat(optValueFor(index), fromString = true)

    fun optObj(index: Int): JSObject? = optValueFor(index) as? JSObject

    fun optArray(index: Int): JSArray? = optValueFor(index) as? JSArray

    private fun getValueFor(index: Int): Any {
        if(index < 0 || index >= size) {
            // Important that we throw an ArrayIndexOutOfBoundsException
            //here and not a JSException
            throw ArrayIndexOutOfBoundsException(index)
        }
        val value = this[index]
        if(value === null) {
            throw JSException("Value at index '$index' was null.")
        }
        return value
    }

    private fun optValueFor(index: Int): Any? {
        if(index < 0 || index >= size) {
            // Make sure to return null so we don't hit
            //an IndexOutOfBoundsException
            return null
        }
        return this[index]
    }

    private companion object {
        private fun isNotType(index: Int, value: Any, type: KClass<*>): String {
            // Do not use KClass directly in order to avoid
            //needing kotlin.reflect as a dependency.
            return "Value with key '$index' was ${value::class.javaPrimitiveType ?: value::class.java} " +
                   "instead of ${type.javaPrimitiveType ?: type.java}"
        }
    }
}
