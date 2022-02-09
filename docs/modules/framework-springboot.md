# Framework - SpringBoot

## What does it do?

- will apply the `org.springframework.boot` plugin
- disables the `jar` task in favor of `bootJar`
- adds a `enforcedPlatform` dependency on `org.springframework.boot:spring-boot-dependencies`
- adds a `implementation` dependency on `org.springframework.boot:spring-boot-starter`
- adds a `testImplementation` dependency on `org.springframework.boot:spring-boot-starter-test`

Logging

- adds a `implementation` dependency on `org.springframework.boot:spring-boot-starter-log4j2`
- adds a `implementation` dependency on `org.apache.logging.log4j:log4j`
- adds a `implementation` dependency on `com.lmax:disruptor`
- configures async logging globally using `com.lmax:disruptor`
- set `log4j2.formatMsgNoLookups` for improved performance
- set `log4j2.contextSelector` to `org.apache.logging.log4j.core.async.AsyncLoggerContextSelector` to enable async logging globally

Metrics

- adds a `implementation` dependency on `io.micrometer:micrometer-core`
- adds a `implementation` dependency on `io.micrometer:micrometer-registry-prometheus`
- adds a `implementation` dependency on `org.springframework.boot:spring-boot-starter-actuator` (only if `spring-boot-starter-web` is present)

Native (off by default)

- adds the `https://repo.spring.io/release` repository
- will apply the `org.springframework.experimental.aot` plugin
- adds a `implementation` dependency on `org.springframework.experimental:spring-native`
- set `BootBuildImage.builder` to `paketobuildpacks/builder:tiny`
- set `BootBuildImage.buildpacks` to `gcr.io/paketo-buildpacks/java-native-image:7.4.0`
- set `BootBuildImage.environment.BP_NATIVE_IMAGE` to `true`
- set `BootBuildImage.HTTP_PROXY`
- set `BootBuildImage.HTTPS_PROXY`

Config

- configures a resources post-processing gradle task that will modify the application.properties with default values

Actuator

- default port `8081` (should not be exposed, access via kubectl port-forward or by in-cluster prometheus)
- enables web discovery
- enable endpoints: info, scheduledtasks, beans, caches, conditions, quartz, loggers, health, heapdump, threaddump, prometheus
- enable git info mode (also adds git properties plugin to generate git.properties)
- enable health.show-details

## Requirements

- `ProjectFramework = SPRINGBOOT`

## Configuration

```gradle
projectConfiguration {
    language.set(me.philippheuer.projectcfg.domain.ProjectLanguage.JAVA)
    framework.set(me.philippheuer.projectcfg.domain.ProjectFramework.SPRINGBOOT)
}
```
