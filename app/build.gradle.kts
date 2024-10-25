plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.tdc.vlxdonline"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.tdc.vlxdonline"
        minSdk = 31
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true;
    }
}

dependencies {

    implementation(libs.firebase.database.ktx)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

//    implementation(libs.android.mail)
//    implementation(libs.android.activation)
    implementation(libs.glide)
    annotationProcessor(libs.compiler)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
apply(plugin = "com.google.gms.google-services")