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
package me.kgustave.json.ktor.client.test.extension

import io.ktor.client.engine.HttpClientEngineFactory
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import java.lang.annotation.Inherited
import java.util.stream.Stream
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.kotlinFunction
import io.ktor.client.engine.apache.Apache as ApacheFactory
import io.ktor.client.engine.cio.CIO as CIOFactory
import io.ktor.client.engine.jetty.Jetty as JettyFactory
import org.junit.platform.commons.util.PreconditionViolationException as Violation

@Inherited
@Target(FUNCTION)
@Retention(RUNTIME)
@ArgumentsSource(EngineProvider::class)
@ParameterizedTest(name = "[{index}] - {0}")
annotation class EngineTest(val values: Array<out EngineType> = [
    EngineType.APACHE,
    EngineType.CIO,
    EngineType.JETTY
])

enum class EngineType(val factory: HttpClientEngineFactory<*>) {
    APACHE(ApacheFactory),
    CIO(CIOFactory),
    JETTY(JettyFactory)
}

internal class EngineProvider: ArgumentsProvider {
    private companion object {
        private val targetType = HttpClientEngineFactory::class.createType(listOf(KTypeProjection.STAR))
    }
    override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
        val function = context.requiredTestMethod.kotlinFunction ?:
                       throw Violation("${context.displayName} is not a kotlin function!")
        val parameter = function.valueParameters.takeIf { it.size == 1 }?.get(0) ?:
                        throw Violation("${context.displayName} must have a single parameter with type $targetType!")
        if(parameter.type != targetType) {
            throw Violation("${context.displayName} must have a single parameter with type $targetType!")
        }
        val engineTest = function.findAnnotation<EngineTest>() ?:
                         throw Violation("${context.displayName} does not have an @EngineTest annotation!")
        return engineTest.values.toList().stream().map { Arguments { arrayOf(it.factory) } }
    }
}