pluginManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    // Permet d'éviter le blocage "prefer settings over project" causé par l'importation de Ivy
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "masbaha"
include(":composeApp")
