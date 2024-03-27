plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.bibi"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.bibi"
        minSdk = 24
        targetSdk = 33
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
        mlModelBinding = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-firestore:24.10.3")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("org.tensorflow:tensorflow-lite-metadata:0.1.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation(platform("com.google.firebase:firebase-bom:32.7.3"))
    implementation ("androidx.fragment:fragment:1.4.0")

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth")
    // thư viện lotte
    implementation ("com.airbnb.android:lottie:6.3.0")
    // thư viện số điện thoạt quốc gia
    implementation ("com.hbb20:ccp:2.5.0")

    //rounded image view
    implementation ("com.makeramen:roundedimageview:2.3.0")
    //glide
    implementation ("com.github.bumptech.glide:glide:4.16.0")


    implementation ("com.facebook.shimmer:shimmer:0.5.0")

    // load lại màn hình
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    // thư viện hiện thị đọc thêm
    implementation ("com.theartofdev.edmodo:android-image-cropper:2.8.0")

    implementation ("com.firebaseui:firebase-ui-firestore:8.0.2")

    implementation ("com.google.firebase:firebase-ml-vision:24.0.3")
    implementation ("com.google.firebase:firebase-ml-vision-image-label-model:20.0.1")

    // opencv with contributions
    implementation ("com.quickbirdstudios:opencv-contrib:4.5.2")

    // Import tflite dependencies
    implementation ("org.tensorflow:tensorflow-lite:0.0.0-nightly-SNAPSHOT")
    // The GPU delegate library is optional. Depend on it as needed.
    implementation ("org.tensorflow:tensorflow-lite-gpu:0.0.0-nightly-SNAPSHOT")
    implementation ("org.tensorflow:tensorflow-lite-support:0.0.0-nightly-SNAPSHOT")

    implementation ("com.algolia:algoliasearch-core:3.16.5")
    implementation ("com.algolia:algoliasearch-java-net:3.16.5")
    //implementation ("com.algolia:algoliasearch-android:3.27.0")
    implementation ("com.github.chrisbanes:PhotoView:2.3.0")
}