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
@file:JvmName("Actual_Jvm_ConversionKt")
package me.kgustave.json.internal

import me.kgustave.json.JSArray
import me.kgustave.json.JSObject
import java.math.BigInteger
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

internal actual fun convertValue(value: Any?): Any? {
    return when(value) {
        null -> null

    // Place these before so that Number doesn't catch them
        is AtomicInteger -> value.get()
        is AtomicLong -> value.get()

        is String, is Number, is BigInteger,
        is Boolean, is JSObject, is JSArray -> value

        is Pair<*, *> -> JSObjectImpl("${value.first}" to convertValue(value.second))
        is Map<*, *> -> value.entries.associateByTo(JSObjectImpl(), {
            it.key as? String ?: "${it.key}"
        }, {
            convertValue(it.value)
        })

        is Collection<*> -> JSArrayImpl(value.mapTo(ArrayList(value.size)) { convertValue(it) })
        is Array<*> -> JSArrayImpl(value.mapTo(ArrayList(value.size)) { convertValue(it) })

        else -> throw IllegalArgumentException("${value::class} is not a valid JS type!")
    }
}