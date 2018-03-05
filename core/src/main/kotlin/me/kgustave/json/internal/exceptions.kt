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
@file:JvmName("Internal_ExceptionsKt")
package me.kgustave.json.internal

import me.kgustave.json.JSTokener
import me.kgustave.json.exceptions.JSException
import me.kgustave.json.exceptions.JSSyntaxException

internal inline fun checkJson(condition: Boolean, msg: () -> String) {
    if(!condition) {
        throw JSException(msg())
    }
}

internal fun JSTokener.syntaxError(message: String, cause: Throwable? = null): Nothing {
    throw JSSyntaxException("$message $this", cause)
}

internal inline fun <reified R> tryWrap(block: () -> R): R {
    try {
        return block()
    } catch(t: Throwable) {
        throw JSException(cause = t)
    }
}

internal inline fun ignored(block: () -> Unit) = try { block() } catch(ignored: Throwable) {}
