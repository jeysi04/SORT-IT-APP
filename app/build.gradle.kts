plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    id ("kotlin-parcelize")
}


android {
    namespace = "com.example.sort_it_json"
    compileSdk = 36 // hChanged from 36 to stable 35


    defaultConfig {
        applicationId = "com.example.sort_it_json"
        minSdk = 24
        targetSdk = 36 // Changed from 36 to stable 35
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }


    //Renaming apk file
    applicationVariants.all {
        outputs.all {
            val outputImpl = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            val appName = "Sort-It"
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

        implementation ("androidx.camera:camera-camera2:1.3.0")
        implementation ("androidx.camera:camera-lifecycle:1.3.0")
        implementation ("androidx.camera:camera-view:1.3.0")

        implementation("org.tensorflow:tensorflow-lite:2.14.0")

        // CameraX core library
        val camerax_version = "1.3.0" // latest stable version
        implementation ("androidx.camera:camera-core:$camerax_version")
        implementation ("androidx.camera:camera-camera2:$camerax_version")

        // CameraX Lifecycle library (binds to lifecycle)
        implementation ("androidx.camera:camera-lifecycle:$camerax_version")

        // CameraX View library (for PreviewView)
        implementation ("androidx.camera:camera-view:$camerax_version")

        // Optional: CameraX Extensions (HDR, Night Mode)
        implementation ("androidx.camera:camera-extensions:$camerax_version")

        // Retrofit
        implementation("com.squareup.retrofit2:retrofit:2.9.0")
        implementation("com.squareup.retrofit2:converter-gson:2.9.0")

        // OkHttp logging interceptor
        implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.11")

        // Coroutines
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-auth")

    // For integration
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

}