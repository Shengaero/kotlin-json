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

import me.kgustave.json.*

/**
 * Abstract implementation of [JSObject].
 *
 * @author Kaidan Gustave
 * @since  1.0
 */
internal abstract class AbstractJSObject(
    protected val map: MutableMap<String, Any?> = HashMap()
): JSObject, MutableMap<String, Any?> by map {
    constructor(tokener: JSTokener): this() {
        if(tokener.nextSymbol() != '{') {
            tokener.syntaxError("A JSObject text must begin with '{'")
        }

        var c: Char
        while(true) {
            c = tokener.nextSymbol()

            when(c) {
                JSTokener.NCHAR -> {
                    tokener.syntaxError("A JSObject text must end with '}'")
                }

                '}' -> return
            }
            tokener.prev()
            val key = tokener.nextKey()
            map[key] = tokener.nextValue()
            when(tokener.nextSymbol()) {
                ';', ',' -> {
                    if(tokener.nextSymbol() == '}') return
                    tokener.prev()
                }

                '}' -> return

                else -> tokener.syntaxError("Expected a ',' or '}'")
            }
        }
    }

    override fun put(key: String, value: Any?): Any? = map.put(key, convertValue(value))
    override fun putAll(from: Map<out String, Any?>) {
        from.forEach { s, u -> put(s, u) }
    }

    override fun isNull(key: String): Boolean {
        return map[key] === null
    }

    override fun toJsonString(indent: Int): String = buildJsonObjectString(indent)
    override fun toString(): String = toJsonString(0)
}
