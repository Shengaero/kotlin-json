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
    fun string(index: Int): String = castTo(index)
    fun long(index: Int): Long = castTo(index)
    fun int(index: Int): Int = castTo(index)
    fun double(index: Int): Double = castTo(index)
    fun float(index: Int): Float = castTo(index)
    fun obj(index: Int): JSObject = castTo(index)
    fun array(index: Int): JSArray = castTo(index)
    fun <T> opt(index: Int): T? = this[index] as? T

    fun isNull(index: Int): Boolean

    private inline fun <reified T> castTo(index: Int): T {
        if(index < 0 || index >= size) {
            // Important that we throw an IndexOutOfBoundsException
            // here and not a JSException
            throw IndexOutOfBoundsException("Index '$index' is outside of indexed bounds.")
        }

        val value = this[index]

        if(value === null) {
            throw JSException("Value at index '$index' was null instead of ${T::class}")
        }

        if(value !is T) {
            throw JSException("Value at index '$index' was ${value::class} instead of ${T::class}")
        }

        return value
    }

    companion object {
        operator fun get(vararg elements: Any?): JSArray {
            return JSArrayImpl(elements)
        }
    }
}
