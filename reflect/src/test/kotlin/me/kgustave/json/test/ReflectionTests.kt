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
import org.junit.jupiter.api.*
import kotlin.system.measureTimeMillis
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@DisplayName("Reflection Tests")
class ReflectionTests {
    @Nested
    @DisplayName("Serialization Tests")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class SerializationTests {
        private val serializer = JSSerializer()

        @BeforeEach fun `Clear Strategies Cache` () {
            serializer.cache.clearStrategies()
        }

        @RepeatedTest(100) fun `Serialize Class Instance to JSON` () {
            val logan = Person("Logan", 66)
            val maxine = Person("Maxine", 61)
            val james = Person("James", 22, mother = maxine, father = logan)
            val house = House("123 Home Address", listOf(logan, maxine, james))
            serializer.serialize(house)
        }

        @Test fun `Reduce Runtime Significantly With Strategy Caching` () {
            val house = House(
                address = "44 JSON Lane",
                tenants = listOf(
                    Person(name = "Annie", age = 25),
                    Person(name = "Calvin", age = 28),
                    Person(name = "Marcus", age = 26),
                    Person(name = "Matthew", age = 24),
                    Person(name = "Ellen", age = 24)
                )
            )

            val t1 = measureTimeMillis { serializer.serialize(house) }

            // clear cache
            serializer.cache.clearStrategies()
            serializer.register(House::class)
            serializer.register(Person::class)

            val t2 = measureTimeMillis { serializer.serialize(house) }

            println("No Pre-Register: $t1 ms")
            println("With Pre-Register: $t2 ms")
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

        @Test fun `Reduce Runtime Significantly With Strategy Caching` () {
            val json = JSObject {
                "address" to "44 JSON Lane"
                "tenants" to JSArray(
                    JSObject {
                        "name" to "Annie"
                        "age" to 25
                    },
                    JSObject {
                        "name" to "Calvin"
                        "age" to 28
                    },
                    JSObject {
                        "name" to "Marcus"
                        "age" to 26
                    },
                    JSObject {
                        "name" to "Matthew"
                        "age" to 24
                    },
                    JSObject {
                        "name" to "Ellen"
                        "age" to 24
                    }
                )
            }

            val t1 = measureTimeMillis { deserializer.deserialize<House>(json) }

            // clear cache
            deserializer.cache.clearStrategies()

            deserializer.register(House::class)
            deserializer.register(Person::class)

            val t2 = measureTimeMillis { deserializer.deserialize<House>(json) }

            println("No Pre-Register: $t1 ms")
            println("With Pre-Register: $t2 ms")

            assertTrue(t1 > t2, "No noticeable runtime gains!")
        }

        private fun checkPerson(expectedName: String, expectedAge: Int, person: Person?) {
            assertNotNull(person)
            assertEquals(expectedName, person?.name)
            assertEquals(expectedAge, person?.age)
        }
    }
}