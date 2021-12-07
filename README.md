# Project Configuration Gradle Plugin

A gradle plugin to share best-practice project configurations and reduce boilerplate buildscript.

## Installation

Add the plugin to your `build.gradle`:

```gradle
plugins {
    id 'me.philippheuer.configuration' version '$version'
}
```

## Usage

You will need to specify some required options to configure your project, this is needed for the plugin to apply the correct configuration.

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

### Related Plugins ###

This plugin may include the following plugins based on the project configuration:

- [Lombok](https://docs.freefair.io/gradle-plugins/6.3.0/reference/)
- [Gradle Test Logger](https://github.com/radarsh/gradle-test-logger-plugin)
- [Shadow](https://github.com/johnrengelman/shadow)
- [Manifest](https://github.com/coditory/gradle-manifest-plugin)
- [Gradle Versions Plugin](https://github.com/ben-manes/gradle-versions-plugin)
- [Quarkus](https://quarkus.io/)
- [SpringBoot](https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/)
