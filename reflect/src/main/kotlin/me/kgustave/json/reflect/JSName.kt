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
package me.kgustave.json.reflect

import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*

/**
 * Used to key for a JSON object value explicitly.
 *
 * `@JSName` specifies the key used with a [JSObject][me.kgustave.json.JSObject] provided when
 * serializing/deserializing it into/from a class instance.
 *
 * This is mostly useful for values who's names may not represent a Kotlin/Java based naming
 * scheme for things like web-APIs.
 *
 * Take for example the following class definition:
 *
 * ```kotlin
 * data class Order(val id: Long, @JSName("customer_name") val customer: String)
 * ```
 *
 * ## Deserialization
 *
 * Normally, constructor parameter values are resolved during deserialization based on their
 * [parameter name][kotlin.reflect.KParameter.name] as the key retrieved from a `JSObject`.
 *
 * Because of this, there must exist a means of specifying the key to use.
 *
 * Take for example the following JSON object representing a customer's order on a website:
 *
 * ```json
 * {
 *   "id": 2252912595123,
 *   "customer_name": "Mark Lee"
 * }
 * ```
 *
 * Notice the name of the `customer_name` value. Normally for this to be mapped to a parameter,
 * the parameter would need to be `customer_name: String`.
 *
 * For this to be properly deserialized without a change in the parameter name, you must
 * apply the `@JSName` with the corresponding value key to use as shown above.
 *
 * ## Serialization
 *
 * Much like deserialization, `@JSName` can be used to specify the output key name
 * for a property value to serialize into a `JSObject` output from the serialization
 * process.
 *
 * Using the same example shown above in:
 *
 * ```kotlin
 * fun main(args: Array<String>) {
 *     val serializer = JSSerializer()
 *     val order: Order = findAnyOrder()
 *     val json = serializer.serialize(order)
 *     println("customer" in json)
 *     println("customer_name" in json)
 * }
 *
 * // false
 * // true
 * ```
 *
 * @author Kaidan Gustave
 * @since  1.5
 */
@JSSerialization
@JSDeserialization
@MustBeDocumented
@Retention(RUNTIME)
@Target(PROPERTY, VALUE_PARAMETER)
annotation class JSName(val value: String)