@file:Suppress("UnstableApiUsage")

import java.util.Properties


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.androidx.navigation.safe.args)
}

android {
    namespace = "com.billion47.task"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.billion47.task"
        minSdk = 24
        targetSdk = 36
    }



    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
        buildConfig = true
    }
}


dependencies {

    // AndroidX Core Libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.multidex)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.gson)

    // Material Design
    implementation(libs.material)

    implementation(libs.play.services.location)

    implementation(libs.converter.gson)

    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    // Networking: Retrofit + Moshi
    implementation(libs.retrofit)
    implementation(libs.moshi)
    implementation(libs.androidx.runtime.saved.instance.state)

    ksp(libs.moshi.kotlin.codegen)
    implementation(libs.converter.moshi)

    // Networking: OkHttp BOM
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // Debugging Tools

    implementation(libs.androidx.work.runtime.ktx)

    implementation ("androidx.media3:media3-exoplayer:1.4.1")
    implementation ("androidx.media3:media3-ui:1.4.1")

    // Utilities
    implementation(libs.utilcodex)
    implementation(libs.timber)
    implementation(libs.fragmentviewbindingdelegate.kt)
    implementation(libs.glide)
    implementation(libs.universal.adapter)



    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.app.update.ktx)

    implementation(libs.permission.flow.android)

    // Hilt DI + WorkManager
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.work)
    ksp(libs.hilt.android.compiler)
    ksp(libs.androidx.hilt.compiler)


}