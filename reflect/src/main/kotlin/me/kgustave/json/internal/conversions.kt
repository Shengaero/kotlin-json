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
@file:JvmName("Internal_ConversionsKt")
package me.kgustave.json.internal

import me.kgustave.json.*
import me.kgustave.json.annotation.JSName
import me.kgustave.json.annotation.JSValue
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.KVisibility
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.kotlinFunction

// In order to maintain consistency between modules, we
//load and store the internal conversion function via reflection
//and use that instead of just copy-pasting code.
private lateinit var CONVERT_VALUE_FUN: KFunction<*>

internal fun convertObjectToJSObject(obj: Any): JSObject {
    val jsProperties = obj::class.jsProperties
    if(jsProperties.isEmpty()) {
        return jsObjectOf()
    }
    return JSObject {
        jsProperties.forEach {
            val jsName = it.findAnnotation<JSName>()?.value ?: it.name
            val result = if(it.isConst) it.call() else it.call(obj)
            jsName to try { convertIndividual(result) } catch(e: Throwable) {
                if(result !== null) {
                    convertObjectToJSObject(result)
                } else throw IllegalStateException("Could not convert $obj using reflection!")
            }
        }
    }
}

internal val KClass<*>.jsProperties: List<KProperty<*>> get() {
    val members = memberProperties
    val unfiltered = members.filter { it.hasAnnotation<JSValue>() }.takeIf { it.isNotEmpty() } ?: members
    return unfiltered.filter { it.visibility == KVisibility.PUBLIC }
}

internal fun convertIndividual(obj: Any?): Any? {
    if(!::CONVERT_VALUE_FUN.isInitialized) {
        val clazz = Class.forName("me.kgustave.json.internal.Internal_ConversionKt")
        val convertValue = clazz.methods.first { it.name == "convertValue" }
        CONVERT_VALUE_FUN = convertValue.kotlinFunction!!
    }

    return CONVERT_VALUE_FUN.call(obj)
}