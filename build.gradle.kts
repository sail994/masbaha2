plugins {
    // 1. Plugin de base pour l'application Android
    id("com.android.application") version "8.3.2" apply false
    id("com.android.library") version "8.3.2" apply false
    
    // 2. IMPORTANT : Remplacement du plugin Android pur par le plugin Multiplatform (KMP)
    id("org.jetbrains.kotlin.multiplatform") version "1.9.23" apply false
    
    // 3. Plugin Compose Multiplatform (Interface partagée Android/iOS)
    id("org.jetbrains.compose") version "1.6.11" apply false
    
    // 4. Plugin KSP (Mis à jour et aligné sur la version Kotlin 1.9.23 pour votre base de données Room)
    id("com.google.devtools.ksp") version "1.9.23-1.0.20" apply false
}
