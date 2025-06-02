plugins {
    id("com.android.library")
    kotlin("android")
    id("maven-publish")
    id("kotlin-parcelize")
    kotlin("plugin.serialization")
}

group = "com.github.Infomaniak"

android {

    namespace = "com.infomaniak.lib.login"

    compileSdk = 35

    defaultConfig {
        minSdk = 24
        targetSdk = 35

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }

    compileOptions {
        sourceCompatibility = rootProject.extra["java_version"] as JavaVersion
        targetCompatibility = rootProject.extra["java_version"] as JavaVersion
    }

    kotlinOptions {
        jvmTarget = (rootProject.extra["java_version"] as JavaVersion).toString()
    }

    buildFeatures { viewBinding = true }

}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.8.1")

    implementation("androidx.browser:browser:1.8.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.lifecycle:lifecycle-service:2.9.0")

    val splittiesVersion = "3.0.0"
    implementation("com.louiscad.splitties:splitties-mainthread:$splittiesVersion")

    implementation("com.google.code.gson:gson:2.12.1")
    implementation("com.google.android.material:material:1.12.0")

    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.infomaniak"
                artifactId = "android-login"
                version = "3.1.4"
            }
        }
    }
}
