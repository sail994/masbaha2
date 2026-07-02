plugins {
    // 1. Plugins Multiplatform et Compose de JetBrains
    id("com.android.application")
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.google.devtools.ksp")
}

kotlin {
    // 2. Configuration de la cible Android
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17" // Mis à jour vers Java 17, standard pour KMP
            }
        }
    }
    
    // 3. Configuration des cibles iOS (iPhone physiques et simulateurs Mac)
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    // 4. Organisation des dépendances par plateforme
    sourceSets {
        // Dépendances communes (Partagées entre Android et iOS)
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3) // Votre interface Masbaha avec Material 3
            implementation(compose.ui)
            implementation(compose.components.resources) // Gestion unifiée des strings/arrays
        }
        
        // Dépendances spécifiques à Android (Room, Core-KTX, Audio)
        androidMain.dependencies {
            implementation("androidx.core:core-ktx:1.12.0")
            implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
            implementation("androidx.activity:activity-compose:1.8.2")
            
            // On conserve Room uniquement côté Android pour le moment
            val roomVersion = "2.6.1"
            implementation("androidx.room:room-runtime:$roomVersion")
            implementation("androidx.room:room-ktx:$roomVersion")
        }
        
        // Dépendances spécifiques à iOS
        iosMain.dependencies {
            // Vos futures librairies iOS natives si nécessaire
        }
    }
}

android {
    namespace = "com.example.masbaha"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.masbaha"
        minSdk = 26 // Conservé pour vos fonctionnalités audio et locales
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    // Note : Les blocs composeOptions et kotlinCompilerExtensionVersion obsolètes ont été supprimés 
    // car JetBrains Compose gère automatiquement le compilateur avec le plugin Kotlin 1.9.23.
}

// 5. Configuration du compilateur KSP pour Room (Cible Android uniquement)
dependencies {
    add("kspAndroid", "androidx.room:room-compiler:2.6.1")
}
