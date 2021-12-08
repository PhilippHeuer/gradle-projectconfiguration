# Framework - SpringBoot

## What does it do?

- will apply the `org.springframework.boot` plugin
- disables the `jar` task in favor of `bootJar`
- adds a `enforcedPlatform` dependency on `org.springframework.boot:spring-boot-dependencies`
- adds a `implementation` dependency on `org.springframework.boot:spring-boot-starter`
- adds a `testImplementation` dependency on `org.springframework.boot:spring-boot-starter-test`

## Requirements

- `ProjectFramework = SPRINGBOOT`

## Configuration

```gradle
projectConfiguration {
    language.set(me.philippheuer.projectcfg.domain.ProjectLanguage.JAVA)
    framework.set(me.philippheuer.projectcfg.domain.ProjectFramework.SPRINGBOOT)
}
```
