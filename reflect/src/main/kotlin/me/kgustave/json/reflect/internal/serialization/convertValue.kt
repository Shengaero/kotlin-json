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
@file:JvmName("Internal_ConvertValueKt")
package me.kgustave.json.reflect.internal.serialization

import me.kgustave.json.JSArray
import me.kgustave.json.JSObject
import me.kgustave.json.jsArrayOf
import me.kgustave.json.jsObjectOf
import me.kgustave.json.reflect.JSSerializer
import java.math.BigInteger
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

internal fun JSSerializer.convertValue(value: Any?): Any? {
    when(value) {
        null -> return null

        is AtomicInteger -> return value.get()
        is AtomicLong -> return value.get()

        is String, is Number,
        is Boolean, is BigInteger,
        is JSObject, is JSArray -> return value

        is Map<*, *> -> {
            val json = jsObjectOf()
            for((k, v) in value) {
                json[k.toString()] = convertValue(v)
            }
            return json
        }
        is Pair<*, *> -> return jsObjectOf("${value.first}" to convertValue(value.second))

        is Collection<*> -> return value.mapTo(jsArrayOf()) { convertValue(it) }
        is Array<*> -> return value.mapTo(jsArrayOf()) { convertValue(it) }

        else -> return serialize(value)
    }
}
