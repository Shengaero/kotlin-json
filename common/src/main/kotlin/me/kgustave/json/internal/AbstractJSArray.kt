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
@file:Suppress("MemberVisibilityCanBePrivate")
package me.kgustave.json.internal

import me.kgustave.json.JSArray
import me.kgustave.json.JSObject
import me.kgustave.json.exceptions.JSException

/**
 * Abstract implementation of [JSArray].
 *
 * @author Kaidan Gustave
 * @since  1.0
 */
internal abstract class AbstractJSArray(protected val list: MutableList<Any?> = ArrayList()): JSArray,
    MutableList<Any?> by list {
    override fun add(element: Any?): Boolean = list.add(convertValue(element))
    override fun add(index: Int, element: Any?) = list.add(index, convertValue(element))
    override fun addAll(elements: Collection<Any?>): Boolean = list.addAll(elements.map { convertValue(it) })
    override fun addAll(index: Int, elements: Collection<Any?>): Boolean = list.addAll(index, elements.map { convertValue(it) })
    override fun set(index: Int, element: Any?): Any? {
        if(index == size) add(element) else return list.set(index, element)
        return null
    }

    override fun boolean(index: Int): Boolean {
        val value = getValueFor(index)
        return checkNotNullJson(convertBoolean(value)) { isNotType(index, value, Boolean::class) }
    }

    override fun string(index: Int): String {
        val value = getValueFor(index)
        return checkNotNullJson(value as? String) { isNotType(index, value, String::class) }
    }

    override fun long(index: Int): Long {
        val value = getValueFor(index)
        convertLong(value)?.let { return it }
        throw JSException(isNotType(index, value, Long::class))
    }

    override fun int(index: Int): Int {
        val value = getValueFor(index)
        return checkNotNullJson(convertInt(value)) { isNotType(index, value, Int::class) }
    }

    override fun double(index: Int): Double {
        val value = getValueFor(index)
        convertDouble(value)
        throw JSException(isNotType(index, value, Double::class))
    }

    override fun float(index: Int): Float {
        val value = getValueFor(index)
        return checkNotNullJson(convertFloat(value)) { isNotType(index, value, Float::class) }
    }

    override fun obj(index: Int): JSObject {
        val value = getValueFor(index)
        return checkNotNullJson(value as? JSObject) { isNotType(index, value, JSObject::class) }
    }

    override fun array(index: Int): JSArray {
        val value = getValueFor(index)
        return checkNotNullJson(value as? JSArray) { isNotType(index, value, JSArray::class) }
    }

    override fun optString(index: Int): String? = convertString(optValueFor(index))

    override fun optBoolean(index: Int): Boolean? = convertBoolean(optValueFor(index), fromString = true)

    override fun optLong(index: Int): Long? = convertLong(optValueFor(index), fromString = true)

    override fun optInt(index: Int): Int? = convertInt(optValueFor(index), fromString = true)

    override fun optDouble(index: Int): Double? = convertDouble(optValueFor(index), fromString = true)

    override fun optFloat(index: Int): Float? = convertFloat(optValueFor(index), fromString = true)

    override fun optObj(index: Int): JSObject? = optValueFor(index) as? JSObject

    override fun optArray(index: Int): JSArray? = optValueFor(index) as? JSArray

    private fun getValueFor(index: Int): Any {
        if(index < 0 || index >= size) {
            // Important that we throw an ArrayIndexOutOfBoundsException
            //here and not a JSException
            throw IndexOutOfBoundsException("Index out of bounds: $index")
        }
        val value = this[index]
        if(value === null) {
            throw JSException("Value at index '$index' was null.")
        }
        return value
    }

    private fun optValueFor(index: Int): Any? {
        if(index < 0 || index >= size) {
            // Make sure to return null so we don't hit
            //an IndexOutOfBoundsException
            return null
        }
        return this[index]
    }

    override fun isNull(index: Int): Boolean {
        if(0 > index || index > size - 1) return true
        return this[index] === null
    }

    override fun toJsonString(indent: Int): String = buildJsonArrayString(indent, 0)

    override fun toString(): String = toJsonString(0)
}
