plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.monprojettictactoe"
    // SDK 36 is Android 16 (Baklava). Ensure you have installed this SDK via SDK Manager.
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.monprojettictactoe"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

// REMOVED: The resolutionStrategy block was deleted.
// It was forcing appcompat 1.6.1 while 'libs.appcompat' was pulling 1.7.1,
// causing the "Duplicate key" resource error.
configurations.all {    resolutionStrategy {
    force("androidx.appcompat:appcompat:1.6.1")
}
}
