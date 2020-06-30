val studioCompilePath: String by project
val studioRunPath: String by project

plugins {
    id("org.jetbrains.intellij") version "0.4.21"
    java
    kotlin("jvm") version "1.3.70"
}

group = "dev.boldizsar.zsolt"
version = "0.6.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    val androidPlugin =
        fileTree(baseDir = "$studioCompilePath/plugins/android/lib").apply {
            include("*.jar")
        }
    compileOnly(androidPlugin)

    testCompile("junit", "junit", "4.12")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    updateSinceUntilBuild = false
    localPath = if (project.hasProperty("studioRunPath")) studioRunPath else studioCompilePath
}
configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}