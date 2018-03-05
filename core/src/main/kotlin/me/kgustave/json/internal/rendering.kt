/*
 * Copyright 2017 Kaidan Gustave
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
package me.kgustave.json.internal

import me.kgustave.json.JSArray
import me.kgustave.json.JSObject
import java.math.BigInteger

internal fun JSObject.buildJsonString(indent: Int, level: Int = 0, newline: Boolean = false): String = buildString {
    val printPretty = indent > 0
    if(printPretty && level > 0 && newline) {
        appendln()
        indent(indent, level)
    }
    append("{")
    var i = 0
    for((k, v) in this@buildJsonString) {
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

        if(i != this@buildJsonString.size - 1)
            append(',')
        i++
    }
    if(printPretty) {
        appendln()
        indent(indent, level)
    }
    append('}')
}

internal fun JSArray.buildJsonString(indent: Int, level: Int = 0, newline: Boolean = false): String = buildString {
    val printPretty = indent > 0
    if(printPretty && level > 0 && newline) {
        appendln()
        indent(indent, level)
    }

    append("[")
    for((i, v) in this@buildJsonString.withIndex()) {
        if(v !is JSObject && v !is JSArray && indent > 0) {
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

        if(i != this@buildJsonString.lastIndex) {
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

private fun convertValue(value: Any?, indent: Int, level: Int, newline: Boolean): String {
    return when(value) {
        null -> value.toString()
        is Boolean -> value.toString()
        is String -> escape(value)
        is Int -> value.toString()
        is Long -> value.toString()
        is Float -> value.toDouble().toString()
        is Double -> value.toString()
        is BigInteger -> value.toString()
        is JSObject -> value.buildJsonString(indent, level + 1, newline)
        is JSArray -> value.buildJsonString(indent, level + 1, newline)
        else -> throw IllegalArgumentException("Cannot convert type: ${value::class}")
    }
}

private inline fun <reified A: Appendable> A.indent(indent: Int, level: Int): A {
    if(level == 0 || indent == 0)
        return this
    append("".padStart(level * indent, ' '))
    return this
}

private inline fun <reified A: Appendable> A.renderString(str: String): A {
    append('"')
    append(escape(str))
    append('"')
    return this
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
                    in '\u2000'..'\u20FF' -> append("\\u${Int.toHexString(c.toInt()).padStart(4, '0')}")
                    else -> append(c)
                }
            }
        }
    }
}

private fun Int.Companion.toHexString(i: Int): String = Integer.toHexString(i)
