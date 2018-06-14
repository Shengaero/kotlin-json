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
@file:Suppress("unused")

package me.kgustave.json.ktor.server

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import me.kgustave.json.JSArray
import me.kgustave.json.JSONDsl
import me.kgustave.json.JSObject

@JSONDsl suspend inline fun ApplicationCall.respondJSObject(status: HttpStatusCode, block: JSObject.() -> Unit) =
    respond(status, JSObject(block))

@JSONDsl suspend inline fun ApplicationCall.respondJSObject(block: JSObject.() -> Unit) =
    respond(JSObject(block))

@JSONDsl suspend fun ApplicationCall.respondJSArray(status: HttpStatusCode, vararg values: Any?) =
    respond(status, JSArray(values))

@JSONDsl suspend fun ApplicationCall.respondJSArray(vararg values: Any?) =
    respond(JSArray(values))

@JSONDsl suspend inline fun ApplicationCall.respondJSArray(status: HttpStatusCode, size: Int, block: (Int) -> Any?) =
    respond(status, JSArray(size, block))

@JSONDsl suspend inline fun ApplicationCall.respondJSArray(size: Int, block: (Int) -> Any?) =
    respond(JSArray(size, block))