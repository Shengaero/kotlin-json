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
import me.kgustave.json.reflect.internal.NotAValue
import me.kgustave.json.reflect.internal.reflectionError
import kotlin.reflect.KFunction

internal class ParameterResolver(
    private val constructor: KFunction<*>,
    private val optional: Boolean,
    private val nullable: Boolean,
    private val key: String,
    private val call: (JSObject, String) -> Any?
) {
    internal fun resolve(json: JSObject): Any? {
        if(optional && key !in json) return NotAValue
        val value = call(json, key)
        if(value === null) {
            if(nullable) return null
            reflectionError { "Could not resolve '$key' value for $constructor!" }
        }
        return value
    }
}