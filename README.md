[version]: https://api.bintray.com/packages/kaidangustave/maven/kotlin-json/images/download.svg
[download]: https://bintray.com/kaidangustave/maven/kotlin-json/_latestVersion
[discord]: https://discord.gg/XCmwxy8
[discord-widget]: https://discordapp.com/api/guilds/301012120613552138/widget.png
[license]: https://img.shields.io/badge/License-Apache%202.0-lightgrey.svg

[ ![version][] ][download]
[ ![license][] ](https://github.com/Shengaero/kotlin-json/tree/master/LICENSE)
[ ![Awesome Kotlin](https://kotlin.link/awesome-kotlin.svg) ](https://github.com/KotlinBy/awesome-kotlin)

[ ![Discord][discord-widget] ][discord]

# Kotlin JSON

Kotlin JSON is a lightweight, highly stylized JavaScript Object Notation (JSON)
for [The Kotlin Programming Language](https://kotlinlang.org/).

### Stylistic

Kotlin JSON allows developers to use the stylistic abilities of Kotlin's DSL API
to create a stylish but highly readable and functional Domain Specifying Language:

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

The following types are supported

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

Kotlin JSON embraces kotlin style in order to create streamlined operations
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

The `reflect` module of the library uses reflection based operations to easily and
efficiently serialize and deserialize `JSObjects` from and into class instances!

For the upcoming examples, consider the following 2 data classes:

```kotlin
data class House(
    val address: String,
    val tenants: List<Person>
)

data class Person(
    val name: String,
    val age: Int,
    @JSOptional val mother: Person? = null,
    @JSOptional val father: Person? = null
)
```

Using the JSDeserializer, JSObjects can be transformed into instances of
data classes, as well as other types.

```kotlin
fun main(args: Array<String>) {
    val deserializer = JSDeserializer()
    val json = JSObject {
        "address" to "123 JSON Lane"

        val mother = JSObject {
            "name" to "Maxine"
            "age" to 42
        }

        val father = JSObject {
            "name" to "Logan"
            "age" to 45
        }

        val child = JSObject {
            "name" to "James"
            "age" to 16
            "mother" to mother
            "father" to father
        }

        "tenants" to JSArray(mother, father, child)
    }

    val house = deserializer.deserialize(json, House::class)
    for(tenant in house.tenants) {
        println("${tenant.name} is ${tenant.age}")
    }
}
// Maxine is 42
// Logan is 45
// James is 16
```

You can also easily serialize any type into a JSObject using an instance of `JSSerializer`!

```kotlin
fun main(args: Array<String>) {
    val serializer = JSSerializer()
    val house = House(
        address = "123 JSON Lane",
        tenants = listOf(
            Person(name = "John", age = 22),
            Person(name = "Jill", age = 20)
        )
    )

    val json = serializer.serialize(house)
    println(json)
}
// {"address":"123 JSON Lane","tenants":[{"name":"John","age":22}, {"name":"Jill","age":20}]}
```

## Dependency Setup
**Gradle**
```groovy
repositories {
    jcenter()
}

dependencies {
    compile "me.kgustave:kotlin-json:${kotlin_json_version}"

    // for reflection support add this
    compile "me.kgustave:kotlin-json-reflect:${kotlin_json_version}"
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
    <artifactId>kotlin-json</artifactId>
    <version>${kotlin_json_version}</version>
    <type>pom</type>
  </dependency>

  <!-- for reflection support add this -->
  <dependency>
    <groupId>me.kgustave</groupId>
    <artifactId>kotlin-json-reflect</artifactId>
    <version>${kotlin_json_version}</version>
  </dependency>
</dependencies>
```

## License

kotlin-json is licensed under the [Apache 2.0 License](https://github.com/Shengaero/kotlin-json/tree/master/LICENSE)

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
