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
        is String, is Number,
        is Boolean, is BigInteger,
        is JSObject, is JSArray -> value

        is AtomicInteger -> value.get()
        is AtomicLong -> value.get()

        is Map<*, *> -> JSObjectImpl(value.mapKeys { "$it" })
        is Pair<*, *> -> JSObjectImpl(Pair("${value.first}", value.second))

        is Collection<*> -> value.toJSArray()
        is Array<*> -> value.toJSArray()

        else -> throw IllegalArgumentException("${value::class} is not a valid JS type!")
    }
}