/*
 * Copyright 2017 Kaidan Gustave
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
package me.kgustave.json.test

import me.kgustave.json.*
import org.junit.jupiter.api.*
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNotNull

/*
 * String parsing tests for kotlin-json-core.
 * Also covers some IO/File related tests.
 *
 * Creation Date: March 5, 2018
 * Author:        Kaidan Gustave
 *
 * Last Modified: March 5, 2018
 * Changes:       Initial implementation.
 * Version:       1.0.0
 */

@DisplayName("Test Parsing")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestParsing {
    @[Test Parsing]
    @DisplayName("Parse JSObject")
    fun testParseJsonObject() {
        val json = parseJsonObject("""
            {
              "test": "Hello, World!"
            }
        """)

        assertEquals("Hello, World!", json["test"])
    }

    @[Test Parsing]
    @DisplayName("Parse JSArray")
    fun testParseJsonArray() {
        val json = parseJsonArray("""
            [
              null,
              "Hello, World!",
              -3,
              65.7
            ]
        """)

        assertEquals(4, json.size)
    }

    @[Test Parsing]
    @DisplayName("Parse Complex JSON")
    fun testComplexParse() {
        val json = parseJsonArray("""
            [
              {
                "name": "Kaidan",
                "age": 19
              },
              null,
              {
                "foo": "bar",
                "baz": "biz",
                "inner": {
                  "json": "object",
                  "number": 1234
                }
              }
            ]
        """)

        assertEquals(3, json.size)

        val obj = json.obj(0)

        assertNotNull(obj)
        assertNotNull(obj.string("name"))
        assertNotNull(obj.int("age"))

        assert(json.isNull(1))
    }

    @[Test IO]
    @DisplayName("Read JSON File From URL")
    fun testReadURL() {
        val json = this::class.java.getResource("/test.json").readJSObject()

        assertNotNull(json["github"])
        val repositories = assertNotNull(json["repositories"] as? JSArray)

        assertEquals(repositories.size, 1)
        assertNotNull(repositories[0] as? JSObject)
    }

    @[Test IO]
    @DisplayName("Read Invalid JSON File From Input Stream")
    fun testIOError() {
        assertFails { this::class.java.getResourceAsStream("/empty.txt").readJSObject() }
    }
}
