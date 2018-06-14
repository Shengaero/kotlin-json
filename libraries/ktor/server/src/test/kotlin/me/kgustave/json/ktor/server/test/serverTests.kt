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
package me.kgustave.json.ktor.server.test

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.config.MapApplicationConfig
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.charset
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondBytes
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.testing.contentType
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import me.kgustave.json.JSWriter
import me.kgustave.json.ktor.server.JSContentConverter
import me.kgustave.json.obj
import me.kgustave.json.readJSObject
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

fun Application.testModule() {
    (environment.config as? MapApplicationConfig)?.apply {
        put("ktor.deployment.port", "9090")
        put("ktor.deployment.host", "0.0.0.0")

        put("ktor.application.id", KotlinJsonKtorServerTests::class.qualifiedName!!)

        put("ktor.deployment.shutdown.url", "/shutdown")
    }

    install(ContentNegotiation) {
        register(ContentType.Application.Json, JSContentConverter()) {
            charset(Charsets.UTF_8)
            registerDeserializable(Account::class)
            registerSerializable(Account::class)
            registerDeserializable(User::class)
            registerSerializable(User::class)
        }
    }
}

data class Account(val id: Long, val user: User)

data class User(val name: String, val age: Int)

@DisplayName("Kotlin Json Ktor Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KotlinJsonKtorServerTests {
    private val accounts = mutableMapOf<Long, Account>()

    @ValueSource(longs = [4141245532L, 512423534L, 2412412400L])
    @ParameterizedTest fun `Normal Content Conversion`(testId: Long): Unit = withTestApplication(Application::testModule) {
        application.routing {
            route("/accounts/{id}") {
                get {
                    val id = requireNotNull(call.parameters["id"]?.toLongOrNull()) { "Invalid account ID" }
                    val account = requireNotNull(accounts[id]) { "Account not found" }
                    call.respond(status = HttpStatusCode.OK, message = account)
                }

                post {
                    val id = requireNotNull(call.parameters["id"]?.toLongOrNull()) { "Invalid account ID" }
                    require(id !in accounts) { "Account with ID already present" }
                    val user = call.receive<User>()
                    accounts[id] = Account(id, user)
                    call.respondBytes(bytes = ByteArray(0), status = HttpStatusCode.Created)
                }
            }
        }

        handleRequest(method = HttpMethod.Post, uri = "/accounts/$testId") {
            val json = JSWriter().obj({
                key("name").value("Kaidan")
                key("age").value(19)
            }).toString()
            this.setBody(json.toByteArray(Charsets.UTF_8))
        }

        val call = handleRequest(method = HttpMethod.Get, uri = "/accounts/$testId")
        val byteBody = assertNotNull(call.response.byteContent)
        val json = byteBody.readJSObject(call.response.contentType().charset() ?: Charsets.UTF_8)
        val account = assertNotNull(accounts[json.long("id")])
        assertEquals(account.user.name, json.obj("user").string("name"))
        assertEquals(account.user.age, json.obj("user").int("age"))
    }
}
