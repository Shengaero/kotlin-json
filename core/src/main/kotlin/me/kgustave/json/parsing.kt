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
@file:JvmName("KotlinJsonParsingKt")
package me.kgustave.json

import me.kgustave.json.internal.JSArrayImpl
import me.kgustave.json.internal.JSObjectImpl
import me.kgustave.json.internal.JSTokener
import me.kgustave.json.internal.removeComments
import me.kgustave.json.options.JSParsingOptions
import org.intellij.lang.annotations.Language

/**
 * Parses the provided JSON [String] into a [JSObject] using
 * the specified [JSParsingOptions].
 *
 * @param options The [JSParsingOptions] to parse with.
 * @param json The JSON [String] to parse.
 *
 * @return A [JSObject] parsed from the JSON [String].
 *
 * @throws me.kgustave.json.exceptions.JSSyntaxException If the JSON [String] has invalid syntax.
 */
fun parseJsonObject(options: JSParsingOptions, @Language("JSON") json: String): JSObject {
    val str = if(options.comments) removeComments(json) else json
    return JSTokener(str).use { t -> JSObjectImpl(t) }
}

/**
 * Parses the provided JSON [String] into a [JSObject].
 *
 * @param json The JSON [String] to parse.
 *
 * @return A [JSObject] parsed from the JSON [String].
 *
 * @throws me.kgustave.json.exceptions.JSSyntaxException If the JSON [String] has invalid syntax.
 */
fun parseJsonObject(@Language("JSON") json: String): JSObject {
    return parseJsonObject(JSParsingOptions, json)
}

/**
 * Parses the provided JSON [String] into a [JSArray] using
 * the specified [JSParsingOptions].
 *
 * @param options The [JSParsingOptions] to parse with.
 * @param json The JSON [String] to parse.
 *
 * @return A [JSArray] parsed from the JSON [String].
 *
 * @throws me.kgustave.json.exceptions.JSSyntaxException If the JSON [String] has invalid syntax.
 */
fun parseJsonArray(options: JSParsingOptions, @Language("JSON") json: String): JSArray {
    val str = if(options.comments) removeComments(json) else json
    return JSTokener(str).use { t -> JSArrayImpl(t) }
}

/**
 * Parses the provided JSON [String] into a [JSArray].
 *
 * @param json The JSON [String] to parse.
 *
 * @return A [JSArray] parsed from the JSON [String].
 *
 * @throws me.kgustave.json.exceptions.JSSyntaxException If the JSON [String] has invalid syntax.
 */
fun parseJsonArray(@Language("JSON") json: String): JSArray {
    return parseJsonArray(JSParsingOptions, json)
}
