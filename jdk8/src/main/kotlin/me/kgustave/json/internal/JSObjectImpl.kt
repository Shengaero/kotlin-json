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

internal actual class JSObjectImpl: AbstractJSObject {
    actual constructor(): super()
    actual constructor(map: Map<String, *>): this() { putAll(map) }
    actual constructor(vararg pairs: Pair<String, *>): this() { putAll(pairs) }
    constructor(tokener: JSTokener): this() {
        if(tokener.nextSymbol() != '{') {
            tokener.expectedObjectStart()
        }

        var c: Char
        while(true) {
            c = tokener.nextSymbol()

            when(c) {
                JSTokener.NCHAR -> {
                    tokener.expectedObjectEnd()
                }

                '}' -> return
            }
            tokener.prev()
            val key = tokener.nextKey()
            map[key] = tokener.nextValue()
            when(tokener.nextSymbol()) {
                ',' -> {
                    if(tokener.nextSymbol() == '}') return
                    tokener.prev()
                }

                '}' -> return

                else -> tokener.expectedCommaOrObjectEnd()
            }
        }
    }
}