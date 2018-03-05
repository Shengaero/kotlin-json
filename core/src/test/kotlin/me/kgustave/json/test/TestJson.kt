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

import me.kgustave.json.JSObject
import me.kgustave.json.jsonArray
import me.kgustave.json.jsonObject
import org.junit.jupiter.api.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/*
 * General tests for kotlin-json-core.
 *
 * Creation Date: March 5, 2018
 * Author:        Kaidan Gustave
 *
 * Last Modified: March 5, 2018
 * Changes:       Initial implementation.
 * Version:       1.0.0
 */

@DisplayName("Test JSON")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestJson {
    @Test
    @DisplayName("Test Simple JSObject Creation")
    fun testJSObject() {
        val json = jsonObject(
            "name" to "Kaidan",
            "age" to 19,
            "traits" to jsonObject(
                "hair_color" to "brown",
                "eye_color" to "blue"
            )
        )
        assert(json.size == 3)

        val name = json.string("name")
        val age = json.int("age")
        val traits = json.obj("traits")
        val hairColor = traits.string("hair_color")
        val eyeColor = traits.string("eye_color")

        assertNotNull(name)
        assertNotNull(age)
        assertNotNull(traits)
        assertNotNull(hairColor)
        assertNotNull(eyeColor)
    }

    @Test
    @DisplayName("Test Simple JSArray Creation")
    fun testJSArray() {
        val json = jsonArray(
            "Hello, World!",
            jsonObject(
                "two" to 2,
                "three" to 3
            ),
            null
        )

        assert(json.size == 3)

        for((i, v) in json.withIndex()) {
            when(i) {
                0 -> assertNotNull(v as? String)
                1 -> assertNotNull(v as? JSObject)
                2 -> assertNull(v)
            }
        }
    }

    @Test
    @DisplayName("Test Print Pretty JSON")
    fun testPrintPretty() {
        val json = jsonArray(
            jsonObject(
                "two" to 2,
                "three" to 3
            ),
            jsonObject(
                "four" to 4,
                "five" to 5,
                "six" to jsonObject(
                    "seven" to jsonArray(8, 9, 10),
                    "nine" to 9,
                    "ten" to jsonObject(
                        "eleven" to 11.0,
                        "negative_twelve" to -12
                    )
                )
            ),
            jsonArray(13, 14, 15, 16)
        )

        assertEquals(json.size, 3)

        val string = json.toJsonString(2)

        assert(string.split("\n").size > 1)
    }

    @Test
    @DisplayName("Test JSON DSL")
    fun testDsl() {
        val json = jsonObject {
            this["name"] = "Kaidan"
            this["age"] = 19
            this["traits"] = jsonObject {
                this["hair_color"] = "brown"
                this["eye_color"] = "blue"
            }
        }

        val traits = assertNotNull(json["traits"] as? JSObject)

        assertEquals(json.string("name"), "Kaidan")
        assertEquals(json.int("age"), 19)
        assertEquals(traits.string("hair_color"), "brown")
        assertEquals(traits.string("eye_color"), "blue")
    }
}
