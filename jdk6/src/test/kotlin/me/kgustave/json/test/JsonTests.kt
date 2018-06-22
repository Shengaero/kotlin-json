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
package me.kgustave.json.test

import me.kgustave.json.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * @author Kaidan Gustave
 */
class JsonTests {
    @Test fun `Dsl Style Creation`() {
        val json = JSObject {
            "foo" to "bar"
            "biz" to null
            "array" to JSArray(5, 10.4, "abc")
            "inner" to JSObject {
                "boo" to JSArray(4) { if(it == 1) "bar" else null }
            }
        }

        assertEquals(json.size, 4)
        assertTrue("foo" in json)
        assertTrue(json.isNull("biz"))
        assertEquals(json.array("array").size, 3)
        assertEquals(json.obj("inner").array("boo").count { it === null }, 3)
    }

    @Test fun `mapOf Style Creation`() {
        val json = jsObjectOf("foo" to "bar", "baz" to 123.4, "biz" to null)

        assertEquals(json.string("foo"), "bar")
        assertTrue("baz" in json)
        assertNull(json["biz"])
    }

    @Test fun `listOf Style Creation`() {
        val json = jsArrayOf("abc", 123, null, "cba")

        assertEquals(4, json.size)
        for((i, v) in json.withIndex()) {
            when(i) {
                0, 3 -> assertTrue(v is String)
                1 -> assertTrue(v is Int)
                2 -> assertNull(v)
            }
        }
    }

    @Test fun `Write Json`() {
        JSWriter().obj()
            .key("foo").value("bar")
            .endObj()
    }
}