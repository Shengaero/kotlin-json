[version]: https://api.bintray.com/packages/kaidangustave/maven/kotlin-json/images/download.svg
[download]: https://bintray.com/kaidangustave/maven/kotlin-json/_latestVersion
[discord]: https://discord.gg/XCmwxy8
[discord-widget]: https://discordapp.com/api/guilds/301012120613552138/widget.png
[license]: https://img.shields.io/badge/License-Apache%202.0-lightgrey.svg
[ ![version][] ][download]
[ ![license][] ](https://github.com/TheMonitorLizard/kotlin-json/tree/master/LICENSE)

[ ![Discord][discord-widget] ][discord]

# Kotlin JSON

Kotlin JSON is a lightweight, highly stylized JavaScript Object Notation (JSON)
for [The Kotlin Programming Language](https://kotlinlang.org/).

### Stylistic

Kotlin JSON allows developers to use the stylistic abilities of Kotlin's DSL API
to create a simplistic but highly readable and functional Domain Specifying
Language:

```kotlin
fun main(args: Array<String>) {
    val json = JSObject {
        "message" to "Hello, World!"
    }
    
    println(json.string("message"))
}
// Hello, World!
```

### Full Type Support

Kotlin JSON supports JSON Objects, JSON Arrays, as well as normal JSON values such 
as Strings, Booleans, Numbers, and null values!

```kotlin
fun main(args: Array<String>) {
    JSObject {
        "null" to null
        "string" to "Hello, World!"
        "int" to 123
        "long" to 1234567891011121314L
        "float" to 123.4
        "double" to 12345678.9101112
        "object" to JSObject { }
        "array" to JSArray()
    }
}
```

### Featured

Kotlin JSON packs standard string parsing and IO operations which allow you to
simplistically read JSON text from anything from a String to an InputStream!

```kotlin
fun main(args: Array<String>) {
      // my-json-file.json
    // {
    //   "message": "Hello, this is from a file!"
    // }
    val myJsonFile = File("my-json-file.json")
    val json = myJsonFile.readJSObject()
    
    println(json.string("message"))
}
// Hello, this is from a file!
```

### Familiar

Kotlin JSON embraces kotlin style in order to create streamlined perations
between it and the kotlin standard library.

```kotlin
fun main(args: Array<String>) {
    // similar to mapOf()
    jsObjectOf("foo" to "bar")
    
    // similar to listOf()
    jsArrayOf("foo", "bar", "baz")
    
    // similar to toMap()
    mapOf("foo" to "bar").toJSObject()
    
    // similar to toList()
    listOf("foo", "bar", "baz").toJSArray()
}
```

The library provides the interfaces `JSObject` and `JSArray` which both extend the
`MutableMap` and `MutableList` interfaces respectively, and allow for a familiar
writing style to the rest of the kotlin language.

### Reflective (EXPERIMENTAL)

The `reflect` module of the library allows reflection based operations, allowing
you to annotate `object` instances to construct `JSObject` instances from them:

```kotlin
object John {
    @JSValue
    const val name = "John"
    
    @JSValue
    const val age = 22
}

fun main(args: Array<String>) {
    val json = json(John)
    println("${json.string("name")} is ${json.int("age")} years old")
}
// John is 22 years old
```

## Dependency Setup
**Gradle**
```groovy
repositories {
    jcenter()
}

dependencies {
    compile "me.kgustave:kotlin-json-core:1.4.0"
}
```

**Maven**
```xml
<repositories>
  <repository>
    <id>jcenter</id>
    <name>jcenter-bintray</name>
    <url>https://jcenter.bintray.com</url>
  </repository>
</repositories>
```

```xml
<dependencies>
  <dependency>
    <groupId>me.kgustave</groupId>
    <artifactId>kotlin-json-core</artifactId>
    <version>1.4.0</version>
    <type>pom</type>
  </dependency>
</dependencies>
```

## License

kotlin-json is licensed under the Apache 2.0 License

```
Copyright 2018 Kaidan Gustave

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
