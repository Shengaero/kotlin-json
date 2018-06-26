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
package me.kgustave.json.internal

internal fun Map<String, *>.buildJsonObjectString(
    indent: Int,
    level: Int = 0,
    newline: Boolean = false
): String = buildString {
    val printPretty = indent > 0
    if(printPretty && level > 0 && newline) {
        append('\n')
        indent(indent, level)
    }
    append('{')
    var i = 0
    for((k, v) in this@buildJsonObjectString) {
        if(printPretty) {
            append('\n')
            indent(indent, level + 1)
        }

        renderString(k)
        append(":")
        if(printPretty)
            append(' ')

        val converted = renderValue(v, indent, level, false)

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
        append('\n')
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
        append('\n')
        indent(indent, level)
    }

    append('[')
    for((i, v) in this@buildJsonArrayString.withIndex()) {
        if(valueCheckToNewline(v, indent)) {
            append('\n')
        }
        if(printPretty) {
            indent(indent, level + 1)
        }

        val converted = renderValue(v, indent, level, true)

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
        append('\n')
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
        append('\n')
        indent(indent, level)
    }

    append('[')
    for((i, v) in this@buildJsonArrayString.withIndex()) {
        if(valueCheckToNewline(v, indent)) {
            append('\n')
        }
        if(printPretty) {
            indent(indent, level + 1)
        }

        val converted = renderValue(v, indent, level, true)

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
        append('\n')
        if(level > 0) {
            indent(indent, level)
        }
    }
    append(']')
}

internal fun Sequence<*>.buildJsonArrayString(
    indent: Int,
    level: Int = 0,
    newline: Boolean = false
): String = buildString {
    val printPretty = indent > 0
    if(printPretty && level > 0 && newline) {
        append('\n')
        indent(indent, level)
    }

    append('[')
    var first = true
    this@buildJsonArrayString.forEach { v ->
        if(!first) {
            append(',')
        } else {
            first = false
        }
        if(valueCheckToNewline(v, indent)) {
            append('\n')
        }
        if(printPretty) {
            indent(indent, level + 1)
        }

        val converted = renderValue(v, indent, level, true)

        if(v is String) {
            renderString(converted)
        } else {
            append(converted)
        }
    }
    if(printPretty) {
        append('\n')
        if(level > 0) {
            indent(indent, level)
        }
    }
    append(']')
}

internal fun escape(str: String): String = buildString {
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
                    in '\u2000'..'\u20FF' -> append("\\u${renderHexString(c.toInt()).padStart(4, '0')}")
                    else -> append(c)
                }
            }
        }
    }
}

internal inline fun <reified A: Appendable> A.renderString(str: String): A {
    append('"')
    append(escape(str))
    append('"')
    return this
}

private inline fun <reified A: Appendable> A.indent(indent: Int, level: Int): A {
    if(level != 0 && indent != 0)
        append("".padStart(level * indent))
    return this
}

private fun valueCheckToNewline(v: Any?, indent: Int): Boolean =
    indent > 0 && v !is Map<*, *> && v !is List<*> && v !is Array<*>