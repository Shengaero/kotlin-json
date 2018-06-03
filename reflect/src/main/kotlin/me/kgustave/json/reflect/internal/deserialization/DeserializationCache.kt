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
package me.kgustave.json.reflect.internal.deserialization

import me.kgustave.json.JSObject
import me.kgustave.json.reflect.JSDeserializer
import me.kgustave.json.reflect.internal.reflectionError
import java.lang.reflect.Modifier
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KVisibility.INTERNAL
import kotlin.reflect.KVisibility.PUBLIC
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaConstructor
import kotlin.UnsupportedOperationException as UnsupportedException

/**
 * A cache to store strategies for deserialization of
 * [JSON Objects][JSObject] into strong types.
 *
 * @author Kaidan Gustave
 * @since 1.5
 */
internal class DeserializationCache(private val deserializer: JSDeserializer) {
    private val strategies = mutableMapOf<KClass<*>, DeserializationStrategy>()

    internal fun register(klass: KClass<*>) {
        registerInternally(klass)
    }

    internal fun construct(klass: KClass<*>, json: JSObject): Any {
        return strategies.computeIfAbsent(klass) { registerInternally(klass) }.construct(json)
    }

    private fun registerInternally(klass: KClass<*>): DeserializationStrategy {
        if(klass.isData) return registerData(klass) else throw IllegalArgumentException(
            "$klass is not a data class! Non data class construction is currently unsupported!"
        )
    }

    private fun registerData(klass: KClass<*>): DeserializationStrategy {
        require(klass !in strategies) {
            "Construction strategy for $klass has already been registered!"
        }
        val primaryConstructor = requireNotNull(klass.primaryConstructor) {
            // This isn't probably possible, but we check and report
            //for the sake of "if it happens"
            "Data class $klass does not have a primary constructor?"
        }
        checkClassIsValidToConstruct(klass, primaryConstructor)
        val strategy = DeserializationStrategy(deserializer, primaryConstructor)
        strategies[klass] = strategy
        return strategy
    }

    private companion object {
        @JvmStatic private fun checkClassIsValidToConstruct(klass: KClass<*>, constructor: KFunction<*>) {
            // class checks
            when {
                klass.visibility != PUBLIC &&
                klass.visibility != INTERNAL -> {
                    val debugVisibility = klass.visibility?.toString()?.toLowerCase() ?: when {
                        Modifier.isProtected(klass.java.modifiers) -> "protected"
                        else -> "package-private"
                    }
                    reflectionError { "Unable to create $debugVisibility class instances: $klass!" }
                }
                klass.objectInstance !== null ->
                    reflectionError { "Unable to create instance of object class instances: $klass!" }
                klass.isInner ->
                    reflectionError { "Unable to create inner class instances: $klass!" }
                klass.isSealed ->
                    reflectionError { "Unable to create sealed class instances: $klass!" }
                klass.isAbstract ->
                    reflectionError { "Unable to create abstract class instances: $klass!" }
            }

            // constructor checks
            when {
                constructor.visibility != PUBLIC &&
                constructor.visibility != INTERNAL -> {
                    val debugVisibility = constructor.visibility?.toString()?.toLowerCase() ?: run {
                        val jvm = checkNotNull(constructor.javaConstructor) {
                            "Unable to retrieve Java platform constructor"
                        }

                        return@run when {
                            Modifier.isProtected(jvm.modifiers) -> "protected"
                            else -> "package-private"
                        }
                    }
                    reflectionError { "Unable to create $debugVisibility class instances: $klass!" }
                }
            }
        }
    }

    // Internal for testing
    internal fun clearStrategies() {
        strategies.clear()
    }

}