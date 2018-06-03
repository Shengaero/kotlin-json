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
package me.kgustave.json.reflect.internal

import kotlin.reflect.KParameter

/**
 * Stupidly simple one-to-one map for instance parameter
 * used to call a getter or parameter-less function.
 *
 * @author Kaidan Gustave
 * @since  1.5
 */
internal class InstanceParameterMap(parameter: KParameter, instance: Any): Map<KParameter, Any> {
    private val entry: Map.Entry<KParameter, Any> = object: Map.Entry<KParameter, Any> {
        override val key get() = parameter
        override val value get() = instance
        override fun toString() = "$key=$value"
    }
    override val size    get() = 1
    override val values  get() = listOf(entry.value)
    override val keys    get() = setOf(entry.key)
    override val entries get() = setOf(entry)
    override fun isEmpty(): Boolean = false
    override fun containsKey(key: KParameter): Boolean = key == entry.key
    override fun containsValue(value: Any): Boolean = value == entry.value
    override fun get(key: KParameter): Any? = entry.value.takeIf { key in this }

    override fun toString(): String = "($entry)"
}