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
@file:JvmMultifileClass()
package me.kgustave.json.reflect

import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*

/**
 * Marks an annotation that is used in
 * class-to-JSON serialization processes.
 *
 * @author Kaidan Gustave
 * @since  1.5
 */
@MustBeDocumented
@Retention(SOURCE)
@Target(ANNOTATION_CLASS, CLASS, FUNCTION, PROPERTY)
@Experimental(Experimental.Level.WARNING)
annotation class JSSerialization

/**
 * Marks an annotation that is used in
 * JSON-to-class deserialization processes.
 *
 * @author Kaidan Gustave
 * @since  1.5
 */
@MustBeDocumented
@Retention(SOURCE)
@Target(ANNOTATION_CLASS, CLASS, FUNCTION, PROPERTY)
@Experimental(Experimental.Level.WARNING)
annotation class JSDeserialization
