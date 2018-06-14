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
package me.kgustave.json.ktor

import io.ktor.cio.write
import io.ktor.content.OutgoingContent
import io.ktor.http.ContentType
import io.ktor.http.charset
import io.ktor.http.withCharset
import kotlinx.coroutines.experimental.io.ByteWriteChannel
import me.kgustave.json.JSString
import java.nio.charset.Charset

/**
 * Represents outgoing JSON content.
 *
 * @since  1.6
 * @author Kaidan Gustave
 */
class JSOutgoingContent(json: JSString, charset: Charset): OutgoingContent.WriteChannelContent() {
    private val jsonText by lazy { json.toJsonString(0) }
    override val contentType = ContentType.Application.Json.withCharset(charset)
    override val contentLength = jsonText.length.toLong()
    override suspend fun writeTo(channel: ByteWriteChannel) =
        channel.write(jsonText, contentType.charset()!!)
}