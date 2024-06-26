plugins {
    kotlin("jvm")
    id("java-gradle-plugin")
    id("com.gradle.plugin-publish")
    id("maven-publish")
}

dependencies {
    // plugin
    implementation(gradleApi())

    // kotlin
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.0")
    implementation("org.jetbrains.kotlin:kotlin-allopen:2.0.0")
    implementation("org.jetbrains.kotlin:kotlin-noarg:2.0.0")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.9.20")

    // third party - plugins
    implementation("io.freefair.gradle:lombok-plugin:8.6")
    implementation("com.adarshr:gradle-test-logger-plugin:4.0.0")
    implementation("gradle.plugin.com.github.johnrengelman:shadow:8.0.0")
    implementation("com.github.ben-manes:gradle-versions-plugin:0.51.0")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.6")
    implementation("com.gorylenko.gradle-git-properties:gradle-git-properties:2.4.2")
    implementation("org.cyclonedx:cyclonedx-gradle-plugin:1.8.2")

    // test
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.3")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.3")
    testImplementation("org.jetbrains.kotlin:kotlin-test:2.0.0")
}

configurations {
    implementation.get().exclude(mapOf("group" to "org.jboss.slf4j", "module" to "slf4j-jboss-logmanager"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.run {
    withType<Test> {
        useJUnitPlatform()
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}

gradlePlugin {
    plugins {
        create(PluginCoordinates.ID) {
            id = PluginCoordinates.ID
            implementationClass = PluginCoordinates.IMPLEMENTATION_CLASS
            version = project.version
        }
    }
}

pluginBundle {
    website = PluginBundle.WEBSITE
    vcsUrl = PluginBundle.VCS
    description = PluginBundle.DESCRIPTION
    tags = PluginBundle.TAGS

    plugins {
        getByName(PluginCoordinates.ID) {
            displayName = PluginBundle.DISPLAY_NAME
        }
    }

    mavenCoordinates {
        groupId = PluginCoordinates.GROUP
        artifactId = PluginCoordinates.ID
        version = project.version as String
    }
}
