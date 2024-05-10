plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val appId = stringProperty("app.id")
val semanticVersioning = stringProperty("app.versionName")
val buildVersion = intProperty("app.buildVersion")
val target = intProperty("app.targetSdk")

android {
    namespace = appId
    compileSdk = target

    defaultConfig {
        applicationId = appId
        minSdk = intProperty("app.minSdk")
        targetSdk = target
        versionCode = generateVersionCode(semanticVersioning, buildVersion)
        versionName = semanticVersioning

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    // Jetpack Compose
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")

    // Material
    implementation("androidx.compose.material:material:1.5.4")
    implementation("androidx.compose.material3:material3-android:1.2.0-alpha12")


    testImplementation("junit:junit:4.13.2")
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
