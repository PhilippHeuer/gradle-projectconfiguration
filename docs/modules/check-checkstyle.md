# Check - Checkstyle

> Checkstyle is a development tool to help programmers write Java code that adheres to a coding standard.
> It automates the process of checking Java code to spare humans of this boring (but important) task.
> This makes it ideal for projects that want to enforce a coding standard.
>
> https://checkstyle.sourceforge.io/

## What does it do?

- will apply the `checkstyle` plugin
- add task `checkstyleAll` in the `verification` group
- set `checkstyle.maxWarnings` to `0`
- set `checkstyle.maxErrors` to `0`
- will by default use the `checkstyle.xml` in the root project

## Configuration

**use a custom checkstyle version**

```gradle
projectConfiguration {
    checkstyleToolVersion.set("9.2.1")
}
```

**use a prepared ruleset**

Valid: google, sun, twitch4j

```gradle
projectConfiguration {
    checkstyleRuleSet.set("google")
}
```
