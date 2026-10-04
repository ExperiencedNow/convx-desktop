import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.ksp)
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.foundation)
    implementation(compose.ui)
    implementation(compose.components.resources)
    implementation(libs.materialKolor)

    // Submodules that are already pure Kotlin/JVM
    implementation(project(":innertube"))
    implementation(project(":canvas"))
    implementation(project(":applecanvas"))
    implementation(project(":vivimusiccanvas"))
    implementation(project(":lrclib"))
    implementation(project(":betterlyrics"))
    implementation(project(":kugou"))
    implementation(project(":simpmusic"))
    implementation(project(":youlyplus"))
    implementation(project(":lastfm"))
    implementation(project(":kizzy"))

    // Ktor and Coroutines
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.json)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.10.1")

    // Audio Engine (LibVLC via vlcj)
    implementation("uk.co.caprica:vlcj:4.11.0")

    // Database & Persistence (Room KMP 2.8.4 + SQLite Bundled Driver + DataStore)
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation("androidx.sqlite:sqlite-bundled:2.5.0-alpha11")
    implementation(libs.datastore)

    // QuickJS for Desktop JVM (Spike S6)
    implementation("io.github.dokar3:quickjs-kt:1.0.5")

    testImplementation(libs.junit)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1")
}

tasks.test {
    testLogging {
        showStandardStreams = true
        events("passed", "skipped", "failed")
    }
}

compose.desktop {
    application {
        mainClass = "com.convx.desktop.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Exe, TargetFormat.Msi)
            packageName = "Convx"
            packageVersion = "1.5.3"
            description = "YouTube Music desktop client with iOS-like liquid glass aesthetic"
            copyright = "© 2026 Convx contributors. GPL-3.0 License."
            windows {
                menuGroup = "Convx"
                shortcut = true
                dirChooser = true
            }
        }
    }
}
