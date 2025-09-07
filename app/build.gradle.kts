plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.joao.plantdoctor"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.joao.plantdoctor"
        minSdk = 21
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // URL de produção (Render) - NÃO MUDE ESTA
            buildConfigField("String", "BASE_URL", "\"https://plantdoctor-backend.onrender.com/\"")
        }
        getByName("debug") {
            // ✅ CORRIGIDO: Agora aponta para o IP do seu computador na rede local.
            buildConfigField("String", "BASE_URL", "\"https://plantdoctor-backend.onrender.com/\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // Dependências principais - Versões revertidas para estáveis
    implementation(libs.androidx.core.ktx)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3") // Versão estável
    implementation("androidx.appcompat:appcompat:1.7.0") // Versão estável

    // Dependências do sistema de Views tradicional - Versões revertidas para estáveis
    implementation("com.google.android.material:material:1.12.0") // Versão estável
    implementation("androidx.constraintlayout:constraintlayout:2.1.4") // Versão estável

    // Testes
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Retrofit para chamadas de rede - Versões revertidas para as mais comuns e estáveis
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0") // Versão estável compatível

    // Componentes de Arquitetura (ViewModel, LiveData, etc.) - Versões revertidas para estáveis
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.3")
    implementation("androidx.activity:activity-ktx:1.9.0")

    // Coroutines para programação assíncrona - Versão estável comum
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}

