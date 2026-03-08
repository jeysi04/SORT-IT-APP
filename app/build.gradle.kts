plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.sort_it_json"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.sort_it_json"
        minSdk = 24
        targetSdk = 36
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
    kotlinOptions {
        jvmTarget = "11"
    }

    //Renaming apk file
    applicationVariants.all {
        outputs.all {
            // Cast required to access outputFileName property
            val outputImpl = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl

            val appName = "Sort-It" // <-- NAME

            outputImpl.outputFileName = "${appName}.apk"
        }
    }
}

dependencies {
    // Gson for JSON parsing
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("androidx.core:core-splashscreen:1.0.0")
    implementation("com.tbuonomo:dotsindicator:5.1.0")
    implementation("androidx.webkit:webkit:1.8.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


}