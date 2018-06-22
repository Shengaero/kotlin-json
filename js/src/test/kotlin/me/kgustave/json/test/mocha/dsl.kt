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
package me.kgustave.json.test.mocha

@DslMarker
internal annotation class MochaDsl

@MochaDsl
fun <R> TestBuilder<R>.async(description: String, callback: (MochaDone) -> Unit): R {
    return asDynamic()(description, callback).unsafeCast<R>()
}

@MochaDsl
operator fun <R> SuiteBuilder<R>.invoke(description: String, callback: () -> Unit): R {
    return asDynamic()(description, callback).unsafeCast<R>()
}

@MochaDsl
operator fun MochaDone.invoke(error: Throwable): dynamic = asDynamic()(error)

@MochaDsl
operator fun MochaDone.invoke(): dynamic = asDynamic()()

@MochaDsl
fun it(description: String, skip: Boolean = false, callback: () -> Unit) {
    if(skip) {
        return it.skip(description, callback)
    }
    it.only(description, callback)
}

@MochaDsl
fun describe(description: String, callback: () -> Unit): Suite = describe.only(description, callback)

@MochaDsl
fun async(description: String, skip: Boolean = false, callback: (MochaDone) -> Unit) {
    if(skip) {
        return it.skip(description) { callback(EmptyDone) }
    }
    it.async(description, callback)
}

object EmptyDone: MochaDone