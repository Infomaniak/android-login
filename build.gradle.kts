buildscript {
    extra.apply {
        set("javaVersion", JavaVersion.VERSION_17)
    }
}

plugins {
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.jetbrains.kotlin.serialization) apply false
}

tasks.register("clean", Delete::class) { delete(rootProject.layout.buildDirectory) }
