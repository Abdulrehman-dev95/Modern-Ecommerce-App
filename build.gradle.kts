buildscript {
    val navVersion = "2.9.3" // Define navVersion at the top level for better visibility
    dependencies {
        classpath ("com.google.dagger:hilt-android-gradle-plugin:2.57")

        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:${navVersion}") // Use string template
    }
}


// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}