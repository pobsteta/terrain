import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-library`
    `maven-publish`
}

// Bytecode JVM 17 : consommable par les modules Android des applications.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

// Génération des jetons (ADR 0001) : sources MCU recopiées et script, dans un source set à
// part. Rien n'en entre dans le jar publié, qui ne dépend pas de MCU.
val generation: SourceSet by sourceSets.creating {
    compileClasspath += sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().output
}
val testGeneration: SourceSet by sourceSets.creating {
    compileClasspath += generation.output + sourceSets.main.get().output
    runtimeClasspath += generation.output + sourceSets.main.get().output
}
kotlin.sourceSets.named("generation") {
    kotlin.srcDirs("generation/mcu", "generation/src")
}
kotlin.sourceSets.named("testGeneration") {
    kotlin.srcDirs("generation/test")
}

dependencies {
    testImplementation(kotlin("test"))
    "testGenerationImplementation"(kotlin("test-junit5"))
}

tasks.test {
    useJUnitPlatform()
    // Les lignes MARGE des tests de contraste sortent dans la console.
    testLogging { showStandardStreams = true }
}

// Rejoue la génération : réécrit StyleTerrain.kt, la table des jetons et le nuancier. La CI
// vérifie ensuite qu'aucun fichier n'a changé (`git diff --exit-code`).
tasks.register<JavaExec>("generer") {
    group = "generation"
    description = "Génère les schémas clair et sombre par Material Color Utilities (ADR 0001)"
    classpath = generation.runtimeClasspath
    mainClass.set("fr.nemeton.terrain.style.generation.GenererKt")
    args(projectDir.absolutePath)
}

tasks.register<Test>("testGeneration") {
    group = "verification"
    description = "Tests du script de génération"
    testClassesDirs = testGeneration.output.classesDirs
    classpath = testGeneration.runtimeClasspath
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            pom {
                name.set("terrain-style")
                description.set("Jetons de couleur et contraste WCAG 2.2 pour les applications de terrain Néméton")
                url.set("https://github.com/pobsteta/terrain")
                licenses {
                    license {
                        name.set("GPL-3.0-or-later")
                        url.set("https://www.gnu.org/licenses/gpl-3.0.html")
                    }
                }
            }
        }
    }
}
