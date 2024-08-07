# Project Configuration Gradle Plugin

A gradle plugin to share best-practice project configurations.

Link: [plugins.gradle.org/plugin/me.philippheuer.configuration](https://plugins.gradle.org/plugin/me.philippheuer.configuration)

## Installation

Add the plugin to your `build.gradle`:

```gradle
plugins {
    id 'me.philippheuer.configuration' version '+'
}
```

## Usage

You can configure your project with a few simple parameters:

Example:

```gradle
projectConfiguration {
    language.set(me.philippheuer.projectcfg.domain.ProjectLanguage.JAVA)
    type.set(me.philippheuer.projectcfg.domain.ProjectType.APP)
    framework.set(me.philippheuer.projectcfg.domain.ProjectFramework.SPRINGBOOT)
}
```

*Note*: You can apply this plugin to each of your project modules independently, if your project contains different project types.

Options:

| Option    | Description                         | allowed values      |
|:----------|:------------------------------------|:--------------------|
| language  | the language                        | JAVA, KOTLIN        |
| type      | the project type                    | APP, BATCH, LIBRARY |
| framework | your framework of choice (optional) | SPRINGBOOT, QUARKUS |

## Example Configurations

| Description             | Link |
|:------------------------|:-----|
| MavenCentral Library    | ...  |
| Private Library         | ...  |
| SpringBoot Microservice | ...  |
| Quarkus Microservice    | ...  |

## Modules

There are options to further configure each `feature`, these links document what a `feature` does by default and what customization options are available.

| Type      | Name                   | Docs                                                  |
|:----------|:-----------------------|:------------------------------------------------------|
| Type      | Application            | [view](docs/modules/type-application.md)              |
| Type      | Library                | [view](docs/modules/type-library.md)                  |
| Policy    | Gradle Wrapper Version | [view](docs/modules/policy-gradle-wrapper-version.md) |
| Framework | SpringBoot             | [view](docs/modules/framework-springboot.md)          |
| Framework | Quarkus                | [view](docs/modules/framework-quarkus.md)             |
| Feature   | Lombok                 | [view](docs/modules/feature-lombok.md)                |
| Feature   | Javadoc                | [view](docs/modules/feature-javadoc.md)               |
| Feature   | Test Logging           | [view](docs/modules/feature-test-logging.md)          |
| Feature   | Shadow                 | [view](docs/modules/feature-shadow.md)                |
| Feature   | JUnit5                 | [view](docs/modules/feature-junit5.md)                |
| Feature   | Manifest               | [view](docs/modules/feature-manifest.md)              |
| Feature   | VersionUpgrade         | [view](docs/modules/feature-versionupgrade.md)        |
| Check     | Checkstyle (Java)      | [view](docs/modules/check-checkstyle.md)              |
| Check     | Detekt (Kotlin)        | [view](docs/modules/check-detekt.md)                  |

### Related Plugins ###

This plugin may include the following plugins based on your project configuration:

- [Lombok](https://docs.freefair.io/gradle-plugins/6.3.0/reference/)
- [Gradle Test Logger](https://github.com/radarsh/gradle-test-logger-plugin)
- [Shadow](https://github.com/johnrengelman/shadow)
- [Manifest](https://github.com/coditory/gradle-manifest-plugin)
- [Gradle Versions Plugin](https://github.com/ben-manes/gradle-versions-plugin)
- [Quarkus](https://quarkus.io/)
- [SpringBoot](https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/)
- [Checkstyle](https://docs.gradle.org/current/userguide/checkstyle_plugin.html)
- [Detekt](https://github.com/detekt/detekt)

## License

Released under the [MIT License](./LICENSE).
