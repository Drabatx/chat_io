plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("kapt")
    alias(libs.plugins.daggerHilt)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    alias(libs.plugins.googleGmsGoogleServices)
}

android {
    namespace = "com.drabatx.chatio"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.drabatx.chatio"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material.core)
    implementation(libs.androidx.compose.material)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.material.icons)


    //dagger
    implementation(libs.dagger.hilt)
    kapt (libs.dagger.compiler)

    //Retorfit
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson.converter)
    implementation(libs.square.interceptor)

    //Corutinas
    implementation(libs.kotlin.coroutines)
    //
    implementation(libs.glide)
    implementation(libs.androidx.viewmodel.compose)
    implementation(libs.coil.compose)
    implementation(libs.constraintlayout)
    implementation(libs.paging.compose)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.lottie.compose)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.5.1")

}