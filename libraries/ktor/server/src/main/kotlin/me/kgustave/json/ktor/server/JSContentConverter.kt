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
@file:Suppress("RedundantCompanionReference")
package me.kgustave.json.ktor.server

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.features.ContentConverter
import io.ktor.features.suitableCharset
import io.ktor.http.ContentType
import io.ktor.pipeline.PipelineContext
import io.ktor.request.ApplicationReceiveRequest
import io.ktor.request.ContentTransformationException
import io.ktor.request.contentCharset
import kotlinx.coroutines.experimental.io.ByteReadChannel
import kotlinx.coroutines.experimental.io.readRemaining
import kotlinx.io.core.readText
import me.kgustave.json.*
import me.kgustave.json.exceptions.JSSyntaxException
import me.kgustave.json.ktor.JSOutgoingContent
import me.kgustave.json.reflect.JSDeserializer
import me.kgustave.json.reflect.JSSerializer
import java.nio.charset.Charset
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

/**
 * Converts JSON request bodies into [JSObjects][JSObject],
 * as well as JSObjects into text content for responses.
 *
 * Additionally, [serialization][JSSerializer] and
 * [deserialization][JSDeserializer] is available.
 *
 * @author Kaidan Gustave
 */
class JSContentConverter(
    private val deserializer: JSDeserializer = JSDeserializer(),
    private val serializer: JSSerializer = JSSerializer(),
    private var charset: Charset = Charsets.UTF_8
): ContentConverter {
    fun registerDeserializable(klass: KClass<*>) {
        deserializer.register(klass)
    }

    fun registerSerializable(klass: KClass<*>) {
        serializer.register(klass)
    }

    fun charset(charset: Charset) {
        this.charset = charset
    }

    override suspend fun convertForReceive(context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>): Any? {
        val subject = context.subject
        val type = subject.type
        val channel = subject.value as? ByteReadChannel ?: return null
        val charset = context.call.request.contentCharset() ?: charset
        val text = channel.readRemaining().readText(charset.newDecoder()).takeIf(String::isNotBlank) ?: return null
        try {
            if(type.isSubclassOf(JSArray::class)) return parseJsonArray(text)
            val json = parseJsonObject(text)
            if(type.isSubclassOf(JSObject::class)) return json
            return deserializer.deserialize(json, type)
        } catch(e: JSSyntaxException) {
            throw ContentTransformationException("Could not parse JSON!")
        }
    }

    override suspend fun convertForSend(context: PipelineContext<Any, ApplicationCall>,
                                        contentType: ContentType, value: Any): Any? {
        if(!ContentType.Application.Json.match(contentType.withoutParameters())) return null
        val charset = context.context.suitableCharset(charset)
        val json = value as? JSObject ?: value as? JSArray ?: serializer.serialize(value)
        return JSOutgoingContent(json, charset)
    }
}