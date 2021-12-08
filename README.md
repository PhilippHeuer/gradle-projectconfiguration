# Project Configuration Gradle Plugin

A gradle plugin to share best-practice project configurations.

## Installation

Add the plugin to your `build.gradle`:

```gradle
plugins {
    id 'me.philippheuer.configuration' version '$version'
}
```

## Usage

There are a few parameters that are required for in project configurations:

Example:
```gradle
projectConfiguration {
    language.set(me.philippheuer.projectcfg.domain.ProjectLanguage.JAVA)
    type.set(me.philippheuer.projectcfg.domain.ProjectType.APP)
    framework.set(me.philippheuer.projectcfg.domain.ProjectFramework.SPRINGBOOT)
}
```

Options:

| Option | Description | allowed values |
| :--- | :--- | :--- |
| language | the language | JAVA, KOTLIN |
| type | the project type | APP, LIBRARY |
| framework | your framework of choice (optional) | SPRINGBOOT, QUARKUS |

## Modules

| Type | Name | Docs |
| :--- | :--- | :---: |
| Type | Application | [view](docs/modules/type-application.md) |
| Type | Library | [view](docs/modules/type-library.md) |
| Policy | Gradle Wrapper Version | [view](docs/modules/policy-gradle-wrapper-version.md) |
| Framework | SpringBoot | [view](docs/modules/framework-springboot.md) |
| Framework | Quarkus | [view](docs/modules/framework-quarkus.md) |
| Feature | Lombok | [view](docs/modules/feature-lombok.md) |
| Feature | Javadoc | [view](docs/modules/feature-javadoc.md) |
| Feature | Test Logging | [view](docs/modules/feature-test-logging.md) |
| Feature | Shadow | [view](docs/modules/feature-shadow.md) |
| Feature | JUnit5 | [view](docs/modules/feature-junit5.md) |
| Feature | Manifest | [view](docs/modules/feature-manifest.md) |

...

### Related Plugins ###

This plugin may include the following plugins based on the project configuration:

- [Lombok](https://docs.freefair.io/gradle-plugins/6.3.0/reference/)
- [Gradle Test Logger](https://github.com/radarsh/gradle-test-logger-plugin)
- [Shadow](https://github.com/johnrengelman/shadow)
- [Manifest](https://github.com/coditory/gradle-manifest-plugin)
- [Gradle Versions Plugin](https://github.com/ben-manes/gradle-versions-plugin)
- [Quarkus](https://quarkus.io/)
- [SpringBoot](https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/)
