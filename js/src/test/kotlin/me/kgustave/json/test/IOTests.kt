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
@file:Suppress("unused")
package me.kgustave.json.test

import me.kgustave.json.readJSArray
import me.kgustave.json.readJSObject
import me.kgustave.json.test.mocha.describe
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * @author Kaidan Gustave
 */
class IOTests {
    private val testResources = "src/test/resources"

    @Test fun readJSObjectFromFile() = describe("Read an object from a json file") {
        val json = readJSObject("$testResources/object.json")
        assertTrue("foo" in json)
        assertEquals("bar", json.optString("foo"))
    }

    @Test fun readJSArrayFromFile() = describe("Read an array from a json file") {
        val json = readJSArray("$testResources/array.json")
        assertTrue(json.isNotEmpty())
        assertTrue(json.isNull(1))
        assertEquals("baz", json[2])
    }
}