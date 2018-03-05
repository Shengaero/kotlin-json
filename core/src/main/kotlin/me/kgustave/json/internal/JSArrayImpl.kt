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
package me.kgustave.json.internal

import me.kgustave.json.AbstractJSArray
import me.kgustave.json.JSTokener

/**
 * Internal implementation of [JSArray][me.kgustave.json.JSArray].
 *
 * @author  Kaidan Gustave
 * @version 1.0
 */
@PublishedApi
internal class JSArrayImpl: AbstractJSArray {
    @PublishedApi
    internal constructor(): super()
    @PublishedApi
    internal constructor(array: Array<*>): super(array.toMutableList())
    @PublishedApi
    internal constructor(collection: Collection<*>): super(collection.toMutableList())
    @PublishedApi
    internal constructor(x: JSTokener): super(x)

    override fun isNull(index: Int): Boolean {
        return list[index] === null
    }
}
