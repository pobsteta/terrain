plugins {
    alias(libs.plugins.kotlin.jvm) apply false
}

// Coordonnées Maven : JitPack publie `com.github.pobsteta.terrain:<module>:<tag>` ; le groupe
// et la version locaux servent à `publishToMavenLocal`.
allprojects {
    group = "com.github.pobsteta.terrain"
    version = providers.environmentVariable("VERSION").getOrElse("dev")
}
