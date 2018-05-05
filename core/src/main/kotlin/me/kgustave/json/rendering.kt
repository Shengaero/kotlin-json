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
@file:JvmName("KotlinJsonRenderingKt")
package me.kgustave.json

import me.kgustave.json.internal.buildJsonString

/**
 * Renders the receiver [Map] to a JSON Object string
 * with an optional [indent] factor.
 *
 * @param indent The indent factor, default 0.
 *
 * @return The rendered JSON Object string.
 */
fun Map<String, *>.toJsonString(indent: Int = 0): String = buildJsonString(indent)

/**
 * Renders the receiver [Array] of [Pairs][Pair] to a
 * JSON Object string with an optional [indent] factor.
 *
 * @param indent The indent factor, default 0.
 *
 * @return The rendered JSON Object string.
 */
fun Array<out Pair<String, *>>.toJsonString(indent: Int = 0): String = buildJsonString(indent)

/**
 * Renders the receiver [List] to a JSON Array string
 * with an optional [indent] factor.
 *
 * @param indent The indent factor, default 0.
 *
 * @return The rendered JSON Array string.
 */
fun List<*>.toJsonString(indent: Int = 0): String = buildJsonString(indent)

/**
 * Renders the receiver [Array] to a JSON Array string
 * with an optional [indent] factor.
 *
 * @param indent The indent factor, default 0.
 *
 * @return The rendered JSON Array string.
 */
fun Array<*>.toJsonString(indent: Int = 0): String = buildJsonString(indent)