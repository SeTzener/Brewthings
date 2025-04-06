import java.util.Properties

plugins {
    alias(libs.plugins.android)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.google.services)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.serialization)
    alias(libs.plugins.spotless)
    alias(libs.plugins.junit5.android)
}

val appId = stringProperty("app.id")
val semanticVersioning = stringProperty("app.versionName")
val buildVersion = intProperty("app.buildVersion")
val target = intProperty("app.targetSdk")

spotless {
    kotlin {
        target("**/*.kt")
        ktlint().editorConfigOverride(
            mapOf(
                "indent_style" to "space",
                "indent_size" to "4",
                "ktlint_standard_comment-wrapping" to "disabled",
            ),
        )
    }

    kotlinGradle {
        target("*.gradle.kts")
        ktlint()
    }

    // Add configurations for other languages as needed
}

android {
    namespace = appId
    compileSdk = target

    signingConfigs {
        create("release") {
            try {
                val secrets = readProperties(file("../secrets.properties"))
                storeFile = file(secrets.getString("signing.release.storeFile"))
                storePassword = secrets.getString("signing.release.storePassword")
                keyAlias = secrets.getString("signing.release.keyAlias")
                keyPassword = secrets.getString("signing.release.keyPassword")
            } catch (e: Exception) {
                println("Warning: Could not read signing properties from secrets.properties file.")
                e.printStackTrace()
            }
        }
        getByName("debug") {
            storeFile = file(stringProperty("signing.debug.storeFile"))
            storePassword = stringProperty("signing.debug.storePassword")
            keyAlias = stringProperty("signing.debug.keyAlias")
            keyPassword = stringProperty("signing.debug.keyPassword")
        }
    }

    defaultConfig {
        applicationId = appId
        minSdk = intProperty("app.minSdk")
        targetSdk = target
        versionCode = generateVersionCode(semanticVersioning, buildVersion)
        versionName = semanticVersioning
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["runnerBuilder"] = "de.mannodermaus.junit5.AndroidJUnit5Builder"
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
        debug {
            signingConfig = signingConfigs.getByName("debug")
            applicationIdSuffix = stringProperty("app.debug.applicationIdSuffix")
            versionNameSuffix = stringProperty("app.debug.versionNameSuffix")
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    // Core Libraries
    implementation(libs.core.ktx)

    // Accompanist
    implementation(libs.accompanist.permissions)
    implementation(libs.accompanist.systemuicontroller)

    // AndroidX AppCompat
    implementation(libs.androidx.appcompat)

    // AndroidX Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // AndroidX DataStore
    implementation(libs.androidx.datastore.preferences)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    // Jetpack Compose
    implementation(libs.compose.activity)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)

    // ConstraintLayout
    implementation(libs.compose.constraintLayout)

    // Navigation Compose
    implementation(libs.navigation.ui)
    implementation(libs.navigation.compose)
    implementation(libs.navigation.fragment)

    // Material
    implementation(libs.compose.material3)

    // Kable
    implementation(libs.kable)

    // Koin
    implementation(libs.koin)
    implementation(libs.koin.compose)

    // KotlinX
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)

    // MPAndroidChart
    implementation(libs.mpandroidchart)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    annotationProcessor(libs.room.compiler)
    ksp(libs.room.compiler)

    // Unit Testing
    testImplementation(libs.junit5.jupiter.api)
    testImplementation(libs.junit5.jupiter.engine)
    testImplementation(libs.junit5.jupiter.params)
    testImplementation(libs.kotest.junit5)
    testImplementation(libs.mockk)
    testImplementation(libs.mockk.dsl)
    testImplementation(libs.karumi)
    testImplementation(libs.serialization.moshi)
    testImplementation(libs.serialization.moshi.kotlin)

    // Instrumented Testing
    androidTestImplementation(libs.junit5.jupiter.api)
    androidTestRuntimeOnly(libs.junit5.jupiter.engine)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.runner)
}
tasks.withType<Test> {
    useJUnitPlatform()
}

fun generateVersionCode(versionName: String, buildVersion: Int): Int {
    val versionParts = versionName.split('.')
    val major = versionParts.getOrNull(0)?.toIntOrNull() ?: 0
    val minor = versionParts.getOrNull(1)?.toIntOrNull() ?: 0
    val patch = versionParts.getOrNull(2)?.toIntOrNull() ?: 0

    return major * 1000000 + minor * 10000 + patch * 100 + buildVersion
}

fun stringProperty(name: String) = properties[name] as String
fun intProperty(name: String) = stringProperty(name).toInt()

fun readProperties(propertiesFile: File): Properties = Properties().apply {
    propertiesFile.inputStream().use { fis ->
        load(fis)
    }
}

fun Properties.getString(name: String) = this[name] as String
