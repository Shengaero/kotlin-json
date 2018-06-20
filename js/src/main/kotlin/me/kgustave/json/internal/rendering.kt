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
package me.kgustave.json.internal

import me.kgustave.json.JSArray
import me.kgustave.json.JSObject

internal actual fun renderValue(value: Any?, indent: Int, level: Int, newline: Boolean): String {
    return when(value) {
        null -> value.toString()
        is Boolean -> value.toString()
        is String -> escape(value)
        is Int -> value.toString()
        is Long -> value.toString()
        is Float -> value.toDouble().toString()
        is Double -> value.toString()
        is JSObject -> value.buildJsonObjectString(indent, level + 1, newline)
        is JSArray -> value.buildJsonArrayString(indent, level + 1, newline)
        is Map<*, *> -> {
            @Suppress("UNCHECKED_CAST")
            // This might be faster to cast if the typing is correct
            val realMap = value as? Map<String, *> ?: value.mapKeys { "${it.key}" }
            return realMap.buildJsonObjectString(indent, level + 1, newline)
        }
        is List<*> -> value.buildJsonArrayString(indent, level + 1, newline)
        is Array<*> -> value.buildJsonArrayString(indent, level + 1, newline)
        else -> throw IllegalStateException("Cannot convert type: ${value::class}")
    }
}

internal actual fun renderHexString(int: Int): String {
    TODO("Implement")
}