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
import me.kgustave.json.internal.*
import kotlin.js.Json

// Access

/**
 * Creates a [JSObject] using the provided key-value [pairs].
 *
 * @param pairs The key-value [Pairs][Pair] to create a [JSObject] with.
 *
 * @return A new [JSObject].
 */
@JsName("objectOf")
actual fun jsObjectOf(vararg pairs: Pair<String, Any?>): JSObject = JSObjectImpl(*pairs)

/**
 * Creates a [JSArray] using the provided [items].
 *
 * @param items The items to create a [JSArray] with.
 *
 * @return A new [JSArray].
 */
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
@JsName("fromMap")
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
@JsName("fromCollection")
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
@JsName("fromList")
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
@JsName("fromArray")
actual fun Array<*>.toJSArray(): JSArray {
    return JSArrayImpl(this)
}

/**
 * Produces a [JSArray] from the receiver [Sequence].
 *
 * @receiver The [Sequence] to create a [JSArray] with.
 *
 * @return A [JSArray] created from the [Sequence].
 */
@JsName("fromSequence")
actual fun Sequence<*>.toJSArray(): JSArray {
    return this.toCollection(JSArrayImpl())
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

/**
 * Renders the receiver [Sequence] to a JSON Array string
 * with an optional [indent] factor.
 *
 * @param indent The indent factor, default 0.
 *
 * @return The rendered JSON Array string.
 */
@JsName("stringifySequence")
actual fun Sequence<*>.toJsonString(indent: Int): String = buildJsonArrayString(indent)

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
 * @return A [JSObject].
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

// Files

@Suppress("UnsafeCastFromDynamic")
private val fs: dynamic by lazy { js("require('fs')") }
private const val fsLink = "https://nodejs.org/api/fs.html"

/**
 * Reads a [JSObject] from a file at the provided [source][src].
 *
 * Internally this function uses the file system (fs) API in node.js,
 * and thus it is only available when using commonjs.
 *
 * The source to read from may be either a [URL][org.w3c.dom.url.URL] or a path [String].
 *
 * Additionally, a string name for the encoding may be specified. By
 * default this is `UTF-8`. This must be specified in order for fs to
 * return a string value to parse. Invalid or empty encoding strings
 * will cause unexpected behavior or issues.
 *
 * @param src The source to read from.
 * @param encoding The encoding to read with. Default `UTF-8`.
 *
 * @return A [JSObject] read from the source.
 */
fun readJSObject(src: dynamic, encoding: String = "utf8"): JSObject {
    return parseJsonObject(readString(src, encoding))
}

/**
 * Reads a [JSArray] from a file at the provided [source][src].
 *
 * Internally this function uses the file system (fs) API in node.js,
 * and thus it is only available when using commonjs.
 *
 * The source to read from may be either a [URL][org.w3c.dom.url.URL] or a path [String].
 *
 * Additionally, a string name for the encoding may be specified. By
 * default this is `UTF-8`. This must be specified in order for fs to
 * return a string value to parse. Invalid or empty encoding strings
 * will cause unexpected behavior or issues.
 *
 * @param src The source to read from.
 * @param encoding The encoding to read with. Default `UTF-8`.
 *
 * @return A [JSArray] read from the source.
 */
fun readJSArray(src: dynamic, encoding: String = "utf8"): JSArray {
    return parseJsonArray(readString(src, encoding))
}

/**
 * Writes the receiver [JSObject] to the specified [output][out].
 *
 * Internally this function uses the file system (fs) API in node.js,
 * and thus it is only available when using commonjs.
 *
 * Currently, only [URL][org.w3c.dom.url.URL] and [String] are safely supported by the
 * kotlin-json API, however this may also any variables that would normally be accepted
 * by [fs.writeFile](https://nodejs.org/api/fs.html#fs_fs_writefile_file_data_options_callback).
 *
 * Additionally, a string name for the encoding may be specified. By
 * default this is `UTF-8`. This must be specified in order for fs to
 * return a string value to parse. Invalid or empty encoding strings
 * will cause unexpected behavior or issues.
 *
 * It's also worth noting that this function does not truncate any existing
 * content. In order to perform this, a library user might look into
 * avoiding usage of this function in favor of a more dedicated file handling
 * library.
 *
 * @receiver The [JSObject] to write.
 * @param out The output destination to write to.
 * @param indent The indent to write with. Default 0.
 * @param encoding The encoding to read with. Default `UTF-8`.
 */
fun JSObject.writeTo(out: dynamic, indent: Int = 0, encoding: String = "utf8") {
    writeString(this.toJsonString(indent), out, encoding)
}

/**
 * Writes the receiver [JSArray] to the specified [output][out].
 *
 * Internally this function uses the file system (fs) API in node.js,
 * and thus it is only available when using commonjs.
 *
 * Currently, only [URL][org.w3c.dom.url.URL] and [String] are safely supported by the
 * kotlin-json API, however this may also any variables that would normally be accepted
 * by [fs.writeFile](https://nodejs.org/api/fs.html#fs_fs_writefile_file_data_options_callback).
 *
 * Additionally, a string name for the encoding may be specified. By
 * default this is `UTF-8`. This must be specified in order for fs to
 * return a string value to parse. Invalid or empty encoding strings
 * will cause unexpected behavior or issues.
 *
 * It's also worth noting that this function does not truncate any existing
 * content. In order to perform this, a library user might look into
 * avoiding usage of this function in favor of a more dedicated file handling
 * library.
 *
 * @receiver The [JSArray] to write.
 * @param out The output destination to write to.
 * @param indent The indent to write with. Default 0.
 * @param encoding The encoding to read with. Default `UTF-8`.
 */
fun JSArray.writeTo(out: dynamic, indent: Int = 0, encoding: String = "utf8") {
    writeString(this.toJsonString(indent), out, encoding)
}

private fun readString(src: dynamic, @Suppress("UNUSED_PARAMETER") encoding: String): String {
    require(isNodeEcosystem()) { "Cannot read files without access to fs API: $fsLink" }
    return fs.readFileSync(src, js("""{encoding: encoding}""")) as? String ?:
           throw Error("When reading $src, the return value was not a string!")
}

private fun writeString(content: String, src: dynamic, @Suppress("UNUSED_PARAMETER") encoding: String) {
    fs.writeFileSync(src, content, js("""{encoding: encoding}"""))
}
