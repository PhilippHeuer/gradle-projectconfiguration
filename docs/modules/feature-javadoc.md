# Feature - Javadoc

> configuration of javadoc tasks

## What does it do?

- set `javadoc.options.windowTitle` to `${rootProject.name} (v${project.version})`
- set `javadoc.options.encoding` to `UTF-8` / overwrite via `javadocEncoding`
- set `javadoc.options.locale` to `en` / overwrite via `javadocLocale`
- set `javadoc.options.links` to configured value in `javadocLinks`
- set `javadoc.options.groups` to configured value in `javadocGroups`
- set `javadoc.options.overview` to configured value in `javadocOverviewTemplate` or `javadocOverviewAggregateTemplate`
- set `javadoc.options.html5` to `true` on JDK9 or above
- fix for JDK11 and above - javadoc's `package-list` file is superseded by `element-list`, but a lot of external tools still need it
- library projects with subprojects: adds a `aggregateJavadoc` task
    - output in `${rootDir}/build/javadoc-aggregate`
    - clears the output dir before generation
    - merges options (links, groups, jflags)

## Configuration

**change encoding**

```gradle
projectConfiguration {
    javadocEncoding.set("UTF-8")
}
```

**change locale**

```gradle
projectConfiguration {
    javadocLocale.set("en")
}
```

**link other javadocs**

```gradle
projectConfiguration {
    javadocLinks.set(listOf(
        "https://javadoc.io/doc/org.jetbrains/annotations/latest"
    ))
}
```

**package grouping into modules**

```gradle
projectConfiguration {
    javadocGroups.set(mapOf(
        "com.example.api" to "API",
        "com.example.app" to "App"
    ))
}
```
