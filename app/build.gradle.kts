

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize") // <-- E SUBSTITUÍDA POR ESTA
    id ("kotlin-kapt")// <- ESSENCIAL para o Glide
}

android {
    namespace = "com.joao.PlantSoS"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.joao.plantsos"
        minSdk = 26
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
    // Dependências principais
    implementation(libs.androidx.core.ktx)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
    implementation("androidx.appcompat:appcompat:1.7.0")

    // Dependências do sistema de Views
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0") // Corrigido: Apenas uma linha
    implementation("androidx.fragment:fragment-ktx:1.7.1")
    implementation("androidx.activity:activity-ktx:1.9.0")

    // Imagens - Escolhendo apenas Coil por ser mais moderno
    implementation("io.coil-kt:coil:2.5.0")

    // Testes
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Retrofit para chamadas de rede
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Componentes de Arquitetura (ViewModel)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.3") // Corrigido: Apenas uma linha

    // Coroutines para programação assíncrona
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    //construcao da IA
    implementation("org.tensorflow:tensorflow-lite:2.15.0")
    // Core TFLite runtime
    implementation("org.tensorflow:tensorflow-lite-support:0.4.4")

    implementation("org.tensorflow:tensorflow-lite-task-vision:0.4.3")
    // Ajuda a carregar o modelo e processar resultados

}