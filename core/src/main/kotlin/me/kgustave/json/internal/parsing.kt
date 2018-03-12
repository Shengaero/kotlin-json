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
@file:JvmName("Internal_ParsingKt")
package me.kgustave.json.internal

internal fun removeComments(str: String): String {
    // Instead of filtering out the commented lines, we simply skip them.
    // This doesn't save much if any overhead but considering the efficiency
    // we lose from comments in general, the quickest operation should be taken.
    return str.split('\n').joinToString("\n") { if(it.trim().startsWith("//")) "" else it }
}

internal fun stringToValue(str: String): Any? {
    if(str.isEmpty()) {
        return str
    }

    when(str) {
        "true" -> return true
        "false" -> return false
        "null" -> return null
    }

    val initial = str[0]
    if(initial in '0'..'9' || initial == '-') {
        ignored {
            if(str.indexOf('.') > -1 || str.indexOf('e') > -1 || str.indexOf('E') > -1 || str == "-0") {
                val d = str.toDouble()
                if(!d.isInfinite() && !d.isNaN()) {
                    return d
                }
            } else {
                val long = str.toLong()

                if(str == long.toString()) {
                    return if(long == long.toInt().toLong()) long.toInt() else long
                }
            }
        }
    }
    return str
}
