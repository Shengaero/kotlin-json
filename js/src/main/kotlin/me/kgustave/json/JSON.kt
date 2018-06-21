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
@file:Suppress("USELESS_CAST", "unused")
package me.kgustave.json

import me.kgustave.json.exceptions.JSSyntaxException
import me.kgustave.json.internal.JSArrayImpl
import me.kgustave.json.internal.JSObjectImpl
import me.kgustave.json.internal.buildJsonArrayString
import me.kgustave.json.internal.buildJsonObjectString
import kotlin.js.Json

// Access

@JsName("objectOf")
actual fun jsObjectOf(vararg pairs: Pair<String, Any?>): JSObject = JSObjectImpl(*pairs)

@JsName("arrayOf")
actual fun jsArrayOf(vararg items: Any?): JSArray = JSArrayImpl(items)

// Collections

/**
 * Creates a [JSObject] from the receiver [Map].
 *
 * @receiver The [Map] to create a [JSObject] with.
 *
 * @return A [JSObject] created from the [Map].
 */
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
@JsName("stringifyMap")
actual fun Map<String, *>.toJsonString(indent: Int): String = buildJsonObjectString(indent)

/**
 * Renders the receiver [List] to a JSON Array string
 * with an optional [indent] factor.
 *
 * @param indent The indent factor, default 0.
 *
 * @return The rendered JSON Array string.
 */
@JsName("stringifyList")
actual fun List<*>.toJsonString(indent: Int): String = buildJsonArrayString(indent)

/**
 * Renders the receiver [Array] to a JSON Array string
 * with an optional [indent] factor.
 *
 * @param indent The indent factor, default 0.
 *
 * @return The rendered JSON Array string.
 */
@JsName("stringifyArray")
actual fun Array<*>.toJsonString(indent: Int): String = buildJsonArrayString(indent)

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
@JsName("parseObject")
actual fun parseJsonObject(json: String): JSObject {
    val map = wrapParseError { JSON.parse<Json>(json) }
    return JSObjectImpl(map)
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
@JsName("parseArray")
actual fun parseJsonArray(json: String): JSArray {
    val array = wrapParseError { JSON.parse<Array<dynamic>>(json) }
    return JSArrayImpl(array)
}

private inline fun <R> wrapParseError(block: () -> R): R {
    try {
        return block()
    } catch(t: Throwable) {
        throw JSSyntaxException(t.message, t)
    }
}

// dynamic

/**
 * Converts the provided object into a [JSObject].
 *
 * @param obj An object
 *
 * @return A [JSObject]
 */
fun jsonObject(obj: dynamic): JSObject {
    // all of this has to be casted explicitly
    // in order for intellij to not have a warning
    return when(obj) {
        null -> throw Error("Cannot process null into JSObject!")
        is Number -> throw Error("Cannot convert number into JSObject!")
        is JSObject -> obj as JSObject
        is String -> parseJsonObject(obj as String)
        is Map<String, *> -> (obj as Map<String, *>).toJSObject()
        else -> {
            val json = JSObjectImpl()
            js("Object.getOwnPropertyNames(obj)").iterator().forEach { k ->
                val key = k.toString()
                json[key] = @Suppress("UnsafeCastFromDynamic") obj[key]
            }
            return json
        }
    }
}
