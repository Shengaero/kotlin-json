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
@file:Suppress("unused")
package me.kgustave.json.reflect

import me.kgustave.json.JSObject
import me.kgustave.json.reflect.internal.checkIfReflectionIsInClasspath
import me.kgustave.json.reflect.internal.reflectionError
import me.kgustave.json.reflect.internal.serialization.SerializationCache
import java.math.BigInteger
import kotlin.reflect.KClass

/**
 * Serializer for [JSObjects][JSObject].
 *
 * @author Kaidan Gustave
 * @since  1.5
 */
open class JSSerializer {
    // Remain internal for testing.
    // Also lazy as a workaround for opening class.
    internal val cache by lazy { SerializationCache(this) }

    fun serialize(any: Any): JSObject {
        checkIfReflectionIsInClasspath()
        verifyNotBottomType(any)
        return cache.destruct(any)
    }

    fun register(klass: KClass<*>) {
        checkIfReflectionIsInClasspath()
        cache.register(klass)
    }

    private fun verifyNotBottomType(any: Any) {
        when(any) {
            is String, is Boolean, is Number, is BigInteger -> reflectionError {
                "Cannot convert simplistic/primitive type: ${any::class}!"
            }
        }
    }
}