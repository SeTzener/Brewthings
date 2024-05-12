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

    // Core Libraries
    implementation(libs.core.ktx)

    // Accompanist
    implementation(libs.accompanist.permissions)

    // AndroidX AppCompat
    implementation(libs.app.compat)

    // Google Material Components
    implementation(libs.material)

    // AndroidX Lifecycle
    implementation(libs.lifecycle.runtime.ktx)

    // Jetpack Compose
    implementation(libs.compose.activity)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)

    // Material
    implementation(libs.compose.material)
    implementation(libs.compose.material3)

    // Kable
    implementation(libs.kable)

    // Koin
    implementation(libs.koin)
    implementation(libs.koin.compose)

    // Unit Testing
    testImplementation(libs.junit)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
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
