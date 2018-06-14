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
import kotlin.reflect.KClass

/**
 * Serializer for [JSObjects][JSObject].
 *
 * @author Kaidan Gustave
 * @since  1.5
 */
class JSSerializer @JvmOverloads constructor(configure: JSSerializer.Config.() -> Unit = {}) {
    // Remain internal for testing.
    internal val cache = SerializationCache(this)

    val isSafe: Boolean

    init {
        val config = Config().apply(configure)
        this.isSafe = config.isSafe
    }

    fun serialize(any: Any): JSObject {
        checkIfReflectionIsInClasspath()
        when(any) {
            is String, is Boolean, is Number -> reflectionError {
                "Cannot convert simplistic/primitive type: ${any::class}!"
            }
        }
        return cache.destruct(any)
    }

    fun register(klass: KClass<*>) {
        checkIfReflectionIsInClasspath()
        cache.register(klass)
    }

    fun isRegistered(klass: KClass<*>) = cache.isRegistered(klass)

    /**
     * Configurations for [JSSerializer].
     *
     * @author Kaidan Gustave
     * @since  1.6
     */
    class Config internal constructor() {
        /**
         * Makes the configured JSSerializer safe from types
         * not marked with [@JSSerializable][JSSerializable].
         */
        var isSafe = false
    }
}