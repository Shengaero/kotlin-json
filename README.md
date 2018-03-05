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
