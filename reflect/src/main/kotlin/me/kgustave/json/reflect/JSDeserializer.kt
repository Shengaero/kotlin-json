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
import me.kgustave.json.reflect.internal.deserialization.DeserializationCache
import kotlin.reflect.KClass
import kotlin.reflect.full.cast

/**
 * Deserializer for [JSObjects][JSObject].
 *
 * @author Kaidan Gustave
 * @since  1.5
 */
@JSDeserialization
class JSDeserializer {
    // Remain internal for testing.
    internal val cache = DeserializationCache(this)

    @JSDeserialization
    fun <T: Any> deserialize(json: JSObject, klass: KClass<T>): T {
        checkIfReflectionIsInClasspath()
        return klass.cast(cache.construct(klass, json))
    }

    @JSDeserialization
    fun <T: Any> register(klass: KClass<T>) {
        checkIfReflectionIsInClasspath()
        cache.register(klass)
    }

    @JSDeserialization
    fun isRegistered(klass: KClass<*>) = cache.isRegistered(klass)
}
