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
 * Creates a [JSObject] from the receiver [Map].
 *
 * @receiver The [Map] to create a [JSObject] with.
 *
 * @return A [JSObject] created from the [Map].
 */
expect fun Map<String, *>.toJSObject(): JSObject

/**
 * Creates a [JSArray] from the receiver [Collection].
 *
 * @receiver The [Collection] to create a [JSArray] with.
 *
 * @return A [JSArray] created from the [Collection].
 */
expect fun Collection<*>.toJSArray(): JSArray

/**
 * Creates a [JSArray] from the receiver [List].
 *
 * @receiver The [List] to create a [JSArray] with.
 *
 * @return A [JSArray] created from the [List].
 */
expect fun List<*>.toJSArray(): JSArray

/**
 * Creates a [JSArray] from the receiver [Array].
 *
 * @receiver The [Array] to create a [JSArray] with.
 *
 * @return A [JSArray] created from the [Array].
 */
expect fun Array<*>.toJSArray(): JSArray

/**
 * Creates a [JSArray] from the receiver [Sequence].
 *
 * @receiver The [Sequence] to create a [JSArray] with.
 *
 * @return A [JSArray] created from the [Sequence].
 */
expect fun Sequence<*>.toJSArray(): JSArray
