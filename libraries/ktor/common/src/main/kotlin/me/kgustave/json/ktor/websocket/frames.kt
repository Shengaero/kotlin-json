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
@file:JvmName("FramesUtil")
package me.kgustave.json.ktor.websocket

import io.ktor.http.cio.websocket.Frame
import kotlinx.io.core.BytePacketBuilder
import me.kgustave.json.*
import java.nio.charset.Charset

fun JSObject.asTextFrame(fin: Boolean = false): Frame.Text {
    val packet = BytePacketBuilder()
    JSWriter(packet).obj {
        for((key, value) in this@asTextFrame) {
            key(key).value(value)
        }
    }
    return Frame.Text(fin, packet.build())
}

fun JSObject.asBinaryFrame(fin: Boolean = false): Frame.Binary {
    val packet = BytePacketBuilder()
    JSWriter(packet).obj {
        for((key, value) in this@asBinaryFrame) {
            key(key).value(value)
        }
    }
    return Frame.Binary(fin, packet.build())
}

fun JSArray.asTextFrame(fin: Boolean = false): Frame.Text {
    val packet = BytePacketBuilder()
    JSWriter(packet).array {
        for(value in this@asTextFrame) {
            value(value)
        }
    }
    return Frame.Text(fin, packet.build())
}

fun JSArray.asBinaryFrame(fin: Boolean = false): Frame.Binary {
    val packet = BytePacketBuilder()
    JSWriter(packet).array {
        for(value in this@asBinaryFrame) {
            value(value)
        }
    }
    return Frame.Binary(fin, packet.build())
}

fun Frame.Text.readJSObject(): JSObject = this.buffer.readJSObject(Charsets.UTF_8)
fun Frame.Text.readJSArray(): JSArray = this.buffer.readJSArray(Charsets.UTF_8)
fun Frame.Binary.readJSObject(charset: Charset = Charsets.UTF_8): JSObject = this.buffer.readJSObject(charset)
fun Frame.Binary.readJSArray(charset: Charset = Charsets.UTF_8): JSArray = this.buffer.readJSArray(charset)