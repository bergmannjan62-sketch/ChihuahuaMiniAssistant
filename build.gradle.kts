plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "de.merios.chihuahua"
    compileSdk = 35

    defaultConfig {
        applicationId = "de.merios.chihuahua"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }
}
