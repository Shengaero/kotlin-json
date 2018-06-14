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

import me.kgustave.json.JSArray

/**
 * Abstract implementation of [JSArray].
 *
 * @author Kaidan Gustave
 * @since  1.0
 */
internal abstract class AbstractJSArray(protected val list: MutableList<Any?> = ArrayList()): JSArray,
    MutableList<Any?> by list {
    constructor(tokener: JSTokener): this() {
        if(tokener.nextSymbol() != '[') {
            tokener.syntaxError("A JSArray text must start with '['")
        }
        if(tokener.nextSymbol() != ']') {
            tokener.prev()
            while(true) {
                if(tokener.nextSymbol() == ',') {
                    tokener.prev()
                    list.add(null)
                } else {
                    tokener.prev()
                    list.add(tokener.nextValue())
                }

                when(tokener.nextSymbol()) {
                    ',' -> {
                        if(tokener.nextSymbol() == ']') {
                            return
                        }
                        tokener.prev()
                    }
                    ']' -> return
                    else -> tokener.syntaxError("Expected a ',' or ']'")
                }
            }
        }
    }

    override fun add(element: Any?): Boolean = list.add(convertValue(element))
    override fun add(index: Int, element: Any?) = list.add(index, convertValue(element))
    override fun addAll(elements: Collection<Any?>): Boolean = list.addAll(elements.map { convertValue(it) })
    override fun addAll(index: Int, elements: Collection<Any?>): Boolean = list.addAll(index, elements.map { convertValue(it) })
    override fun set(index: Int, element: Any?): Any? {
        if(index == size) add(element) else return list.set(index, element)
        return null
    }

    override fun isNull(index: Int): Boolean {
        if(0 > index || index > size - 1) return true
        return this[index] === null
    }

    override fun toJsonString(indent: Int): String = buildJsonArrayString(indent, 0)
    override fun toString(): String = toJsonString(0)
}
