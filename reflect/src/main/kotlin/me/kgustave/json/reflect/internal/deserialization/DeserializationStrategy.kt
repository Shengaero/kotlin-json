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

import me.kgustave.json.JSArray
import me.kgustave.json.JSObject
import me.kgustave.json.reflect.JSDeserializer
import me.kgustave.json.reflect.JSName
import me.kgustave.json.reflect.internal.JSValueType
import me.kgustave.json.reflect.internal.NotAValue
import me.kgustave.json.reflect.internal.reflectionError
import java.lang.reflect.InvocationTargetException
import java.util.*
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KTypeProjection.Companion.invariant
import kotlin.reflect.full.*
import kotlin.reflect.jvm.javaConstructor
import kotlin.reflect.jvm.jvmErasure

/**
 * Functional implementation that stores
 *
 * @author Kaidan Gustave
 * @since  1.5
 */
internal class DeserializationStrategy(
    private val deserializer: JSDeserializer,
    private val constructor: KFunction<*>
) {
    private companion object {
        private val stringValueMapType = Map::class.createType(
            arguments = listOf(
                invariant(String::class.createType()),
                invariant(Any::class.createType(nullable = true))
            )
        )
        private fun callFunction(block: (JSObject, String) -> Any?) = block
    }

    init {
        check(constructor.javaConstructor !== null) { "$constructor was not a constructor!?" }
    }

    private val parameters = constructor.valueParameters

    private val parameterResolvers = parameters.associateBy({ it }) { parameter ->
        val type = parameter.type
        val valueType = when {
            type.isSubtypeOf(String::class.starProjectedType) -> JSValueType.STRING
            type.isSubtypeOf(Int::class.starProjectedType) -> JSValueType.INT
            type.isSubtypeOf(Long::class.starProjectedType) -> JSValueType.LONG
            type.isSubtypeOf(Float::class.starProjectedType) -> JSValueType.FLOAT
            type.isSubtypeOf(Double::class.starProjectedType) -> JSValueType.DOUBLE
            type.isSubtypeOf(stringValueMapType) -> JSValueType.OBJECT
            type.jvmErasure.let {
                it == List::class ||
                it == MutableList::class ||
                it == ArrayList::class ||
                it == LinkedList::class ||
                it.isSubclassOf(JSArray::class)
            } -> JSValueType.ARRAY

            // illegal list subclass
            type.jvmErasure.isSubclassOf(List::class) -> reflectionError {
                "${type.jvmErasure} is not a valid list type for parameter " +
                "#${parameter.index} (${parameter.name})."
            }

            else -> JSValueType.OTHER
        }

        val call = when(valueType) {
            JSValueType.OTHER -> {
                val klass = type.jvmErasure
                callFunction { json, str -> json.optObj(str)?.let { deserializer.deserialize(it, klass) } }
            }

            JSValueType.ARRAY -> {
                if(type.jvmErasure.isSubclassOf(JSArray::class)) valueType.call else {
                    val typeArguments = type.arguments
                    val klass = when(typeArguments.size) {
                        0 -> reflectionError {
                            "Cannot provide values to list type without " +
                            "explicit parameter type arguments!"
                        }

                        1 -> typeArguments[0].type?.jvmErasure

                        else -> reflectionError { "List type intention is too complicated to determine!" }
                    }

                    callFunction { json, str ->
                        json.optArray(str)?.map {
                            // if klass is null this is star projected list type
                            //so anything can be injected into the list
                            if(it !is JSObject || klass === null) return@map it
                            return@map deserializer.deserialize(it, klass)
                        }
                    }

                }
            }

            else -> valueType.call
        }

        val key = parameter.findAnnotation<JSName>()?.value ?: parameter.name!! // param name shouldn't be null

        return@associateBy ParameterResolver(
            constructor = constructor,
            optional = parameter.isOptional,
            nullable = parameter.type.isMarkedNullable,
            key = key,
            call = call
        )
    }

    internal fun construct(json: JSObject): Any {
        val map = resolveParameters(json)
        try {
            return constructor.callBy(map) ?: run {
                // Not sure if this can happen, probably can't
                //but I check anyways because it is better than NPE
                //if it ever does.
                reflectionError { "Call to constructor $constructor returned null!" }
            }
        } catch(e: InvocationTargetException) {
            // catch, unwrap, and throw the target exception
            throw e.targetException ?: e
        }
    }

    private fun resolveParameters(json: JSObject): Map<KParameter, Any?> {
        val map = mutableMapOf<KParameter, Any?>()
        for(valueParameter in parameters) {
            val resolver = checkNotNull(parameterResolvers[valueParameter]) {
                "Unregistered parameter resolver for parameter: $valueParameter"
            }
            val value = resolver.resolve(json)
            if(value === NotAValue) continue
            map[valueParameter] = value
        }
        return map
    }
}
