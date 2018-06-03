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

import me.kgustave.json.reflect.JSSerializer
import me.kgustave.json.reflect.internal.InstanceParameterMap
import me.kgustave.json.reflect.internal.NotAValue
import kotlin.reflect.KProperty1
import kotlin.reflect.full.instanceParameter

/**
 * @author Kaidan Gustave
 * @since  1.5
 */
internal class PropertyHandler(
    private val serializer: JSSerializer,
    private val property: KProperty1<out Any, Any?>,
    private val optional: Boolean
) {
    internal fun handle(instance: Any): Any? {
        val parameters = InstanceParameterMap(property.instanceParameter!!, instance)
        val returns = property.callBy(parameters)
        if(returns === null) return if(optional) NotAValue else null
        return serializer.convertValue(returns)
    }
}