pluginManagement {
    repositories {
        // Miroir Google de Maven Central d'abord : Maven Central limite (HTTP 429) les machines
        // partagées de JitPack.
        maven("https://maven-central.storage-download.googleapis.com/maven2/")
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // Miroir Google de Maven Central d'abord : Maven Central limite (HTTP 429) les machines
        // partagées de JitPack.
        maven("https://maven-central.storage-download.googleapis.com/maven2/")
        mavenCentral()
    }
}

rootProject.name = "terrain"

include(":terrain-gnss")
include(":terrain-journal")
include(":terrain-gpkg")
