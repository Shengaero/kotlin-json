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
package me.kgustave.json.exceptions

/**
 * Higher level exception for all other exceptions
 * that come from the kotlin-json library.
 *
 * This exception has two intentions:
 *
 * 1) To serve as a superclass and a catchall for all extending exceptions.
 * 2) To serve as a generic exception that does not fill the criteria for throwing a subclass of it.
 *
 * Generally speaking, unless otherwise documented, this should be used whenever
 * dealing with catching errors from various kotlin-json resources.
 *
 * @author Kaidan Gustave
 * @since  1.0
 */
@SinceKotlin("1.2")
open class JSException(message: String? = null, cause: Throwable? = null): RuntimeException(message, cause)