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
package me.kgustave.json.options

// TODO Some considerations for options:
// - String2Long: Convert strings to longs automatically
// - No Nulls: Whether or not nullable values are allowed when parsing
// - BigInteger: Support for parsing BigIntegers
// - Shorts: Support for short values

/**
 * Options used for parsing functions.
 *
 * @author Kaidan Gustave
 * @since  1.2
 */
interface JSParsingOptions: Cloneable {
    /**
     * Whether or not parsing should support comments.
     *
     * This will filter all lines starting with `//`,
     * leaving only lines that don't to be parsed.
     *
     * By default this is `false`.
     */
    val comments get() = false

    /**
     * Creates a clone of this [JSParsingOptions].
     */
    override fun clone(): JSParsingOptions = Copy(this)

    /**
     * Global default [JSParsingOptions].
     */
    companion object: JSParsingOptions {
        override var comments = super.comments
    }

    // Simple copy class
    private class Copy(base: JSParsingOptions) : JSParsingOptions {
        override val comments = base.comments
    }
}