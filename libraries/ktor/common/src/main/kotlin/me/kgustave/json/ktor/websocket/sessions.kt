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
@file:JvmName("SessionsUtil")
package me.kgustave.json.ktor.websocket

import io.ktor.http.cio.websocket.WebSocketSession
import me.kgustave.json.JSArray
import me.kgustave.json.JSONDsl
import me.kgustave.json.JSObject

@JSONDsl suspend inline fun WebSocketSession.sendJSObject(block: JSObject.() -> Unit) =
    sendJSObject(false, block)

@JSONDsl suspend inline fun WebSocketSession.sendJSArray(vararg values: Any?) =
    sendJSArray(false, *values)

@JSONDsl suspend inline fun WebSocketSession.sendJSArray(size: Int, block: (Int) -> Any?) =
    sendJSArray(false, size, block)

@JSONDsl suspend inline fun WebSocketSession.sendJSObject(fin: Boolean, block: JSObject.() -> Unit) =
    send(JSObject(block).asTextFrame(fin))

@JSONDsl suspend inline fun WebSocketSession.sendJSArray(fin: Boolean, vararg values: Any?) =
    send(JSArray(*values).asTextFrame(fin))

@JSONDsl suspend inline fun WebSocketSession.sendJSArray(fin: Boolean, size: Int, block: (Int) -> Any?) =
    send(JSArray(size, block).asTextFrame(fin))