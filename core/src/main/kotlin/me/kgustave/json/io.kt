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
@file:JvmName("KotlinJsonIOKt")
package me.kgustave.json

import me.kgustave.json.internal.JSArrayImpl
import me.kgustave.json.internal.JSObjectImpl
import java.io.*
import java.net.URL
import java.nio.charset.Charset

@SinceKotlin("1.2")
@Throws(IOException::class)
fun File.readJSObject(charset: Charset = Charsets.UTF_8): JSObject {
    return bufferedReader(charset).use { it.readJSObject() }
}

@SinceKotlin("1.2")
@Throws(IOException::class)
fun File.readJSArray(charset: Charset = Charsets.UTF_8): JSArray {
    return bufferedReader(charset).use { it.readJSArray() }
}

@SinceKotlin("1.2")
@Throws(IOException::class)
fun URL.readJSObject(charset: Charset = Charsets.UTF_8): JSObject {
    return openStream().use { it.readJSObject(charset) }
}

@SinceKotlin("1.2")
@Throws(IOException::class)
fun URL.readJSArray(charset: Charset = Charsets.UTF_8): JSArray {
    return openStream().use { it.readJSArray(charset) }
}

@SinceKotlin("1.2")
@Throws(IOException::class)
fun InputStream.readJSObject(charset: Charset = Charsets.UTF_8): JSObject {
    return bufferedReader(charset).use { it.readJSObject() }
}

@SinceKotlin("1.2")
@Throws(IOException::class)
fun InputStream.readJSArray(charset: Charset = Charsets.UTF_8): JSArray {
    return bufferedReader(charset).use { it.readJSArray() }
}

@SinceKotlin("1.2")
@Throws(IOException::class)
fun Reader.readJSObject(): JSObject {
    return JSTokener(this).use { JSObjectImpl(it) }
}

@SinceKotlin("1.2")
@Throws(IOException::class)
fun Reader.readJSArray(): JSArray {
    return JSTokener(this).use { JSArrayImpl(it) }
}
