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
package me.kgustave.json

/**
 * A writer for JSON entities.
 *
 * @author Kaidan Gustave
 * @since  1.4
 */
interface JSWriter: AutoCloseable {
    /**
     * The level that this writer is appending at.
     *
     * - Calls to [obj] or [array] increase this by 1.
     * - Calls to [endObj] or [endArray] decrease this by 1.
     *
     * When this returns 0, the writer can no longer be appended to.
     */
    val level: Int

    /**
     * Appends the start of a JSON object.
     *
     * @return This [JSWriter].
     */
    fun obj(): JSWriter

    /**
     * Appends the start of a JSON array.
     *
     * @return This [JSWriter].
     */
    fun array(): JSWriter

    /**
     * Appends the [key] as a key for a JSON object key-value pair.
     *
     * This will require that the next method called on this [JSWriter]
     * is [value].
     *
     * @param key The key to append.
     *
     * @return This [JSWriter].
     */
    fun key(key: String): JSWriter

    /**
     * Appends a [value] to this [JSWriter].
     *
     * This can either be the value expected after a [key] in the context
     * of a JSON object, or simply another value in the context of a JSON
     * array.
     *
     * @param value The value to append.
     *
     * @return This [JSWriter].
     */
    fun value(value: Any?): JSWriter

    /**
     * Appends the end of a JSON object.
     *
     * @return This [JSWriter].
     */
    fun endObj(): JSWriter

    /**
     * Appends the end of a JSON array.
     *
     * @return This [JSWriter].
     */
    fun endArray(): JSWriter

    /**
     * Closes this writer.
     *
     * The internal implementation of this interface wraps an [Appendable],
     * and the implementation of this function closes that [Appendable]
     * granted it also implements [AutoCloseable].
     *
     * This re-throws any exceptions that [AutoCloseable.close] might throw.
     */
    @Throws(Throwable::class)
    override fun close()
}