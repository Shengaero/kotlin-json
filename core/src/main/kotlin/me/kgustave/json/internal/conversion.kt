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
@file:JvmName("Internal_ConversionKt")
package me.kgustave.json.internal

import me.kgustave.json.JSArray
import me.kgustave.json.JSObject
import me.kgustave.json.toJSArray
import java.math.BigInteger
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

internal fun convertValue(value: Any?): Any? {
    return when(value) {
        null -> null

        // Place these before so that Number doesn't catch them
        is AtomicInteger -> value.get()
        is AtomicLong -> value.get()

        is String, is Number,
        is Boolean, is BigInteger,
        is JSObject, is JSArray -> value

        is Map<*, *> -> JSObjectImpl(value.mapKeys { "$it" })
        is Pair<*, *> -> JSObjectImpl(Pair("${value.first}", value.second))

        is Collection<*> -> value.toJSArray()
        is Array<*> -> value.toJSArray()

        else -> throw IllegalArgumentException("${value::class} is not a valid JS type!")
    }
}

internal fun convertString(value: Any?): String? {
    if(value is String) return value
    if(value is JSObject || value is JSArray) {
        // shouldn't return objects or arrays as strings!
        return null
    }
    return value.toString()
}

internal fun convertBoolean(value: Any?, fromString: Boolean = false): Boolean? {
    if(value is Boolean) return value
    if(fromString && (value is String && (value == "true" || value == "false"))) {
        // check if it is true or false so we don't
        //convert a non-boolean value and return
        //false when it should be null
        return value.toBoolean()
    }
    return null
}

internal fun convertLong(value: Any?, fromString: Boolean = false): Long? {
    (value as? Long ?: (value as? Int)?.toLong())?.let { return it }
    if(fromString && value is String) {
        return value.toLongOrNull()
    }
    return null
}

internal fun convertInt(value: Any?, fromString: Boolean = false): Int? {
    (value as? Int)?.let { return it }
    if(fromString && value is String) {
        return value.toIntOrNull()
    }
    return null
}

internal fun convertDouble(value: Any?, fromString: Boolean = false): Double? {
    (value as? Double ?: (value as? Float)?.toDouble())?.let { return it }
    if(fromString && value is String) {
        return value.toDoubleOrNull()
    }
    return null
}

internal fun convertFloat(value: Any?, fromString: Boolean = false): Float? {
    (value as? Float)?.let { return it }
    if(fromString && value is String) {
        return value.toFloatOrNull()
    }
    return null
}
