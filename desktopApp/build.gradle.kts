import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
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
    implementation(project(":canvas"))
    implementation(project(":applecanvas"))
    implementation(project(":vivimusiccanvas"))

    // Ktor and Coroutines
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.json)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.10.1")
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
