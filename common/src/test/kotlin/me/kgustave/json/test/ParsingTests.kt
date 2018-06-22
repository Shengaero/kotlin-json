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
@file:Suppress("TestFunctionName")
package me.kgustave.json.test

import me.kgustave.json.JSObject
import me.kgustave.json.exceptions.JSSyntaxException
import me.kgustave.json.parseJsonArray
import me.kgustave.json.parseJsonObject
import kotlin.test.*

/**
 * @author Kaidan Gustave
 */
@Ignore
class ParsingTests {
    @Test @TestName("Parse Simple Object") fun ParseSimpleObject() {
        val json = parseJsonObject("""
                {
                   "foo": "bar"
                }
        """)
        assertTrue("foo" in json)
        assertEquals(json.string("foo"), "bar")
    }

    @Test @TestName("Parse Complex Object") fun ParseComplexObject() {
        val json = parseJsonObject("""
                {
                   "foo": null,
                   "number": 123,
                   "biz": {
                     "inner": "value"
                   }
                }
            """)

        assertTrue(json.isNull("foo"))
        assertEquals(json.int("number"), 123)
        assertTrue("inner" in json.obj("biz"))
    }

    @Test @TestName("Parse Object With Array") fun ParseObjectWithArray() {
        val json = parseJsonObject("""
                {
                   "values": [1, 5, 3],
                   "sum": 9
                }
            """)

        val values = assertNotNull(json.optArray("values"))
        val sum = values.sumBy { assertNotNull(it as? Int) }
        assertEquals(sum, json.int("sum"))
    }

    @Test @TestName("Try Parse Object And Fail") fun TryParseObjectAndFail() {
        assertFailsWith<JSSyntaxException> {
            // call trim() to avoid intellij error from language injection
            parseJsonObject("""{"": }""".trim())
        }
    }

    @Test @TestName("Parse Simple Array") fun ParseSimpleArray() {
        val json = parseJsonArray("""
                [
                   "foo",
                   "boo"
                ]
            """)

        assertEquals(json.string(0), "foo")
        assertEquals("foo boo", json.joinToString(separator = " "))
    }

    @Test @TestName("Parse Complex Array") fun ParseComplexArray() {
        val json = parseJsonArray("""
                [
                   [2, 5, 7],
                   [1, 8, 4],
                   [9, 9, 3]
                ]
            """)

        assertEquals(json.array(0).int(1), 5)
        assertEquals(json.array(2).int(0), 9)
    }

    @Test @TestName("Parse Array With Object") fun ParseArrayWithObject() {
        val json = parseJsonArray("""
                [
                   {
                      "value": "Hello",
                      "space_after": false
                   },
                   {
                      "value": ",",
                      "space_after": true
                   },
                   {
                      "value": "World!",
                      "space_after": false
                   }
                ]
            """)

        val result = json.joinToString(separator = "") join@ {
            val obj = assertNotNull(it as? JSObject)
            val value = obj.string("value")
            val spaceAfter = obj.boolean("space_after")

            return@join if(spaceAfter) "$value " else value
        }

        assertEquals(result, "Hello, World!")
    }

    @Test @TestName("Try Parse Array And Fail") fun TryParseArrayAndFail() {
        assertFailsWith<JSSyntaxException> {
            // call trim() to avoid intellij error from language injection
            parseJsonObject("""["incomplete]""".trim())
        }
    }
}