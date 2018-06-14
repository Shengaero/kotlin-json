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
 * Internal implementation of [JSArray][me.kgustave.json.JSArray].
 *
 * @author  Kaidan Gustave
 * @version 1.0
 */
internal class JSArrayImpl: AbstractJSArray {
    constructor(array: Array<*>): super(array.mapTo(ArrayList(array.size)) { convertValue(it) })
    constructor(collection: Collection<*>): super(collection.mapTo(ArrayList(collection.size)) { convertValue(it) })
    constructor(tokener: JSTokener): super(tokener)
}
