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

import me.kgustave.json.JSArray
import me.kgustave.json.JSObject
import me.kgustave.json.reflect.JSDeserializer
import me.kgustave.json.reflect.JSSerializer
import me.kgustave.json.reflect.deserialize
import me.kgustave.json.reflect.exceptions.JSReflectionException
import org.junit.jupiter.api.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@DisplayName("Reflection Tests")
class ReflectionTests {
    @Nested
    @DisplayName("Serialization Tests")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class SerializationTests {
        private val serializer = JSSerializer {
            this.isSafe = true
        }

        @BeforeEach fun `Clear Strategies Cache` (info: RepetitionInfo) {
            if(info.currentRepetition == info.totalRepetitions) {
                serializer.cache.clearStrategies()
            }
        }

        @RepeatedTest(100) fun `Serialize Class Instance to JSON` () {
            val logan = Person("Logan", 66)
            val maxine = Person("Maxine", 61)
            val james = Person("James", 22, mother = maxine, father = logan)
            val house = House("123 Home Address", listOf(logan, maxine, james))
            serializer.serialize(house)
        }
    }

    @Nested
    @DisplayName("Deserialization Tests")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class DeserializationTests {
        private val deserializer = JSDeserializer()

        @BeforeEach fun `Clear Strategies Cache` () {
            deserializer.cache.clearStrategies()
        }

        @RepeatedTest(100) fun `Deserialize JSON to Class Instance` () {
            val logan = JSObject {
                "name" to "Logan"
                "age" to 66
            }

            val maxine = JSObject {
                "name" to "Maxine"
                "age" to 61
            }

            val james = JSObject {
                "name" to "James"
                "age" to 22
                "mother" to maxine
                "father" to logan
            }

            val json = JSObject {
                "address" to "123 House Address"
                "tenants" to JSArray(james, logan, maxine)
            }

            val house = deserializer.deserialize<House>(json)
            val person = house.tenants.find { it.name == "James" }

            assertEquals("123 House Address", house.address)
            checkPerson("James", 22, person)
            checkPerson("Maxine", 61, person?.mother)
            checkPerson("Logan", 66, person?.father)
        }

        @RepeatedTest(100) fun `Deserialize JSON to Non-Data Class` () {
            val json = JSObject {
                "name" to "Introduction to Kotlin"
                "pages" to 178
                "author" to null
            }

            val book = deserializer.deserialize<Book>(json)

            assertEquals("Introduction to Kotlin", book.name)
            assertEquals(178, book.pages)
            assertNull(book.author)

            json -= "author"

            assertFailsWith<JSReflectionException> { deserializer.deserialize<Book>(json) }
        }

        private fun checkPerson(expectedName: String, expectedAge: Int, person: Person?) {
            assertNotNull(person)
            assertEquals(expectedName, person?.name)
            assertEquals(expectedAge, person?.age)
        }
    }
}
