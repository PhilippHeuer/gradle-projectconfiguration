# Feature - Publish

> Publish your artifacts

This will be added to all `library` projects

## What does it do?

**Java / Kotlin**

- apply the `maven-publish` plugin
- configure the artifact repository using the information supplied using one of the two methods documented below

## Configuration

You can configure the target using two different methods.

**gradle.properties**

Configuration is done using `gradle.properties`.

You may set `repository.publish.target` to any name like as for example `mavencentral`.
You can configure the details for this name in your local `gradle.properties` file like this:

```gradle.properties
repository.publish.mavencentral.url=https://oss.sonatype.org/service/local/staging/deploy/maven2
repository.publish.mavencentral.username=your_username
repository.publish.mavencentral.password=your_password
```

Note: Do not put these 3 lines into the git repository, put them into the `gradle.properties` file in your user-profile!

**plugin configuration**

```build.gradle.kts
projectConfiguration {
    artifactRepository.set(
        repositories.maven {
            name = "mavencentral"
            url = URI("https://oss.sonatype.org/service/local/staging/deploy/maven2")
            credentials {
                username = "your_username"
                password = "your_password"
            }
        }
    )
}
```
