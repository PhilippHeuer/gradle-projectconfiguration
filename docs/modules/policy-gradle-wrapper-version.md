# Policy - Gradle Wrapper Version

> This module will ensure that only versions of gradle that have been tested with this plugin can be used.

## What does it do?

This module will:

- check the used gradle version against a list of tested versions
- on fail: set the version of the wrapper task to a supported one, so run `wrapper` to change to a supported version

## Configuration

**disable**

```gradle
projectConfiguration {
    gradleVersionPolicyEnabled.set(false)
}
```
