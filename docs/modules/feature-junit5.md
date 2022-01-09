# Feature - JUnit5

> Use JUnit5 contains a lot of innovations for Java 8 and above.

## What does it do?

- set `test.useJUnitPlatform()`
- add `testImplementation` dependency on `org.junit.jupiter:junit-jupiter-api`
- add `testImplementation` dependency on `org.junit.jupiter:junit-jupiter-params` (extension for parameterized tests)
- add `testRuntimeOnly` dependency on `org.junit.jupiter:junit-jupiter-engine`
- set `test.dependsOn("cleanTest")` to run all tests even if no changes have been made

## Configuration

**none**
