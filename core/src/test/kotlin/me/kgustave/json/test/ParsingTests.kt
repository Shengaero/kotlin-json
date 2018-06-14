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
@file:Suppress("ClassName")

package me.kgustave.json.test

import me.kgustave.json.*
import me.kgustave.json.exceptions.JSSyntaxException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@DisplayName("Parsing Tests")
class ParsingTests {
    @Nested inner class `Json Objects` {
        @Test fun `Parse Simple`() {
            val json = parseJsonObject("""
                {
                   "foo": "bar"
                }
            """)
            assertTrue("foo" in json)
            assertEquals(json.string("foo"), "bar")
        }

        @Test fun `Parse Complex`() {
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

        @Test fun `Parse With Array`() {
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

        @Test fun `Try Parse And Fail`() {
            assertFailsWith<JSSyntaxException> {
                // call trim() to avoid intellij error from language injection
                parseJsonObject("""{"": }""".trim())
            }
        }
    }

    @Nested inner class `Json Arrays` {
        @Test fun `Parse Simple`() {
            val json = parseJsonArray("""
                [
                   "foo",
                   "boo"
                ]
            """)

            assertEquals(json.string(0), "foo")
            assertEquals("foo boo", json.joinToString(separator = " "))
        }

        @Test fun `Parse Complex`() {
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

        @Test fun `Parse With Object`() {
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

        @Test fun `Try Parse And Fail`() {
            assertFailsWith<JSSyntaxException> {
                // call trim() to avoid intellij error from language injection
                parseJsonObject("""["incomplete]""".trim())
            }
        }
    }

    @Nested inner class `IO Operations` {
        @Test fun `Read JSObject From Resource`() {
            val url = checkNotNull(this::class.java.getResource("/test.json"))
            val json = url.readJSObject()

            assertNotNull(json["github"] as? String)
            val repositories = assertNotNull(json.optArray("repositories"))
            assertEquals(repositories.size, 1)
            assertNotNull(repositories[0] as? JSObject)
        }

        @Test fun `Read Failure From Invalid Syntax`() {
            assertFailsWith<JSSyntaxException> {
                this::class.java.getResource("/empty.txt").readJSObject()
            }
        }
    }
}