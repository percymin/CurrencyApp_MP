plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android") version "1.9.24"
}

android {
    namespace = "com.cookandroid.currencytest"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.cookandroid.currencytest"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Gemini API 키를 안전하게 관리하기 위한 설정 (선택 사항이나 권장됨)
        // buildConfigField("String", "GEMINI_API_KEY", "\"YOUR_API_KEY_HERE\"")
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
        buildConfig = true // BuildConfig 기능 활성화 (API 키 관리에 유용)
    }
}

dependencies {

    // 기본 Android 라이브러리
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // [추가 1] Retrofit2 & Gson (환율 API 통신 및 JSON 파싱용)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // [추가 2] Google Gemini AI SDK (AI 채팅 및 분석용)
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    // [추가 3] Coroutines & Lifecycle (비동기 작업 및 데이터 흐름 관리)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    // 테스트 라이브러리
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
}