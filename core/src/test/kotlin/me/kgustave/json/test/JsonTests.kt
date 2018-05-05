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
@file:Suppress("ClassName", "RedundantCompanionReference")
package me.kgustave.json.test

import me.kgustave.json.*
import me.kgustave.json.exceptions.JSException
import me.kgustave.json.test.extension.FileTest
import me.kgustave.json.test.extension.SingleInstance
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import java.io.File
import java.math.BigInteger
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.test.*

@DisplayName("Json Tests")
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

    @Nested
    inner class `Json Objects` {
        @Test fun `mapOf Style Creation`() {
            val json = jsObjectOf("foo" to "bar", "baz" to 123.4, "biz" to null)

            assertEquals(json.string("foo"), "bar")
            assertTrue("baz" in json)
            assertNull(json["biz"])
        }

        @TypeValidationTest fun `Allows Valid Types`(type: Any?) {
            JSObject { "key" to type }
        }

        @Nested
        @SingleInstance
        inner class `Mutating Json Objects` {
            private val json = jsObjectOf()

            @RepeatedTest(10, name = SimpleRepeatedTestName)
            fun `Add Values`(info: RepetitionInfo) {
                val rep = info.currentRepetition
                json["value$rep"] = rep

                assertEquals(json.size, rep)
                for(i in 1..rep)
                    assertTrue(json.containsKey("value$i"))
            }

            @RepeatedTest(10, name = SimpleRepeatedTestName)
            fun `Update Values`(info: RepetitionInfo) {
                val rep = info.currentRepetition
                json["value"] = rep

                assertEquals(json.size, 1)
                assertTrue(json.containsKey("value"))
                assertTrue(json.containsValue(rep))
                for(i in (rep - 1) downTo 0)
                    assertFalse(json.containsValue(i))
            }

            @AfterEach fun `Cleanup Repeated Test`(info: RepetitionInfo) {
                if(info.currentRepetition == info.totalRepetitions) json.clear()
            }
        }
    }

    @Nested
    inner class `Json Arrays` {
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

        @TypeValidationTest fun `Allows Valid Types`(type: Any?) {
            JSArray(type)
        }

        @Nested
        @SingleInstance
        inner class `Mutating Json Arrays` {
            private val json = jsArrayOf()

            @RepeatedTest(10, name = SimpleRepeatedTestName)
            fun `Add Values`(info: RepetitionInfo) {
                val rep = info.currentRepetition
                json += (rep - 1)

                assertEquals(json.size, rep)
                for(i in 0 until rep) {
                    assertTrue(i <= json.size)
                    assertEquals(i, json[i])
                }
            }

            @RepeatedTest(10, name = SimpleRepeatedTestName)
            fun `Update Values`(info: RepetitionInfo) {
                val rep = info.currentRepetition
                json[0] = rep

                assertEquals(json.size, 1)
                for(i in (rep - 1) downTo 0)
                    assertFalse(i in json)
            }

            @AfterEach fun `Cleanup Repeated Test`(info: RepetitionInfo) {
                if(info.currentRepetition == info.totalRepetitions) json.clear()
            }
        }
    }

    @Nested
    inner class `Json Writer` {
        @Test fun `Write Json`() {
            JSWriter().obj()
                .key("foo").value("bar")
                .endObj()
        }

        @FileTest("/test.json") fun `Write Json To File`(file: File) {
            if(file.exists()) {
                // delete and create new
                check(file.delete())
                check(file.createNewFile())
            }

            check(file.exists())

            file.writer().use {
                JSWriter(it).obj {
                    key("foo").obj {
                        key("number").value(123)
                    }
                    key("baz").value(100.4)
                }
            }

            val json = file.readJSObject()

            assertTrue("baz" in json)
            assertEquals(json.obj("foo").int("number"), 123)
        }
    }

    @Nested
    @SingleInstance
    inner class `Type Safe` {
        private val json = JSObject {
            "null_value" to null
            "int" to 5
            "long" to 44L
            "float" to 12.3f
            "double" to 99.4
            "string" to "Hello, World!"
            "inner_object" to JSObject { "foo" to "bar" }
            "inner_array" to JSArray(1, 4, 7)
        }

        @Test fun `Opt Value`() {
            assertNotNull(json.opt<String>("string"))
            assertNotNull(json.opt<Int>("int"))
            assertNull(json.opt<Int>("float"))
        }

        @Test fun `Loose Numeric Types`() {
            try {
                json.double("float")
                json.long("int")
            } catch(e: JSException) {
                asserter.fail("Failed: ${e.message}")
            }
            assertFailsWith<JSException> { json.float("double") }
            assertFailsWith<JSException> { json.int("long") }
        }

        @Test fun `Fail When Does Not Exist`() {
            assertFailsWith<JSException> { json.string("not_a_value") }
        }

        @Test fun `Fail When Null`() {
            assertTrue(json.isNull("null_value"))
            assertFailsWith<JSException> { json.obj("null_value") }
        }

        @Test fun `Fail When Different Type`() {
            assertFailsWith<JSException> { json.array("inner_object") }
            assertFailsWith<JSException> { json.obj("inner_array") }
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ValidTypesProvider::class)
    private annotation class TypeValidationTest

    // Arguments Provider for JS type validation
    class ValidTypesProvider: ArgumentsProvider {
        private companion object Instance: ArgumentsProvider {
            val NULL: Any? = null
            const val INT = 123
            const val LONG = 789L
            const val FLOAT = 123.4f
            const val DOUBLE = 5.678
            const val STRING = "abc"
            val ATOMIC_INT = AtomicInteger(1111)
            val ATOMIC_LONG = AtomicLong(9999)
            val BIG_INT = BigInteger.valueOf(9000000000L)!!
            val JS_OBJECT = JSObject { "foo" to "bar" }
            val JS_ARRAY = JSArray(1, 2, "3", null)
            val MAP = mapOf("baz" to "buz")
            val PAIR = "hello" to "world"
            val COLLECTION = listOf(1, 56.5, "aaa")
            val ARRAY = arrayOf("faz", "fiz", 4563)

            val ALL_TYPES = listOf(NULL, INT, LONG, FLOAT,
                DOUBLE, STRING, ATOMIC_INT, ATOMIC_LONG,
                BIG_INT, JS_OBJECT, JS_ARRAY, MAP, PAIR,
                COLLECTION, ARRAY).map { Arguments { arrayOf(it) } }

            override fun provideArguments(context: ExtensionContext) = ALL_TYPES.stream()
        }

        override fun provideArguments(context: ExtensionContext) = Instance.provideArguments(context)
    }
}