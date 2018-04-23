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
@file:JvmName("ConvenienceKt")
@file:Suppress("NOTHING_TO_INLINE", "Unused")
package me.kgustave.json

import me.kgustave.json.internal.JSArrayImpl
import me.kgustave.json.internal.JSONDsl
import me.kgustave.json.internal.JSObjectImpl

@SinceKotlin("1.2")
fun emptyJSObject(): JSObject = JSObjectImpl()

@SinceKotlin("1.2")
fun emptyJSArray(): JSArray = JSArrayImpl()

@JSONDsl
@SinceKotlin("1.2")
fun jsonObject(vararg pairs: Pair<String, Any?>): JSObject {
    if(pairs.isEmpty()) {
        return JSObjectImpl()
    }
    return JSObjectImpl(pairs.toMap())
}

@JSONDsl
@SinceKotlin("1.2")
fun jsonArray(vararg items: Any?): JSArray {
    if(items.isEmpty()) {
        return JSArrayImpl()
    }
    return JSArrayImpl(items)
}

@JSONDsl
@SinceKotlin("1.2")
inline fun jsonObject(block: JSObject.() -> Unit): JSObject {
    val json = JSObjectImpl()
    json.block()
    return json
}

@JSONDsl
@SinceKotlin("1.2")
inline fun jsonArray(block: JSArray.() -> Unit): JSArray {
    val json = JSArrayImpl()
    json.block()
    return json
}

@SinceKotlin("1.2")
inline fun <reified M: Map<String, *>> M.toJSObject(): JSObject = JSObjectImpl(this)

@SinceKotlin("1.2")
inline fun <reified C: Collection<*>> C.toJSArray(): JSArray = JSArrayImpl(this)

@SinceKotlin("1.2")
inline fun <reified L: List<*>> L.toJSArray(): JSArray = JSArrayImpl(this)

@SinceKotlin("1.2")
inline fun Array<*>.toJSArray(): JSArray = JSArrayImpl(this)
