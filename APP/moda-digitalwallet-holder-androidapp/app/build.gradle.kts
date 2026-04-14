import com.android.build.gradle.internal.dsl.SigningConfig
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.hiltAndroidGradle)
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("org.cyclonedx.bom") version "2.3.1"

}

android {
    namespace = "tw.gov.moda.diw"
    compileSdk = 35
    defaultConfig {
        applicationId = "tw.gov.moda.diw"
        minSdk = 28
        targetSdk = 35
        versionCode = 32
        versionName = "1.0.0+20251117_01"

        // 使用新的 archivesName 屬性取代已棄用的 archivesBaseName
        base.archivesName.set("moda-" + defaultConfig.versionName)
    }

    android.buildFeatures.buildConfig = true
    flavorDimensions.add("moda")

    productFlavors {
        create("prod") {
            // 數發部
            dimension = "moda"
            buildConfigField("String", "DID_ISSUER_URL", "\"https://frontend.wallet.gov.tw\"")
            buildConfigField("String", "CITIZEN_DIGITAL_URL", "\"https://issuer-moica.wallet.gov.tw\"")
            buildConfigField("String", "OFFICIAL_IMAGE_URL", "\"https://www.wallet.gov.tw\"")
            resValue ("string", "app_name", "數位憑證皮夾")
            manifestPlaceholders["APPLINK_HOST"] = "frontend.wallet.gov.tw"
        }
    }


    buildTypes {
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            isShrinkResources = false
        }
        release {
            isMinifyEnabled = false
            isDebuggable = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }



    androidComponents.beforeVariants {
        it.androidTest.enable = false
    }

    testOptions{
        unitTests.all {
            it.enabled = false
        }
    }
}


dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    implementation(libs.androidx.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.dagger.hilt.android)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.security.crypto)
    implementation(libs.gson)
    implementation(libs.zxing.android.embedded)
    implementation(libs.zxing.core)
    implementation(libs.biometric)
    implementation(libs.play.services.basement)
    implementation(libs.play.services.base)
    implementation(libs.mlkit.barcode.scanning)
    implementation(libs.lottie)

    kapt(libs.dagger.hilt.compiler)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.sqlcipher)
    implementation(libs.sqlite)
    kapt(libs.room.compiler)
    debugImplementation("com.example.did_sdk_module:flutter_debug:1.0")
    releaseImplementation("com.example.did_sdk_module:flutter_release:1.0")
    configurations.all {
        resolutionStrategy {
            force("com.google.protobuf:protobuf-java:3.25.5")
            force("commons-io:commons-io:2.17.0")
            force("com.google.guava:guava:33.3.1-jre")
            force("io.netty:netty-codec-http2:4.1.114.Final")
            force("io.netty:netty-codec-http:4.1.114.Final")
            force("io.netty:netty-handler:4.1.114.Final")
        }
    }
}