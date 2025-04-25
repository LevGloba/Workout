// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false

    //kps + Hilt and Dagger
    id("com.google.dagger.hilt.android") version "2.56.1" apply false
    id("com.google.devtools.ksp") version "2.1.10-1.0.29" apply false
}
buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath(libs.androidx.navigation.safe.args.gradle.plugin)
    }
}