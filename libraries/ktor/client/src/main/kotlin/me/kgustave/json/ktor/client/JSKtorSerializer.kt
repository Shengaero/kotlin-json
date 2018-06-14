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
package me.kgustave.json.ktor.client

import io.ktor.client.call.TypeInfo
import io.ktor.client.features.json.JsonSerializer
import io.ktor.client.response.HttpResponse
import io.ktor.client.response.readText
import io.ktor.content.OutgoingContent
import io.ktor.http.charset
import me.kgustave.json.JSArray
import me.kgustave.json.JSObject
import me.kgustave.json.ktor.JSOutgoingContent
import me.kgustave.json.parseJsonArray
import me.kgustave.json.parseJsonObject
import me.kgustave.json.reflect.JSDeserializer
import me.kgustave.json.reflect.JSSerializer
import java.nio.charset.Charset
import kotlin.reflect.full.isSubclassOf

class JSKtorSerializer(
    private val serializer: JSSerializer = JSSerializer(),
    private val deserializer: JSDeserializer = JSDeserializer(),
    private val charset: Charset = Charsets.UTF_8
): JsonSerializer {
    override suspend fun read(type: TypeInfo, response: HttpResponse): Any {
        val klass = type.type
        val charset = response.charset() ?: this.charset
        val text = response.readText(charset)
        if(klass.isSubclassOf(JSArray::class)) return parseJsonArray(text)
        val json = parseJsonObject(text)
        if(klass.isSubclassOf(JSObject::class)) return json
        return deserializer.deserialize(json, klass)
    }

    override fun write(data: Any): OutgoingContent {
        val json = ((data as? JSObject ?: data as? JSArray) ?: serializer.serialize(data))
        return JSOutgoingContent(json, charset)
    }
}