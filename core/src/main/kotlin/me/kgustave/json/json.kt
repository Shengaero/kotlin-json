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
@file:JvmName("JSON")
@file:Suppress("NOTHING_TO_INLINE", "Unused", "FunctionName", "EXTENSION_SHADOWED_BY_MEMBER")
package me.kgustave.json

import me.kgustave.json.internal.JSArrayImpl
import me.kgustave.json.internal.JSONDsl
import me.kgustave.json.internal.JSObjectImpl
import me.kgustave.json.internal.JSWriterImpl
import java.lang.StringBuilder

/**
 * Creates a [JSObject] using the provided key-value [pairs].
 *
 * @param pairs The key-value [Pairs][Pair] to create a [JSObject] with.
 *
 * @return A new [JSObject].
 */
@JvmName("objectOf")
fun jsObjectOf(vararg pairs: Pair<String, Any?>): JSObject = JSObjectImpl(*pairs)

/**
 * Creates a [JSArray] using the provided [items].
 *
 * @param items The items to create a [JSArray] with.
 *
 * @return A new [JSArray].
 */
@JvmName("arrayOf")
fun jsArrayOf(vararg items: Any?): JSArray = JSArrayImpl(items)

/**
 * Creates a [JSObject] and runs the provided [block] on it.
 *
 * @param block The block to run.
 *
 * @return A new [JSObject].
 */
@JSONDsl
inline fun jsonObject(block: JSObject.() -> Unit): JSObject = JSObject(block)

/**
 * Creates a [JSObject] and runs the provided [block] on it.
 *
 * @param block The block to run.
 *
 * @return A new [JSObject].
 */
@JSONDsl
inline fun JSObject(block: JSObject.() -> Unit): JSObject = jsObjectOf().apply(block)

/**
 * Creates a [JSArray] with the provided [items].
 *
 * @param items The items to create the [JSArray] with.
 *
 * @return A new [JSArray].
 */
@JSONDsl
inline fun JSArray(vararg items: Any?): JSArray = jsArrayOf(*items)

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
@JSONDsl
inline fun JSArray(size: Int, init: (Int) -> Any?): JSArray {
    val array = jsArrayOf()
    for(i in 0 until size) {
        array[i] = init(i)
    }
    return array
}

/**
 * Creates a [JSObject] from the receiver [Map].
 *
 * @receiver The [Map] to create a [JSObject] with.
 *
 * @return A [JSObject] created from the [Map].
 */
fun Map<String, *>.toJSObject(): JSObject = JSObjectImpl(this)

/**
 * Creates a [JSArray] from the receiver [Collection].
 *
 * @receiver The [Collection] to create a [JSArray] with.
 *
 * @return A [JSArray] created from the [Collection].
 */
fun Collection<*>.toJSArray(): JSArray = JSArrayImpl(this)

/**
 * Creates a [JSArray] from the receiver [List].
 *
 * @receiver The [List] to create a [JSArray] with.
 *
 * @return A [JSArray] created from the [List].
 */
fun List<*>.toJSArray(): JSArray = JSArrayImpl(this)

/**
 * Creates a [JSArray] from the receiver [Array].
 *
 * @receiver The [Array] to create a [JSArray] with.
 *
 * @return A [JSArray] created from the [Array].
 */
fun Array<*>.toJSArray(): JSArray = JSArrayImpl(this)

/**
 * Gets a value with the [key] specified, or `null` if it is not type [T].
 *
 * @receiver The [JSObject] to opt for.
 *
 * @param [T] The type of value to get.
 * @param key The key to get a value for.
 *
 * @return A value of type [T], or `null` if no value exists,
 * or if the value is not the type [T].
 */
inline fun <reified T> JSObject.opt(key: String): T? = this[key] as? T

