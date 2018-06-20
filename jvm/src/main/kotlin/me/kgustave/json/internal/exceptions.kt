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
@file:JvmName("Actual_Jvm_ExceptionsKt")
package me.kgustave.json.internal

import kotlin.reflect.KClass

internal actual fun isNotType(key: String, value: Any, type: KClass<*>): String {
    return "Value with key '$key' was ${value::class.javaPrimitiveType ?: value::class.java} " +
           "instead of ${type.javaPrimitiveType ?: type.java}"
}

internal actual fun isNotType(index: Int, value: Any, type: KClass<*>): String {
    return "Value with key '$index' was ${value::class.javaPrimitiveType ?: value::class.java} " +
           "instead of ${type.javaPrimitiveType ?: type.java}"
}