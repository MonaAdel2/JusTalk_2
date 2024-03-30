plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id ("androidx.navigation.safeargs.kotlin")
//    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.justalk_2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.justalk_2"
        minSdk = 24
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
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // navigation graph
    implementation ("androidx.navigation:navigation-fragment-ktx:2.6.0")
    implementation ("androidx.navigation:navigation-ui-ktx:2.6.0")

    // navigation bar
//    implementation ("com.etebarian:meow-bottom-navigation:1.2.0")

    // for notification
    implementation ("com.squareup.retrofit2:retrofit:2.6.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.6.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.5.0")

    // rounded image
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    // naviagtion bar
    implementation ("com.etebarian:meow-bottom-navigation:1.2.0")

    // image loader
    implementation ("com.github.bumptech.glide:glide:4.14.2")

    //  Firebase
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation(platform("com.google.firebase:firebase-bom:32.2.2"))
    implementation("com.google.firebase:firebase-auth-ktx:22.1.1")
    implementation("com.google.firebase:firebase-firestore-ktx:24.7.0")
    implementation("com.google.firebase:firebase-storage-ktx:20.2.1")

    implementation ("com.google.firebase:firebase-messaging:23.1.2")
    implementation ("com.google.firebase:firebase-installations:17.1.3")

    // splash screen
    implementation("androidx.core:core-splashscreen:1.0.0")

    // image cropper
    implementation ("androidx.activity:activity-ktx:1.2.0-alpha06")
    api ("com.theartofdev.edmodo:android-image-cropper:2.8.0")

    // image compressor
    implementation("com.github.Shouheng88:compressor:1.6.0")

}