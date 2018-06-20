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
@file:JsModule("mocha")
@file:JsNonModule
package me.kgustave.json.test.mocha

external val describe: ContextDefinition
external val it: TestDefinition

external interface MochaDone
external interface SuiteBuilder<out R>
external interface TestBuilder<out R>: SuiteBuilder<R>

external interface ContextDefinition: SuiteBuilder<Suite> {
    val only: SuiteBuilder<Suite>
    val skip: SuiteBuilder<Unit>
    fun timeout(ms: Number)
}

external interface Runnable {
    var title: String
    var fn: Function<*>
    var async: Boolean
    var sync: Boolean
    var timedOut: Boolean
}

external interface Suite {
    var parent: Suite
    var title: String
    fun fullTitle(): String
}

external interface Test: Runnable {
    var parent: Suite
    var pending: Boolean
    var state: String? /* "failed" | "passed" | undefined */
    fun fullTitle(): String
}

external interface TestDefinition: TestBuilder<Test> {
    val only: TestBuilder<Test>
    val skip: TestBuilder<Unit>
    var state: String /* "failed" | "passed" */
    fun timeout(ms: Number)
}