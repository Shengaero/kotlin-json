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

/**
 * ### JavaScript Array
 *
 * JSArrays are [MutableList] sub-interfaces that provide null-safe accessors for
 * various [JSON data types](https://www.w3schools.com/js/js_json_datatypes.asp).
 *
 * @author Kaidan Gustave
 * @since  1.0
 */
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
    fun boolean(index: Int): Boolean

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
    fun string(index: Int): String

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
    fun long(index: Int): Long

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
    fun int(index: Int): Int

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
    fun double(index: Int): Double

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
    fun float(index: Int): Float

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
    fun obj(index: Int): JSObject

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
    fun array(index: Int): JSArray

    /**
     * Returns `true` if the value at the [index] specified is `null`.
     *
     * @param index The index to check for.
     *
     * @return `true` if the value with the [index] specified is `null`.
     */
    fun isNull(index: Int): Boolean

    fun optString(index: Int): String?

    fun optBoolean(index: Int): Boolean?

    fun optLong(index: Int): Long?

    fun optInt(index: Int): Int?

    fun optDouble(index: Int): Double?

    fun optFloat(index: Int): Float?

    fun optObj(index: Int): JSObject?

    fun optArray(index: Int): JSArray?
}
