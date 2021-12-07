# Library Type

> This module takes care of the general setup for library projects.

## What does it do?

- will apply the `java-library` plugin
- sets the `sourceCompatibility` to the default java version
- sets the `encoding` to `UTF-8`
- will generate sourcesJar
- will generate javadocJar

## Configuration

**set the java version, defaults to java 11**

```gradle
projectConfiguration {
    javaVersion.set(JavaVersion.VERSION_11)
}
```
