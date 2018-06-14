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
package me.kgustave.json.reflect.internal.serialization

import me.kgustave.json.JSObject
import me.kgustave.json.reflect.*
import me.kgustave.json.reflect.internal.reflectionError
import java.lang.reflect.Modifier
import kotlin.reflect.KClass
import kotlin.reflect.KVisibility.INTERNAL
import kotlin.reflect.KVisibility.PUBLIC
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

internal class SerializationCache(private val serializer: JSSerializer) {
    private val strategies = mutableMapOf<KClass<*>, SerializationStrategy>()

    internal fun register(klass: KClass<*>) {
        if(klass !in strategies) {
            registerInternally(klass)
        }
    }

    internal fun destruct(any: Any): JSObject {
        val klass = any::class
        checkClassIsValidToDestruct(klass)
        return (strategies[klass] ?: registerInternally(klass)).destruct(any)
    }

    internal fun isRegistered(klass: KClass<*>): Boolean = klass in strategies

    private fun registerInternally(klass: KClass<*>): SerializationStrategy {
        check(!serializer.isSafe || klass.annotations.any { it is JSSerializable }) {
            "$klass is not marked with @JSSerializable!"
        }

        var valid = klass.memberProperties.filter {
            it.visibility == PUBLIC || it.visibility == INTERNAL
        }

        valid.filter { it.findAnnotation<JSValue>() !== null }.takeUnless { it.isEmpty() }?.let { valid = it }

        val properties = valid.associateBy({ it.findAnnotation<JSName>()?.value ?: it.name }) p@ {
            return@p PropertyHandler(serializer, it, it.findAnnotation<JSOptional>() !== null)
        }
        val strategy = SerializationStrategy(serializer = serializer, properties = properties)
        check(klass !in strategies) { "Already registered type: $klass" }
        strategies[klass] = strategy
        return strategy
    }

    private companion object {
        @JvmStatic private fun checkClassIsValidToDestruct(klass: KClass<*>) {
            // class checks
            when {
                klass.visibility != PUBLIC && klass.visibility != INTERNAL -> {
                    val debugVisibility = klass.visibility?.toString()?.toLowerCase() ?: when {
                        Modifier.isProtected(klass.java.modifiers) -> "protected"
                        else -> "package-private"
                    }
                    reflectionError { "Unable to create $debugVisibility class instances: $klass!" }
                }
                klass.isSealed -> reflectionError { "Unable to create sealed class instances: $klass!" }
                klass.isAbstract -> reflectionError { "Unable to create abstract class instances: $klass!" }
            }
        }
    }

    // Internal for testing
    internal fun clearStrategies() {
        strategies.clear()
    }
}