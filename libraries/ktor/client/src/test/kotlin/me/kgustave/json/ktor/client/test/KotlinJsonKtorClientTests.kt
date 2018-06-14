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
@file:Suppress("MemberVisibilityCanBePrivate")

package me.kgustave.json.ktor.client.test

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.tests.utils.clientTest
import io.ktor.client.tests.utils.config
import io.ktor.client.tests.utils.test
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.http.*
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import me.kgustave.json.JSArray
import me.kgustave.json.JSObject
import me.kgustave.json.ktor.client.JSKtorSerializer
import me.kgustave.json.ktor.client.json
import me.kgustave.json.ktor.client.test.extension.EngineTest
import me.kgustave.json.ktor.client.test.extension.EngineType.APACHE
import me.kgustave.json.ktor.client.test.extension.EngineType.CIO
import me.kgustave.json.ktor.server.JSContentConverter
import me.kgustave.json.reflect.JSDeserializer
import me.kgustave.json.reflect.JSSerializer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance
import org.slf4j.event.Level
import java.util.concurrent.TimeUnit
import kotlin.collections.set
import io.ktor.client.engine.HttpClientEngineFactory as Factory

@DisplayName("Kotlin Json Ktor Client Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KotlinJsonKtorClientTests {
    private companion object {
        private const val port = 9090
        private val charset = Charsets.UTF_8
        private val contentType = ContentType.Application.Json
        private val jsSerializer = JSSerializer()
        private val jsDeserializer = JSDeserializer()
        private val ktorSerializer = JSKtorSerializer(jsSerializer, jsDeserializer, charset)
        private val home = House(address = "123 My House", tenants = listOf(Person(name = "Kaidan", age = 19)))
    }

    private lateinit var server: NettyApplicationEngine
    private val houses = mutableMapOf<String, House>()

    @BeforeEach fun initialize() {
        this.server = embeddedServer(Netty, port = port, watchPaths = listOf()) {
            install(CallLogging) {
                level = Level.DEBUG
            }
            install(ContentNegotiation) {
                val converter = JSContentConverter(jsDeserializer, jsSerializer)
                register(contentType, converter) {
                    charset(charset)
                    registerSerializable(House::class)
                    registerDeserializable(House::class)
                    registerSerializable(Person::class)
                    registerDeserializable(Person::class)
                }
            }
            routing {
                route("/houses") {
                    get("/{address}") {
                        val address = call.parameters["address"]!!
                        val house = houses[address]!!
                        call.response.headers.append(HttpHeaders.ContentType, "application/json;charset=utf-8", false)
                        call.respond(house)
                    }
                    post {
                        val house = call.receive<House>()
                        require(house.address !in houses) { "Address already registered" }
                        houses[house.address] = house
                        call.respond(HttpStatusCode.Created)
                    }
                }
            }
        }

        this.server.start(wait = false)
    }

    @EngineTest([CIO, APACHE]) fun `Serialize Request Content`(factory: Factory<*>) = clientTest(factory) {
        config {
            install(JsonFeature) {
                serializer = ktorSerializer
            }
        }

        test { client ->
            client.post<Unit>("http", port = port, path = "/houses") {
                contentType(contentType.withCharset(charset))
                accept(contentType.withCharset(charset))
                body = home
            }
        }
    }

    @EngineTest([CIO, APACHE]) fun `Raw JS Request Content`(factory: Factory<*>) = clientTest(factory) {
        config {
            install(JsonFeature) {
                serializer = ktorSerializer
            }
        }

        test { client ->
            client.post<Unit>("http", port = port, path = "/houses") {
                contentType(contentType.withCharset(charset))
                json {
                    "address" to "123 My House"
                    "tenants" to JSArray(
                        JSObject {
                            "name" to "Kaidan"
                            "age" to 19
                        }
                    )
                }
            }
        }
    }

    @AfterEach fun destroy() {
        this.server.stop(0, 0, TimeUnit.MILLISECONDS)
        this.houses.clear()
    }

    data class House(val address: String, val tenants: List<Person>)

    data class Person(val name: String, val age: Int)
}