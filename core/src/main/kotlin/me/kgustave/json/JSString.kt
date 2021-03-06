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
 * Interface used to stringify JSON style entities.
 *
 * The [toJsonString] provides a `indent` parameter for
 * pretty print JSON strings.
 *
 * @author Kaidan Gustave
 * @since  1.0
 */
interface JSString {
    /**
     * Converts a value to a JSON entity as a [String]
     * rendered with the specified [indent] factor.
     */
    fun toJsonString(indent: Int): String
}