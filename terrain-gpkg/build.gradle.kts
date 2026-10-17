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

dependencies {
    api(project(":terrain-gnss"))
    testImplementation(kotlin("test"))
    // Pilote SQLite JDBC : le GeoPackage se lit en test comme sur l'appareil (android.database.sqlite).
    testImplementation("org.xerial:sqlite-jdbc:3.53.4.0")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            pom {
                name.set("terrain-gpkg")
                description.set("Couches vecteur GeoPackage (lecture, écriture, reprojection) pour les applications de terrain Néméton")
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
