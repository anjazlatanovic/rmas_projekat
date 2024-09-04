// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    //alias(libs.plugins.google.gms.google.services) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    //id ("com.google.android.gms:play-serviceslocation:21.2.0")

    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1"
}

buildscript {
    dependencies {
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
        //classpath ("com.google.gms:google-services:4.4.2")
        classpath(libs.google.services)
    }
}

