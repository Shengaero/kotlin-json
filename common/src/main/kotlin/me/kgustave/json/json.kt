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
@file:Suppress("FunctionName", "NOTHING_TO_INLINE")
package me.kgustave.json

// Basic Access

/**
 * Creates a [JSObject] using the provided key-value [pairs].
 *
 * @param pairs The key-value [Pairs][Pair] to create a [JSObject] with.
 *
 * @return A new [JSObject].
 */
expect fun jsObjectOf(vararg pairs: Pair<String, Any?>): JSObject

/**
 * Creates a [JSArray] using the provided [items].
 *
 * @param items The items to create a [JSArray] with.
 *
 * @return A new [JSArray].
 */
expect fun jsArrayOf(vararg items: Any?): JSArray

// DSL

/**
 * Creates a [JSObject] and runs the provided [block] on it.
 *
 * @param block The block to run.
 *
 * @return A new [JSObject].
 */
@JSONDsl inline fun JSObject(block: JSObject.() -> Unit): JSObject = jsObjectOf().apply(block)

/**
 * Creates a [JSArray] with the provided [items].
 *
 * @param items The items to create the [JSArray] with.
 *
 * @return A new [JSArray].
 */
@JSONDsl inline fun JSArray(vararg items: Any?): JSArray = jsArrayOf(*items)

/**
 * Creates a [JSArray] of the specified [size], where each element
 * is calculated by calling the specified [init] function.
 *
 * The [init] function returns an array element given its index.
 *
 * @param size The size of the [JSArray].
 * @param init A function to initialize the array where the value
 * is the index to place the calculated value at.
 *
 * @return A new [JSArray].
 */
@JSONDsl inline fun JSArray(size: Int, init: (Int) -> Any?): JSArray {
    val array = jsArrayOf()
    for(i in 0 until size) {
        array[i] = init(i)
    }
    return array
}