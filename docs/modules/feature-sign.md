# Feature - Sign

> Sign your artifacts, prerequisite for publishing most library projects

This will be added to all `library` projects

## What does it do?

**Java / Kotlin**

- apply the `signing` plugin
- will sign the `main` publication

## Configuration

Sample:
```gradle.properties
# GPG
signing.gnupg.executable=gpg
signing.gnupg.keyName=7009X1Z1
```

Also see: https://docs.gradle.org/current/userguide/signing_plugin.html
