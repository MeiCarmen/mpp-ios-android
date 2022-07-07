import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

val kotlinx_serialization_version = "0.20.0"
val kotlinx_coroutines_version = "1.3.3"
val ktor_version = "1.3.2"
val klock_version = "1.10.0"

kotlin {
    //select iOS target platform depending on the Xcode environment variables
    val iOSTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget =
        if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true)
            ::iosArm64
        else
            ::iosX64

    iOSTarget("ios") {
        binaries {
            framework {
                baseName = "SharedCode"
            }
        }
    }

    jvm("android")

    sourceSets["commonMain"].dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-common")

        implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:${kotlinx_serialization_version}")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:${kotlinx_coroutines_version}")

        implementation("io.ktor:ktor-client-core:${ktor_version}")
        implementation("io.ktor:ktor-client-json:${ktor_version}")
        implementation("io.ktor:ktor-client-logging:${ktor_version}")
        implementation("io.ktor:ktor-client-serialization:${ktor_version}")
        implementation("com.soywiz.korlibs.klock:klock:$klock_version")
        implementation(kotlin("test-common"))
        implementation(kotlin("test-annotations-common"))
    }

    sourceSets["androidMain"].dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib")

        implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:${kotlinx_serialization_version}")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${kotlinx_coroutines_version}")

        implementation("io.ktor:ktor-client-android:${ktor_version}")
        implementation("io.ktor:ktor-client-core-jvm:${ktor_version}")
        implementation("io.ktor:ktor-client-json-jvm:${ktor_version}")
        implementation("io.ktor:ktor-client-logging-jvm:${ktor_version}")
        implementation("io.ktor:ktor-client-serialization-jvm:${ktor_version}")
        implementation(kotlin("test-junit"))
        implementation("junit:junit:4.13.2")
    }

    sourceSets["iosMain"].dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-native:${kotlinx_coroutines_version}")

        implementation("io.ktor:ktor-client-ios:${ktor_version}")
        implementation("io.ktor:ktor-client-json-native:${ktor_version}")
        implementation("io.ktor:ktor-client-logging-native:${ktor_version}")
        implementation("io.ktor:ktor-client-serialization-native:${ktor_version}")
    }
}


val packForXcode by tasks.creating(Sync::class) {
    group = "build"

    //selecting the right configuration for the iOS framework depending on the Xcode environment variables
    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
    val framework = kotlin.targets.getByName<KotlinNativeTarget>("ios").binaries.getFramework(mode)

    inputs.property("mode", mode)
    dependsOn(framework.linkTask)

    val targetDir = File(buildDir, "xcode-frameworks")
    from({ framework.outputDirectory })
    into(targetDir)

    doLast {
        val gradlew = File(targetDir, "gradlew")
        gradlew.writeText("#!/bin/bash\nexport 'JAVA_HOME=${System.getProperty("java.home")}'\ncd '${rootProject.rootDir}'\n./gradlew \$@\n")
        gradlew.setExecutable(true)
    }
}

tasks.getByName("build").dependsOn(packForXcode)
