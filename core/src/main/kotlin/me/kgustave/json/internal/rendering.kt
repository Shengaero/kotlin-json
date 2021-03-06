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
@file:JvmName("Internal_RenderingKt")
@file:Suppress("NOTHING_TO_INLINE")
package me.kgustave.json.internal

import me.kgustave.json.JSArray
import me.kgustave.json.JSObject
import java.lang.IllegalStateException
import java.math.BigInteger
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

internal fun Map<String, *>.buildJsonObjectString(
    indent: Int,
    level: Int = 0,
    newline: Boolean = false
): String = buildString {
    val printPretty = indent > 0
    if(printPretty && level > 0 && newline) {
        appendln()
        indent(indent, level)
    }
    append('{')
    var i = 0
    for((k, v) in this@buildJsonObjectString) {
        if(printPretty) {
            appendln()
            indent(indent, level + 1)
        }

        renderString(k)
        append(":")
        if(printPretty)
            append(' ')

        val converted = convertValue(v, indent, level, false)

        if(v is String) {
            renderString(converted)
        } else {
            append(converted)
        }

        if(i != this@buildJsonObjectString.size - 1)
            append(',')
        i++
    }
    if(printPretty) {
        appendln()
        indent(indent, level)
    }
    append('}')
}

internal fun List<*>.buildJsonArrayString(
    indent: Int,
    level: Int = 0,
    newline: Boolean = false
): String = buildString {
    val printPretty = indent > 0
    if(printPretty && level > 0 && newline) {
        appendln()
        indent(indent, level)
    }

    append('[')
    for((i, v) in this@buildJsonArrayString.withIndex()) {
        if(valueCheckToNewline(v, indent)) {
            appendln()
        }
        if(printPretty) {
            indent(indent, level + 1)
        }

        val converted = convertValue(v, indent, level, true)

        if(v is String) {
            renderString(converted)
        } else {
            append(converted)
        }

        if(i != this@buildJsonArrayString.lastIndex) {
            append(',')
        }
    }
    if(printPretty) {
        appendln()
        if(level > 0) {
            indent(indent, level)
        }
    }
    append(']')
}

internal fun Array<*>.buildJsonArrayString(
    indent: Int,
    level: Int = 0,
    newline: Boolean = false
): String = buildString {
    val printPretty = indent > 0
    if(printPretty && level > 0 && newline) {
        appendln()
        indent(indent, level)
    }

    append('[')
    for((i, v) in this@buildJsonArrayString.withIndex()) {
        if(valueCheckToNewline(v, indent)) {
            appendln()
        }
        if(printPretty) {
            indent(indent, level + 1)
        }

        val converted = convertValue(v, indent, level, true)

        if(v is String) {
            renderString(converted)
        } else {
            append(converted)
        }

        if(i != this@buildJsonArrayString.lastIndex) {
            append(',')
        }
    }
    if(printPretty) {
        appendln()
        if(level > 0) {
            indent(indent, level)
        }
    }
    append(']')
}

internal inline fun <reified A: Appendable> A.renderString(str: String): A {
    append('"')
    append(escape(str))
    append('"')
    return this
}

internal fun convertValue(value: Any?, indent: Int, level: Int, newline: Boolean): String {
    return when(value) {
        null -> value.toString()
        is Boolean -> value.toString()
        is String -> escape(value)
        is Int -> value.toString()
        is Long -> value.toString()
        is Float -> value.toDouble().toString()
        is Double -> value.toString()
        is BigInteger -> value.toString()
        is AtomicInteger -> value.get().toString()
        is AtomicLong -> value.get().toString()
        is JSObject -> value.buildJsonObjectString(indent, level + 1, newline)
        is JSArray -> value.buildJsonArrayString(indent, level + 1, newline)
        is Map<*, *> -> {
            @Suppress("UNCHECKED_CAST")
            // This might be faster to cast if the typing is correct
            val realMap = value as? Map<String, *> ?: value.mapKeys { "${it.key}" }
            return realMap.buildJsonObjectString(indent, level + 1, newline)
        }
        is List<*> -> value.buildJsonArrayString(indent, level + 1, newline)
        is Array<*> -> value.buildJsonArrayString(indent, level + 1, newline)
        else -> throw IllegalStateException("Cannot convert type: ${value::class}")
    }
}

private inline fun <reified A: Appendable> A.indent(indent: Int, level: Int): A {
    if(level == 0 || indent == 0)
        return this
    append(String(CharArray(level * indent) { ' ' }))
    return this
}

private fun valueCheckToNewline(v: Any?, indent: Int): Boolean {
    return v !is Map<*, *> && v !is List<*> && v !is Array<*> && indent > 0
}

private fun escape(str: String): String = buildString {
    for(c in str) {
        when(c) {
            '"'      -> append("\\$c")
            '\\'     -> append("$c$c")
            '\n'     -> append("\\n")
            '\r'     -> append("\\r")
            '\t'     -> append("\\t")
            '\b'     -> append("\\b")
            '\u000c' -> append("\\f")

            else -> {
                when(c) {
                    in '\u0000'..'\u001F',
                    in '\u007F'..'\u009F',
                    in '\u2000'..'\u20FF' -> append("\\u${Integer.toHexString(c.toInt()).padStart(4, '0')}")
                    else -> append(c)
                }
            }
        }
    }
}
