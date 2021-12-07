# Application Type

> This module takes care of the general setup for application projects.

## What does it do?

- will apply the `java` plugin
- sets the `sourceCompatibility` and `targetCompatibility` to the default java version
- sets the `encoding` to `UTF-8`

## Configuration

**set the java version, defaults to java 11**

```gradle
projectConfiguration {
    javaVersion.set(JavaVersion.VERSION_11)
}
```
