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
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.22")
    implementation("org.jetbrains.kotlin:kotlin-allopen:1.7.22")
    implementation("org.jetbrains.kotlin:kotlin-noarg:1.7.22")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.7.20")

    // third party - spring
    implementation("org.springframework.boot:spring-boot-gradle-plugin:3.0.0")
    //implementation("org.springframework.experimental:spring-native:0.11.2")
    //implementation("org.springframework.experimental:spring-aot-gradle-plugin:0.11.2")

    // third party - quarkus
    implementation("io.quarkus:gradle-application-plugin:2.14.3.Final")

    // third party - plugins
    implementation("io.freefair.gradle:lombok-plugin:6.6")
    implementation("com.adarshr:gradle-test-logger-plugin:3.2.0")
    implementation("gradle.plugin.com.github.johnrengelman:shadow:7.1.2")
    implementation("com.coditory.gradle:manifest-plugin:0.2.1")
    implementation("com.github.ben-manes:gradle-versions-plugin:0.44.0")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.22.0")
    implementation("com.gorylenko.gradle-git-properties:gradle-git-properties:2.4.1")
    implementation("org.cyclonedx:cyclonedx-gradle-plugin:1.7.2")

    // test
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.7.22")
}

configurations {
    implementation.get().exclude(mapOf("group" to "org.jboss.slf4j", "module" to "slf4j-jboss-logmanager"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.run {
    withType<Test> {
        useJUnitPlatform()
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
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
