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
@file:JvmName("JSON")
@file:Suppress("FunctionName")
package me.kgustave.json

import me.kgustave.json.internal.*
import me.kgustave.json.options.JSParsingOptions
import org.intellij.lang.annotations.Language
import java.lang.StringBuilder

// Access

/**
 * Creates a [JSArray] using the provided [items].
 *
 * @param items The items to create a [JSArray] with.
 *
 * @return A new [JSArray].
 */
@JvmName("arrayOf")
@SafeVarargs actual fun jsArrayOf(vararg items: Any?): JSArray {
    return JSArrayImpl(items.mapTo(ArrayList(items.size), ::convertValue))
}

/**
 * Creates a [JSObject] using the provided key-value [pairs].
 *
 * @param pairs The key-value [Pairs][Pair] to create a [JSObject] with.
 *
 * @return A new [JSObject].
 */
@JvmName("objectOf")
@SafeVarargs actual fun jsObjectOf(vararg pairs: Pair<String, Any?>): JSObject {
    return pairs.associateByTo(JSObjectImpl(), { it.first }, { convertValue(it.second) })
}

// Collections

/**
 * Creates a [JSObject] from the receiver [Map].
 *
 * @receiver The [Map] to create a [JSObject] with.
 *
 * @return A [JSObject] created from the [Map].
 */
@JvmName("object")
actual fun Map<String, *>.toJSObject(): JSObject {
    return this.mapValuesTo(JSObjectImpl()) { containsValue(it.value) }
}

/**
 * Creates a [JSArray] from the receiver [Collection].
 *
 * @receiver The [Collection] to create a [JSArray] with.
 *
 * @return A [JSArray] created from the [Collection].
 */
@JvmName("array")
actual fun Collection<*>.toJSArray(): JSArray {
    return this.mapTo(JSArrayImpl()) { it }
}

/**
 * Creates a [JSArray] from the receiver [List].
 *
 * @receiver The [List] to create a [JSArray] with.
 *
 * @return A [JSArray] created from the [List].
 */
@JvmName("array")
actual fun List<*>.toJSArray(): JSArray {
    return (this as Collection<*>).toJSArray()
}

/**
 * Creates a [JSArray] from the receiver [Array].
 *
 * @receiver The [Array] to create a [JSArray] with.
 *
 * @return A [JSArray] created from the [Array].
 */
@JvmName("array")
actual fun Array<*>.toJSArray(): JSArray {
    return JSArrayImpl(this)
}

// Rendering

/**
 * Renders the receiver [Map] to a JSON Object string
 * with an optional [indent] factor.
 *
 * @param indent The indent factor, default 0.
 *
 * @return The rendered JSON Object string.
 */
@JvmName("stringify")
@JvmOverloads actual fun Map<String, *>.toJsonString(indent: Int): String = buildJsonObjectString(indent)

/**
 * Renders the receiver [List] to a JSON Array string
 * with an optional [indent] factor.
 *
 * @param indent The indent factor, default 0.
 *
 * @return The rendered JSON Array string.
 */
@JvmName("stringify")
@JvmOverloads actual fun List<*>.toJsonString(indent: Int): String = buildJsonArrayString(indent)

/**
 * Renders the receiver [Array] to a JSON Array string
 * with an optional [indent] factor.
 *
 * @param indent The indent factor, default 0.
 *
 * @return The rendered JSON Array string.
 */
@JvmName("stringify")
@JvmOverloads actual fun Array<*>.toJsonString(indent: Int): String = buildJsonArrayString(indent)

// Parsing

/**
 * Parses the provided JSON [String] into a [JSObject].
 *
 * @param json The JSON [String] to parse.
 *
 * @return A [JSObject] parsed from the JSON [String].
 *
 * @throws me.kgustave.json.exceptions.JSSyntaxException If the JSON [String] has invalid syntax.
 */
@JvmName("parseObject")
actual fun parseJsonObject(@Language("JSON") json: String): JSObject {
    return parseJsonObject(JSParsingOptions, json)
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
@JvmName("parseArray")
actual fun parseJsonArray(@Language("JSON") json: String): JSArray {
    return parseJsonArray(JSParsingOptions, json)
}

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
@JvmName("parseObject")
fun parseJsonObject(options: JSParsingOptions, @Language("JSON") json: String): JSObject {
    val str = if(options.comments) removeComments(json) else json
    return JSTokener(str).use { t -> JSObjectImpl(t) }
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
@JvmName("parseArray")
fun parseJsonArray(options: JSParsingOptions, @Language("JSON") json: String): JSArray {
    val str = if(options.comments) removeComments(json) else json
    return JSTokener(str).use { t -> JSArrayImpl(t) }
}

// Writer

/**
 * Creates a new [JSWriter] that wraps the specified [writer].
 *
 * @param writer The [Appendable] to wrap into a [JSWriter].
 *
 * @return A new [JSWriter].
 */
@JvmName("writer")
@JvmOverloads fun JSWriter(writer: Appendable = StringBuilder()): JSWriter = JSWriterImpl(writer)

/**
 * [Starts][JSWriter.obj] and [ends][JSWriter.endObj] a JSON Object
 * with the receiver [JSWriter].
 *
 * @receiver A [JSWriter].
 *
 * @param block The block to apply to the receiver inside the new object.
 *
 * @return The receiver [JSWriter].
 */
@JSONDsl inline fun JSWriter.obj(block: JSWriter.() -> Unit): JSWriter {
    obj()
    if(this is JSWriterImpl) {
        val prevLock = lockManualEnd
        lockManualEnd = true
        this.block()
        lockManualEnd = prevLock
    } else this.block()
    return endObj()
}

/**
 * [Starts][JSWriter.array] and [ends][JSWriter.endArray] a JSON Array
 * with the receiver [JSWriter].
 *
 * @receiver A [JSWriter].
 *
 * @param block The block to apply to the receiver inside the new array.
 *
 * @return The receiver [JSWriter].
 */
@JSONDsl inline fun JSWriter.array(block: JSWriter.() -> Unit): JSWriter {
    array()
    if(this is JSWriterImpl) {
        val prevLock = lockManualEnd
        lockManualEnd = true
        this.block()
        lockManualEnd = prevLock
    } else this.block()
    return endArray()
}