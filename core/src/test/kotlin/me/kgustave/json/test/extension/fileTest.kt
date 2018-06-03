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
package me.kgustave.json.test.extension

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import java.io.File
import java.util.stream.Stream
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.*

@[Target(CLASS, FUNCTION, CONSTRUCTOR) Retention(RUNTIME)]
@ArgumentsSource(FileProvider::class)
@ParameterizedTest(name = "{arguments}")
annotation class FileTest(
    val value: String,
    val createNew: Boolean = true,
    val deleteAfter: Boolean = true
)

class FileProvider: ArgumentsProvider {
    private val userDir = System.getProperty("user.dir")
    private lateinit var file: File

    override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
        val provideFile = checkNotNull(fileTestFromContext(context)) { "@FileTest not found!" }
        file = File(userDir + provideFile.value)

        if(provideFile.createNew) {
            if(file.exists()) {
                check(file.delete())
            }
            check(file.createNewFile())
            check(file.exists())
            if(provideFile.deleteAfter) {
                file.deleteOnExit()
            }
        }

        return listOf(Arguments({ arrayOf(file) })).stream()
    }

    private fun fileTestFromContext(context: ExtensionContext): FileTest? {
        return context.testMethod.takeIf { it.isPresent }
            ?.get()?.getAnnotation(FileTest::class.java)
    }
}