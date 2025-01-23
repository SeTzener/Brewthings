package com.brewthings.app.utils

import com.karumi.kotlinsnapshot.KotlinSnapshot
import java.nio.file.Paths
import kotlin.reflect.KClass
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.junit.jupiter.api.TestInfo

inline infix fun <reified T> T.shouldMatchSnapshot(testInfo: TestInfo) {
    val className: KClass<out Any> = testInfo.testClass.get().kotlin
    val serialized: String = JsonSerializationConfig.encodeToString(serializer(), this)
    KotlinSnapshot(
        testClassAsDirectory = true,
        snapshotsFolder = pathForTest(className.qualifiedName),
    ).matchWithSnapshot(
        value = serialized,
        snapshotName = testInfo.testMethod.get().name,
    )
}

fun pathForTest(qualifiedName: String?): String {
    val dir = "/src/test/java"
    val snapshotsFolder = qualifiedName?.replace(".", "/")
    return Paths.get(dir, snapshotsFolder).parent.toString()
}

val JsonSerializationConfig: Json = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
    encodeDefaults = true
}
