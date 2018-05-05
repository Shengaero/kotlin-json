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
package me.kgustave.json.internal

private var CHECKED_FOR_KOTLIN_REFLECT = false

// Checks if kotlin-reflect is in the classpath.
//If it's not, we throw a KotlinReflectionNotSupportedError
internal fun checkIfReflectionIsInClasspath() {
    if(CHECKED_FOR_KOTLIN_REFLECT) return
    try { Class.forName("kotlin.reflect.jvm.internal.KClassImpl") } catch(e: ClassNotFoundException) {
        throw KotlinReflectionNotSupportedError(
            "kotlin-reflect not found in classpath! Make sure you have added " +
            "the proper kotlin reflect dependency!"
        )
    }
    CHECKED_FOR_KOTLIN_REFLECT = true
}