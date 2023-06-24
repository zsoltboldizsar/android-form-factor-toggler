val studioCompilePath: String by project
val studioRunPath: String by project

plugins {
    id("org.jetbrains.intellij") version "1.14.1"
    java
    kotlin("jvm") version "1.8.22"
}

group = "dev.boldizsar.zsolt"
version = "0.7.0"

repositories {
    mavenCentral()
}

dependencies {
    val androidPlugin =
        fileTree(baseDir = "$studioCompilePath/plugins/android/lib").apply {
            include("*.jar")
        }
    compileOnly(androidPlugin)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0")

    testImplementation("junit", "junit", "4.12")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    updateSinceUntilBuild.set(false)
    localPath.set(if (project.hasProperty("studioRunPath")) studioRunPath else studioCompilePath)
}
configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "17"
    }
    instrumentCode {
        // Depending on your local IDE used ($studioCompilePath) a corresponding compiler version must be set.
        // The releases can be found at: https://www.jetbrains.com/intellij-repository/releases
        compilerVersion.set("223.7571.182")
    }
}