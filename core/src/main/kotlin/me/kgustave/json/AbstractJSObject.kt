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
package me.kgustave.json

import me.kgustave.json.internal.buildJsonString
import me.kgustave.json.internal.syntaxError

/**
 * Abstract implementation of [JSObject].
 *
 * While this is exposed as a public class, it's done
 * only with the intent of covering alternate future
 * implementations of the [JSObject] interface is
 * subject to change like any internal resource.
 *
 * @author Kaidan Gustave
 * @since  1.0
 */
@SinceKotlin("1.2")
abstract class AbstractJSObject(
    protected val map: MutableMap<String, Any?> = HashMap()
): JSObject, MutableMap<String, Any?> by map {
    constructor(x: JSTokener): this() {
        var c: Char
        var key: String

        if(x.nextClean() != '{')
            x.syntaxError("A JSObject text must begin with '{'")

        while(true) {
            c = x.nextClean()
            when (c) {
                0.toChar() -> x.syntaxError("A JSObject text must end with '}'")

                '}' -> return

                else -> {
                    x.back()
                    key = x.nextValue().toString()
                }
            }

            c = x.nextClean()
            if(c != ':') {
                x.syntaxError("Expected a ':' after a key")
            }

            map[key] = x.nextValue()

            when(x.nextClean()) {
                ';', ',' -> {
                    if(x.nextClean() == '}') return
                    x.back()
                }

                '}' -> return

                else -> x.syntaxError("Expected a ',' or '}'")
            }
        }
    }

    override fun toJsonString(indent: Int): String = buildJsonString(indent)
    override fun toString(): String = toJsonString(0)
}