/**
 * Gets a value at the [index] specified, or `null` if it is not type [T].
 *
 * @receiver The [JSArray] to opt for.
 *
 * @param [T] The type of value to get.
 * @param index The index to get a value at.
 *
 * @return A value of type [T], or `null` if no value exists,
 * or if the value is not the type [T].
 */
inline fun <reified T> JSArray.opt(index: Int): T? = getOrNull(index) as? T

// Writer

/**
 * Creates a new [JSWriter] that wraps the specified [writer].
 *
 * @param writer The [Appendable] to wrap into a [JSWriter].
 *
 * @return A new [JSWriter].
 */
fun JSWriter(writer: Appendable = StringBuilder()): JSWriter = JSWriterImpl(writer)

/**
 * [Starts][JSWriter.obj] and [ends][JSWriter.endObj] a JSON Object
 * with the receiver [JSWriter].
 *
 * @receiver A [JSWriter].
 *
 * @param block The block to apply to the receiver inside the new object.
 *
 * @return The receiver [JSWriter].
 */
inline fun JSWriter.obj(block: JSWriter.() -> Unit): JSWriter {
    obj()
    if(this is JSWriterImpl) {
        val prevLock = lockManualEnd
        lockManualEnd = true
        this.block()
        lockManualEnd = prevLock
    } else this.block()
    return endObj()
}

/**
 * [Starts][JSWriter.array] and [ends][JSWriter.endArray] a JSON Array
 * with the receiver [JSWriter].
 *
 * @receiver A [JSWriter].
 *
 * @param block The block to apply to the receiver inside the new array.
 *
 * @return The receiver [JSWriter].
 */
inline fun JSWriter.array(block: JSWriter.() -> Unit): JSWriter {
    array()
    if(this is JSWriterImpl) {
        val prevLock = lockManualEnd
        lockManualEnd = true
        this.block()
        lockManualEnd = prevLock
    } else this.block()
    return endArray()
}

// DEPRECATED

@Deprecated(
    message = "Semantically this is incorrect, and should be replaced with " +
              "more proper syntax functions. Scheduled for removal in 1.6.0.",
    level = DeprecationLevel.WARNING
)
@JSONDsl
inline fun jsonArray(block: JSArray.() -> Unit): JSArray {
    val json = jsArrayOf()
    json.block()
    return json
}

@Deprecated(
    message = "Deprecated and replaced with jsObjectOf to match common kotlin naming semantics. " +
              "Removal is scheduled for 1.6.0.",
    replaceWith = ReplaceWith(
        expression = "jsObjectOf(pairs)",
        imports = ["me.kgustave.json.jsObjectOf"]
    ),
    level = DeprecationLevel.WARNING
)
fun jsonObject(vararg pairs: Pair<String, Any?>): JSObject = jsObjectOf(*pairs)

@Deprecated(
    message = "Deprecated and replaced with jsArrayOf to match common kotlin naming semantics. " +
              "Removal is scheduled for 1.6.0.",
    replaceWith = ReplaceWith(
        expression = "jsArrayOf(items)",
        imports = ["me.kgustave.json.jsArrayOf"]
    ),
    level = DeprecationLevel.WARNING
)
fun jsonArray(vararg items: Any?): JSArray = jsArrayOf(*items)

@Deprecated(
    message = "Optimized jsObjectOf(). Removal is scheduled for 1.6.0.",
    replaceWith = ReplaceWith(
        expression = "jsObjectOf()",
        imports = ["me.kgustave.json.jsObjectOf"]
    ),
    level = DeprecationLevel.WARNING
)
fun emptyJSObject(): JSObject = jsObjectOf()

@Deprecated(
    message = "Deprecated in favor of jsArrayOf(). Removal is scheduled for 1.6.0.",
    replaceWith = ReplaceWith(
        expression = "jsArrayOf()",
        imports = ["me.kgustave.json.jsArrayOf"]
    ),
    level = DeprecationLevel.WARNING
)
fun emptyJSArray(): JSArray = jsArrayOf()