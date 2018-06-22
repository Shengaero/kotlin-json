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
package me.kgustave.json.internal

import me.kgustave.json.JSWriter
import java.util.*

/**
 * Internal implementation of [JSWriter].
 *
 * @author Kaidan Gustave
 */
@PublishedApi
internal class JSWriterImpl(private val writer: Appendable = StringBuilder()): JSWriter {
    private val stack = LinkedList<State>()
    private val current: State? get() = stack.peekLast()

    private var started = false
    private var expectingValue = false

    var lockManualEnd
        get() = current?.lockEnding == true
        set(value) { current!!.lockEnding = value }

    override val level get() = stack.size

    override fun obj(): JSWriterImpl {
        stack.add(State(Mode.OBJECT).also {
            it.lockEnding = false
        })
        startCurrent()
        expectingValue = false
        return this
    }

    override fun array(): JSWriterImpl {
        stack.add(State(Mode.ARRAY).also { it.lockEnding = false })
        startCurrent()
        expectingValue = true
        return this
    }

    override fun key(key: String): JSWriterImpl {
        checkObjWriting()
        checkIfKeyExpected()

        current!!.let(State::beforeValue)

        writer.renderString(key)
        writer.append(':')
        expectingValue = true
        return this
    }

    override fun value(value: Any?): JSWriterImpl {
        checkIfValueExpected()
        val asString = renderValue(value, 0, 0, false)
        current!!.let(State::beforeValue)
        if(value is String) writer.renderString(value) else writer.append(asString)
        current!!.let(State::afterValue)
        return this
    }

    override fun endObj(): JSWriterImpl {
        checkLockedManualEnd()
        val last = stack.pollLast()
        checkObjWriting(last)
        endState(last)
        current?.let(State::afterValue)
        return this
    }

    override fun endArray(): JSWriterImpl {
        checkLockedManualEnd()
        val last = stack.pollLast()
        checkArrayWriting(last)
        endState(last)
        current?.let(State::afterValue)
        return this
    }

    private fun startIfNecessary() {
        if(!started) {
            started = true
            return
        } else if(level > 0) {
            return
        }

        throw IllegalStateException("Writer has already started")
    }

    private fun startCurrent() {
        startIfNecessary()
        val current = checkNotNull(this.current) { "Current state was null!" }
        current.beforeValue()
        writer.append(current.mode.start)
    }

    private fun endState(state: State) {
        writer.append(state.mode.end)
    }

    private fun checkIfKeyExpected() {
        check(!expectingValue) { "Key not expected!" }
    }

    private fun checkIfValueExpected() {
        check(expectingValue) { "Value not expected!" }
    }

    private fun checkObjWriting(state: State? = this.current) {
        check(state?.mode == Mode.OBJECT) { "Object writing was not started!" }
    }

    private fun checkArrayWriting(state: State? = this.current) {
        check(state?.mode == Mode.ARRAY) { "Array writing was not started!" }
    }

    private fun checkLockedManualEnd() {
        check(!current!!.lockEnding) { "Manually ending is not allowed in a block context!" }
    }

    private fun checkIfAbleToStringify() {
        check(level == 0) { "Unable to convert writer value to string because level is not 0!" }
    }

    @Throws(Throwable::class)
    override fun close() {
        if(writer is AutoCloseable) {
            writer.close()
        }
    }

    override fun toString(): String {
        checkIfAbleToStringify()
        if(writer is StringBuilder) {
            return writer.toString()
        }
        return "JSWriter(level = $level, content = $writer)"
    }

    private inner class State(val mode: Mode) {
        var commasOn = false
        var lockEnding = false

        fun beforeValue() {
            if(commasOn) {
                writer.append(',')
                if(mode == Mode.OBJECT) {
                    commasOn = false
                }
            }

            if(mode == Mode.OBJECT) {
                expectingValue = true
            }
        }

        fun afterValue() {
            if(!commasOn) {
                commasOn = true
            }
            if(mode == Mode.OBJECT) {
                expectingValue = false
            }
        }
    }

    private enum class Mode(val start: Char, val end: Char) {
        ARRAY(start = '[', end = ']'),
        OBJECT(start = '{', end = '}');
    }
}