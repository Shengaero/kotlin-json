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
@file:Suppress("MemberVisibilityCanBePrivate")

package me.kgustave.json.internal

import me.kgustave.json.exceptions.JSSyntaxException
import java.io.IOException
import java.io.Reader

class JSTokener(reader: Reader): AutoCloseable {
    companion object {
        const val NCHAR = 0.toChar()
    }

    constructor(string: String): this(string.reader())

    private val reader = reader.takeIf { it.markSupported() } ?: reader.buffered()
    private var index = 0L
    private var character = 1L
    private var line = 1L
    private var eof = false
    private var prev = NCHAR
    private var usePrevious = false

    private val isAtEnd get() = eof && usePrevious

    fun prev(): Char {
        // make sure we're not using prev already
        checkJson(!usePrevious) { "Stepping back two steps is not supported" }

        index -= 1
        character -= 1
        usePrevious = true
        eof = false
        return prev
    }

    @Throws(IOException::class) fun hasNext(): Boolean {
        next()
        if(isAtEnd) {
            return false
        }
        prev()
        return true
    }

    @Throws(IOException::class) fun nextSymbol(): Char {
        var c: Char
        do c = next() while(c <= ' ' && c.toInt() != 0)
        return c
    }

    @Throws(IOException::class) fun nextText(length: Int): String {
        require(length >= 0) { "Cannot read text with negative length!" }

        if(length == 0) return ""
        val chars = CharArray(length) {
            val next = next()
            if(isAtEnd) syntaxError("Substring bounds error")
            return@CharArray next
        }
        return String(chars)
    }

    @Throws(IOException::class) fun nextString(quote: Char): String = buildString {
        var c: Char
        while(true) {
            c = next()
            when(c) {
                NCHAR, '\r', '\n' -> syntaxError("Unterminated string")
                '\\' -> {
                    c = next()
                    when(c) {
                        'b' -> append('\b')
                        't' -> append('\t')
                        'n' -> append('\n')
                        'f' -> append('\u000C') // Escape for \f in kotlin isn't supported
                        'r' -> append('\r')
                        'u' -> try {
                            append(Integer.parseInt(nextText(4), 16).toChar())
                        } catch(e: NumberFormatException) {
                            syntaxError("Illegal escape", e)
                        }
                        '"', '\'', '\\', '/' -> append(c)
                        else -> syntaxError("Illegal escape")
                    }
                }

                else -> {
                    if(c == quote)
                        return@buildString
                    append(c)
                }
            }
        }
    }

    @Throws(IOException::class) fun nextKey(): String {
        val key = nextValue() as? String ?: syntaxError("Key should begin with quotation")
        val n = nextSymbol()
        if(n != ':') syntaxError("Expected a ':' after a key")
        return key
    }

    @Throws(IOException::class) fun nextValue(): Any? {
        var c = nextSymbol()
        when(c) {
            '"', '\'' -> return nextString(c)

            '{' -> {
                prev()
                return JSObjectImpl(this)
            }

            '[' -> {
                prev()
                return JSArrayImpl(this)
            }
        }
        val string = buildString {
            while(c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0) {
                append(c)
                c = next()
            }
        }.trim()
        prev()
        if(string.isEmpty()) syntaxError("Missing value")
        return stringToValue(string)
    }

    @Throws(IOException::class) fun nextTo(delimiter: Char): String = buildString {
        var c: Char
        while(true) {
            c = next()
            if(c.toInt() == 0) break
            if(c == delimiter || c == '\n' || c == '\r') {
                prev()
                break
            }
            append(c)
        }
    }.trim()

    @Throws(IOException::class) fun nextTo(delimiters: String): String = buildString {
        var c: Char
        while(true) {
            c = next()
            if(c.toInt() == 0) break
            if(delimiters.indexOf(c) >= 0 || c == '\n' || c == '\r') {
                prev()
                break
            }
            append(c)
        }
    }.trim()

    @Throws(IOException::class) fun next(): Char {
        var c: Int
        if(usePrevious) {
            usePrevious = false
            c = prev.toInt()
        } else {
            c = reader.read()

            if(c <= 0) {
                eof = true
                c = 0
            }
        }
        // Next index
        index += 1

        when {
        // newline
            prev == '\r' -> {
                line += 1
                character = (if(c == '\n'.toInt()) 0 else 1).toLong()
            }
        // newline
            c == '\n'.toInt() -> {
                line += 1
                character = 0
            }
        // next character
            else -> character += 1
        }
        // set previous
        prev = c.toChar()
        // return previous
        return prev
    }

    @Throws(IOException::class) fun next(c: Char): Char {
        val n = next()
        if(n != c) syntaxError("Expected '$c' and instead saw '$n'")
        return n
    }

    @Throws(IOException::class) fun skipTo(to: Char): Char {
        var c: Char
        val startIndex = index
        val startCharacter = character
        val startLine = line
        reader.mark(1000000)
        do {
            c = this.next()
            if(c.toInt() == 0) {
                reader.reset() // throws IOException
                index = startIndex
                character = startCharacter
                line = startLine
                return c
            }
        } while(c != to)

        prev()
        return c
    }

    fun syntaxError(msg: String, cause: Throwable? = null): Nothing =
        throw JSSyntaxException("$msg $this", cause)

    fun expectedObjectStart(): Nothing =
        syntaxError("A JSObject text must begin with '{'")

    fun expectedObjectEnd(): Nothing =
        syntaxError("A JSObject text must end with '}'")

    fun expectedArrayStart(): Nothing =
        syntaxError("A JSArray text must start with '['")

    fun expectedArrayEnd(): Nothing =
        syntaxError("A JSObject text must end with '}'")

    fun expectedCommaOrObjectEnd(): Nothing =
        syntaxError("Expected a ',' or '}'")

    fun expectedCommaOrArrayEnd(): Nothing =
        syntaxError("Expected a ',' or ']'")

    override fun close() {
        reader.close()
    }

    override fun toString() = "at $index [character $character line $line]"
}