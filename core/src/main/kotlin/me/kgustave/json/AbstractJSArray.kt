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
package me.kgustave.json

import me.kgustave.json.internal.buildJsonString
import me.kgustave.json.internal.syntaxError
import java.util.*

/**
 * Abstract implementation of [JSArray].
 *
 * While this is exposed as a public class, it's done
 * only with the intent of covering alternate future
 * implementations of the [JSArray] interface is
 * subject to change like any internal resource.
 *
 * @author Kaidan Gustave
 * @since  1.0
 */
@SinceKotlin("1.2")
abstract class AbstractJSArray(
    protected val list: MutableList<Any?> = LinkedList()
): JSArray, MutableList<Any?> by list {
    constructor(x: JSTokener): this() {
        if(x.nextClean() != '[') {
            x.syntaxError("A JSArray text must start with '['")
        }
        if(x.nextClean() != ']') {
            x.back()
            while(true) {
                if(x.nextClean() == ',') {
                    x.back()
                    list.add(null)
                } else {
                    x.back()
                    list.add(x.nextValue())
                }

                when(x.nextClean()) {
                    ',' -> {
                        if(x.nextClean() == ']') {
                            return
                        }
                        x.back()
                    }
                    ']' -> return
                    else -> x.syntaxError("Expected a ',' or ']'")
                }
            }
        }
    }

    override fun toJsonString(indent: Int): String = buildJsonString(indent, 0)
    override fun toString(): String = toJsonString(0)
}
