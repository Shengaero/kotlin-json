/*
 * Copyright 2017 Kaidan Gustave
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
    fun string(key: String): String = castTo(key)
    fun long(key: String): Long = castTo(key)
    fun int(key: String): Int = castTo(key)
    fun double(key: String): Double = castTo(key)
    fun float(key: String): Float = castTo(key)
    fun obj(key: String): JSObject = castTo(key)
    fun array(key: String): JSArray = castTo(key)
    fun <T> opt(key: String): T? = this[key] as? T

    fun isNull(key: String): Boolean

    private inline fun <reified T> castTo(key: String): T {
        if(key !in this) {
            throw JSException("Key '$key' does not correspond to any value.")
        }

        val value = this[key]

        if(value === null) {
            throw JSException("Value with key '$key' was null instead of ${T::class}")
        }

        if(value !is T) {
            throw JSException("Value with key '$key' was ${value::class} instead of ${T::class}")
        }

        return value
    }
}
