plugins {
    alias(libs.plugins.kotlin.jvm) apply false
}

// Coordonnées Maven : JitPack publie `com.github.pobsteta.terrain:<module>:<tag>` ; le groupe
// et la version locaux servent à `publishToMavenLocal`.
allprojects {
    group = "com.github.pobsteta.terrain"
    version = providers.environmentVariable("VERSION").getOrElse("dev")
}

// Contrôle du format des tags (mission 033) : le script est appelé par la CI et testé ici.
tasks.register<Exec>("test") {
    group = "verification"
    description = "Teste scripts/verifier-tag.sh (format vAAAA.M.N des tags)."
    commandLine("bash", "scripts/verifier-tag-test.sh")
}
