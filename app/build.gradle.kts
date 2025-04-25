plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    //Parcelize
    id("kotlin-parcelize")

    //kps + Hilt and Dagger
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")

    //safeArgs
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.example.training"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.training"
        minSdk = 26
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
    //Включаем привязку представления
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.fragment.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //ExoPLayer
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    //Jetpack Navigation
    // Views/Fragments integration
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    //kps + Hilt and Dagger
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    //Coroutine
    implementation(libs.kotlinx.coroutines.android)
}