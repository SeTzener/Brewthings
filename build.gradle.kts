// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    application
    kotlin("jvm") version libs.versions.kotlin
    alias(libs.plugins.android) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.spotless) apply false
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
