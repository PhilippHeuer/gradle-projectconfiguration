# Feature - Lombok

> This will apply the lombok plugin to the project.

## What does it do?

- apply the `io.freefair.lombok` plugin to all projects
- set `lombok.disableConfig` to `true`
- set `lombok.version` to `1.18.22`
- set `javadoc.source` to `delombok`
- set `javadoc.dependsOn` to `delombok`
- set `javadoc.options.Xdoclint:none` to `-quiet`

## Requirements

- `ProjectLanguage = JAVA`

## Configuration

**include or exclude delombok sources in javadoc**

```gradle
projectConfiguration {
    javadocLombok.set(true)
}
```
