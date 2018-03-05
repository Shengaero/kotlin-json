# kotlin-json

JavaScript Object Notation (JSON) for [Kotlin](https://kotlinlang.org/) JVM.

## Dependency Setup
**Gradle**
```groovy
repositories {
    maven { url "https://dl.bintray.com/kaidangustave/maven" }
}

dependencies {
    compile "me.kgustave:kotlin-json-core:1.0.0"
}
```

**Maven**
```xml
<repositories>
  <repository>
    <snapshots>
      <enabled>false</enabled>
    </snapshots>
    <id>bintray-kaidangustave-maven</id>
    <name>bintray</name>
    <url>https://dl.bintray.com/kaidangustave/maven</url>
  </repository>
</repositories>
```

```xml
<dependencies>
  <dependency>
    <groupId>me.kgustave</groupId>
    <artifactId>kotlin-json-core</artifactId>
    <version>1.0.0</version>
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
