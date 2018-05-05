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
@file:Suppress("MayBeConstant", "unused")

package me.kgustave.json.test

import me.kgustave.json.JSObject
import me.kgustave.json.annotation.JSName
import me.kgustave.json.annotation.JSValue
import me.kgustave.json.opt
import me.kgustave.json.reflect.json
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@DisplayName("Test Reflection")
class ReflectionTests {
    @Test fun `Object Creation`() {
        val obj = object {
            val name = "Kaidan"
            val age = 19
            val traits = object {
                val hair_color = "Brown"
                val eye_color = "Blue"
            }
        }

        val json = json(obj)

        assertTrue(json.isNotEmpty())
        val traits = assertNotNull(json.opt<JSObject>("traits"))
        assertEquals(obj.age, json.int("age"))
        assertEquals(obj.name, json.string("name"))
        assertEquals(obj.traits.eye_color, traits.string("eye_color"))
        assertEquals(obj.traits.hair_color, traits.string("hair_color"))
    }

    @Test fun `Marked Object Creation`() {
        val json = json(Kaidan)

        assertTrue(json.isNotEmpty())
        assertEquals(Kaidan.age, json.int("age"))
        assertEquals(Kaidan.name, json.string("name"))
    }

    object Kaidan {
        @JSValue
        const val name = "Kaidan"

        @JSValue
        const val age = 19

        @JSValue
        val traits = object {
            @JSValue
            @JSName("hair_color")
            val hairColor = "Brown"

            @JSValue
            @JSName("eye_color")
            val eyeColor = "Blue"
        }
    }
}