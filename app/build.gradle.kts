import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android") version "1.9.22"
}

// 1. local.properties 파일 불러오기 (Kotlin DSL 방식)
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
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

        // 2. BuildConfig에 변수 추가 (Kotlin DSL 방식)
        // local.properties에서 값을 읽어와서 저장합니다. 값이 없으면 빈 문자열("")을 넣습니다.
        val geminiKey = localProperties.getProperty("gemini.api.key") ?: ""
        val exchangeKey = localProperties.getProperty("exchange.api.key") ?: ""

        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiKey\"")
        buildConfigField("String", "EXCHANGE_API_KEY", "\"$exchangeKey\"")
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true // BuildConfig 기능 활성화
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
}

dependencies {
    // 기본 Android 라이브러리
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // [추가 1] Retrofit2 & Gson (환율 API 통신 및 JSON 파싱용)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // [추가 2] Google Gemini AI SDK (최신 버전 0.9.0)
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    // [추가 3] Coroutines & Lifecycle (비동기 작업 및 데이터 흐름 관리)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // 테스트 라이브러리
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
