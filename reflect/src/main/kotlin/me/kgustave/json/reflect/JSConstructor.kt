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
package me.kgustave.json.reflect

import me.kgustave.json.reflect.internal.JSDeserialization
import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*

/**
 * Specifies which constructor should be used during deserialization
 * of a [JSObject][me.kgustave.json.JSObject] into an instance of the
 * class.
 *
 * Without this annotation, any available constructor may be used, notably the
 * first one returned by [KClass.constructors][kotlin.reflect.KClass.constructors].
 *
 * Additionally, this annotation may be applied to the actual class to signify
 * the primary constructor should be used, and this annotation does not work
 * on [data classes](https://kotlinlang.org/docs/reference/data-classes.html).
 *
 * @author Kaidan Gustave
 * @since 1.6
 */
@JSDeserialization
@MustBeDocumented
@Retention(RUNTIME)
@Target(CLASS, CONSTRUCTOR)
annotation class JSConstructor