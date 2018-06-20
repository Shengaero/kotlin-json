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
 * Marks a property that is to be considered optional
 * when returning `null` during serialization.
 *
 * By default the [serializer][me.kgustave.json.reflect.JSSerializer] considers
 * all properties available to be required for serialization.
 *
 * The `@JSOptional` annotation is used to dictate that a property
 * returning `null` should be excluded from the serialization.
 *
 * ```kotlin
 * data class User(
 *     val id: Long,
 *     @JSName("account_name") val accountName: String,
 *     @JSName("display_name") @JSOptional val displayName: String? = null
 * )
 *
 * fun main(args: Array<String>) {
 *     val user1 = User(123, "user1", "userOne")
 *     val user2 = User(321, "user2")
 *
 *     val serializer = JSSerializer()
 *
 *     println("User 1:\n${serializer.serialize(user1).toJsonString(2)}")
 *     println()
 *     println("User 2:\n${serializer.serialize(user2).toJsonString(2)}")
 * }
 *
 * // User 1:
 * // {
 * //   "id": 123,
 * //   "account_name": "user1",
 * //   "display_name": "userOne"
 * // }
 * //
 * // User 2:
 * // {
 * //   "id": 321,
 * //   "account_name": "user2"
 * // }
 * ```
 *
 * @author Kaidan Gustave
 * @since  1.5
 */
@JSSerialization
@MustBeDocumented
@Retention(RUNTIME)
@Target(PROPERTY)
annotation class JSOptional