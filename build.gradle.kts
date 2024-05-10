// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    application
    kotlin("jvm") version libs.versions.kotlin
    id("com.android.application") version libs.versions.androidGradle apply false
    id("org.jetbrains.kotlin.android") version libs.versions.kotlinGradle apply false
}

buildscript{
    dependencies {
        classpath(libs.android.application)
        classpath(libs.kotlin.android)
        classpath(libs.toml4j)
    }
    repositories{
        google()
        mavenCentral()
    }
}