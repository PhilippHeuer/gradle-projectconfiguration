# Framework - Quarkus

## What does it do?

- will apply the `io.quarkus` plugin

## Requirements

- `ProjectFramework = QUARKUS`

## Configuration

```gradle
projectConfiguration {
    language.set(me.philippheuer.projectcfg.domain.ProjectLanguage.JAVA)
    framework.set(me.philippheuer.projectcfg.domain.ProjectFramework.QUARKUS)
}
```
