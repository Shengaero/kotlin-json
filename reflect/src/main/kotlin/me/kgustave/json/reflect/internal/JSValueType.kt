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
package me.kgustave.json.reflect.internal

import me.kgustave.json.JSObject

/**
 * @author Kaidan Gustave
 */
internal enum class JSValueType(val call: (JSObject, String) -> Any?) {
    STRING({ json, key -> json.optString(key) }),
    INT({ json, key -> json.optInt(key) }),
    LONG({ json, key -> json.optLong(key) }),
    FLOAT({ json, key -> json.optFloat(key) }),
    DOUBLE({ json, key -> json.optDouble(key) }),
    OBJECT({ json, key -> json.optObj(key) }),
    ARRAY({ json, key -> json.optArray(key) }),
    OTHER({ _, _ -> throw IllegalStateException("This should not be called!") });
}