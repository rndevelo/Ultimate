// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        // Make sure that you have the following two repositories
        google()  // Google's Maven repository
        mavenCentral()  // Maven Central repository
    }
    dependencies {
        // Add the dependency for the Google services Gradle plugin
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.51.1")
        classpath("com.google.gms:google-services:4.4.2")
        classpath("com.android.tools.build:gradle:8.5.0")
        classpath("com.google.firebase:firebase-crashlytics-gradle:3.0.1")
    }
}

plugins {
    id("com.android.application") version "8.5.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id ("com.android.library") version "8.5.0" apply false

    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
}