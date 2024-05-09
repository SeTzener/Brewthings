// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    application
    kotlin("jvm") version "1.9.20"
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
}

buildscript{
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.9.20"))
        classpath("com.android.tools.build:gradle:8.2.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")
    }
    repositories{
        google()
        mavenCentral()
    }
}