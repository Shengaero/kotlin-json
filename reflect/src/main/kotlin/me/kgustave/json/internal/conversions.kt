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
import java.math.BigInteger
import kotlin.reflect.KVisibility
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

internal fun convertObjectToJSObject(obj: Any): JSObject {
    val jsProperties = (obj::class.memberProperties.filter { it.hasAnnotation<JSValue>() }
                            .takeIf { it.isNotEmpty() } ?: obj::class.memberProperties)
        .filter { it.visibility == KVisibility.PUBLIC }
    if(jsProperties.isEmpty()) {
        return emptyJSObject()
    }
    return JSObject {
        jsProperties.forEach {
            val jsName = it.findAnnotation<JSName>()?.value ?: it.name
            val result = if(it.isConst) it.call() else it.call(obj)
            this[jsName] = convertIndividual(result)
        }
    }
}

internal fun convertIndividual(obj: Any?): Any? {
    return when(obj) {
        null -> null
        is String, is Number, is Boolean, is BigInteger, is JSObject, is JSArray -> obj
        is Pair<*, *> -> JSObject { this[obj.first.toString()] = obj.second.toString() }
        is Map<*, *> -> obj.mapKeys { it.toString() }.toJSObject()
        is Collection<*> -> obj.toJSArray()
        is Array<*> -> obj.toJSArray()

        else -> convertObjectToJSObject(obj)
    }
}