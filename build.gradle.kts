// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    application
    kotlin("jvm") version libs.versions.kotlin
    alias(libs.plugins.gradle.android) apply false
    alias(libs.plugins.gradle.kotlin) apply false
    alias(libs.plugins.gradle.room) apply false
    alias(libs.plugins.gradle.ksp) apply false
    alias(libs.plugins.gradle.serialization) apply false
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
