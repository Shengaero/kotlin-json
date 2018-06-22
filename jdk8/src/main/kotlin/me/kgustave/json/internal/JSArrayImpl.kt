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

/**
 * @author Kaidan Gustave
 */
internal actual class JSArrayImpl: AbstractJSArray {
    actual constructor(): super()
    actual constructor(array: Array<*>): this() { addAll(array) }
    actual constructor(collection: Collection<*>): this() { addAll(collection) }
    constructor(tokener: JSTokener) {
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
}