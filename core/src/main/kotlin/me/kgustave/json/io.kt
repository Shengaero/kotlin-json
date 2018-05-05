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
@file:JvmName("KotlinJsonIOKt") @file:Suppress("unused")

package me.kgustave.json

import me.kgustave.json.internal.JSArrayImpl
import me.kgustave.json.internal.JSObjectImpl
import me.kgustave.json.internal.JSTokener
import java.io.*
import java.net.URL
import java.nio.charset.Charset

/**
 * Reads a the receiver [File] and produces a [JSObject]
 * from the contents.
 *
 * @receiver The [File] to read from.
 *
 * @param charset The [Charset] to read with (default [UTF-8][Charsets.UTF_8]).
 *
 * @return A [JSObject] created from the contents of the [File].
 *
 * @throws java.io.IOException If an IO error occurs while reading the [File].
 * @throws me.kgustave.json.exceptions.JSSyntaxException If a syntax error is detected while reading.
 */
@Throws(IOException::class)
fun File.readJSObject(charset: Charset = Charsets.UTF_8): JSObject {
    return bufferedReader(charset).use { it.readJSObject() }
}

/**
 * Reads a the receiver [File] and produces a [JSArray]
 * from the contents.
 *
 * @receiver The [File] to read from.
 *
 * @param charset The [Charset] to read with (default [UTF-8][Charsets.UTF_8]).
 *
 * @return A [JSArray] created from the contents of the [File].
 *
 * @throws java.io.IOException If an IO error occurs while reading the [File].
 * @throws me.kgustave.json.exceptions.JSSyntaxException If a syntax error is detected while reading.
 */
@Throws(IOException::class)
fun File.readJSArray(charset: Charset = Charsets.UTF_8): JSArray {
    return bufferedReader(charset).use { it.readJSArray() }
}

/**
 * [Opens a stream][URL.openStream] to the receiver [URL] and
 * produces a [JSObject] from the read content of the stream.
 *
 * @receiver The [URL] to read from.
 *
 * @param charset The [Charset] to read with (default [UTF-8][Charsets.UTF_8]).
 *
 * @return A [JSObject] created from the contents of the stream.
 *
 * @throws java.io.IOException If an IO error occurs while reading from the [URL].
 * @throws me.kgustave.json.exceptions.JSSyntaxException If a syntax error is detected while reading.
 */
@Throws(IOException::class)
fun URL.readJSObject(charset: Charset = Charsets.UTF_8): JSObject {
    return openStream().use { it.readJSObject(charset) }
}

/**
 * [Opens a stream][URL.openStream] to the receiver [URL] and
 * produces a [JSArray] from the read content of the stream.
 *
 * @receiver The [URL] to read from.
 *
 * @param charset The [Charset] to read with (default [UTF-8][Charsets.UTF_8]).
 *
 * @return A [JSArray] created from the contents of the stream.
 *
 * @throws java.io.IOException If an IO error occurs while reading from the [URL].
 * @throws me.kgustave.json.exceptions.JSSyntaxException If a syntax error is detected while reading.
 */
@Throws(IOException::class)
fun URL.readJSArray(charset: Charset = Charsets.UTF_8): JSArray {
    return openStream().use { it.readJSArray(charset) }
}

/**
 * Reads from the receiver [InputStream] and produces a
 * [JSObject] from the it's contents.
 *
 * @receiver The [InputStream] to read from.
 *
 * @param charset The [Charset] to read with (default [UTF-8][Charsets.UTF_8]).
 *
 * @return A [JSObject] created from the contents of the stream.
 *
 * @throws java.io.IOException If an IO error occurs while reading from the [InputStream].
 * @throws me.kgustave.json.exceptions.JSSyntaxException If a syntax error is detected while reading.
 */
@Throws(IOException::class)
fun InputStream.readJSObject(charset: Charset = Charsets.UTF_8): JSObject {
    return bufferedReader(charset).use { it.readJSObject() }
}

/**
 * Reads from the receiver [InputStream] and produces a
 * [JSArray] from the it's contents.
 *
 * @receiver The [InputStream] to read from.
 *
 * @param charset The [Charset] to read with (default [UTF-8][Charsets.UTF_8]).
 *
 * @return A [JSArray] created from the contents of the stream.
 *
 * @throws java.io.IOException If an IO error occurs while reading from the [InputStream].
 * @throws me.kgustave.json.exceptions.JSSyntaxException If a syntax error is detected while reading.
 */
@Throws(IOException::class)
fun InputStream.readJSArray(charset: Charset = Charsets.UTF_8): JSArray {
    return bufferedReader(charset).use { it.readJSArray() }
}

/**
 * Uses the receiver [Reader] to read and produce a [JSObject].
 *
 * @receiver The [Reader] to use.
 *
 * @return A [JSObject] created from the contents of the stream.
 *
 * @throws java.io.IOException If an IO error occurs while reading using the [Reader].
 * @throws me.kgustave.json.exceptions.JSSyntaxException If a syntax error is detected while reading.
 */
@Throws(IOException::class)
fun Reader.readJSObject(): JSObject {
    return JSTokener(this).use { JSObjectImpl(it) }
}

/**
 * Uses the receiver [Reader] to read and produce a [JSArray].
 *
 * @receiver The [Reader] to use.
 *
 * @return A [JSArray] created from the contents of the stream.
 *
 * @throws java.io.IOException If an IO error occurs while reading using the [Reader].
 * @throws me.kgustave.json.exceptions.JSSyntaxException If a syntax error is detected while reading.
 */
@Throws(IOException::class)
fun Reader.readJSArray(): JSArray {
    return JSTokener(this).use { JSArrayImpl(it) }
}
