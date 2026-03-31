// Top-level build file
plugins {
   alias(libs.plugins.android.application) apply false
   alias(libs.plugins.kotlin.android) apply false // Keep this!


   // Keep your Google Services plugin
   id("com.google.gms.google-services") version "4.4.2" apply false 
}
