/*
 * Politique de versionnement :
 *
 * Format stable :
 *   YYYY.WEEK
 *
 * Format beta / build de test :
 *   YYYY.WEEK.bBUILD
 *
 * Detail :
 *   YYYY  = année ISO de publication
 *   WEEK  = semaine ISO de l'année
 *   BUILD = numéro du build GitHub Actions, ou "local" si le build est lancé hors CI
 *
 * Mise a jour de YYYY.WEEK :
 *   Modifier versionYear et versionWeek en haut de ce fichier.
 *   Site utile pour trouver le numero de semaine ISO :
 *     https://weeknumber.com/
 *
 * Version stable :
 *   Une version stable ne contient pas le numéro de build.
 *
 *   Commande :
 *     ./gradlew clean build -Prelease=true
 *
 *   Résultat, par exemple pendant la semaine 25 de 2026 :
 *     2026.25
 *
 * Version beta locale :
 *   Sans -Prelease=true, le build est considéré comme une beta.
 *   Si aucun numéro de build CI n'est disponible, BUILD vaut "local".
 *
 *   Commande :
 *     ./gradlew clean build
 *
 *   Résultat, par exemple pendant la semaine 25 de 2026 :
 *     2026.25.devbuild-local
 *
 * Version beta en CI GitHub Actions :
 *   GitHub fournit un numéro de run via GITHUB_RUN_NUMBER.
 *   Le workflow le transmet aussi sous BUILD_NUMBER pour garder un nom générique.
 *
 *   Commande exécutée par la CI avec BUILD_NUMBER=247 :
 *     ./gradlew clean build
 *
 *   Résultat :
 *     2026.25.devbuild-247
 *
 * Version forcée optionnelle :
 *   Utilisée surtout par le workflow de release GitHub.
 *   Cela permet de construire un JAR stable dont la version correspond exactement au tag Git.
 *
 *   Commande :
 *     ./gradlew clean build -Prelease=true -PversionOverride=2026.25
 *
 *   Résultat :
 *     2026.25
 */

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
    java
    id("com.gradleup.shadow") version "9.4.2"
}

/*
 * Version de base a mettre a jour manuellement.
 * Numero de semaine ISO : https://weeknumber.com/
 */

val versionYear = 2026
val versionWeek = 25

val isRelease = providers
    .gradleProperty("release")
    .map { it.toBooleanStrictOrNull() ?: false }
    .orElse(false)
    .get()

val versionOverride = providers
    .gradleProperty("versionOverride")
    .orElse("")
    .get()
    .removePrefix("v")

val buildNumber = providers
    .gradleProperty("buildNumber")
    .orElse(providers.environmentVariable("BUILD_NUMBER"))
    .orElse(providers.environmentVariable("GITHUB_RUN_NUMBER"))
    .orElse("local")
    .get()

val baseVersion = "$versionYear.$versionWeek"
val computedVersion = when {
    versionOverride.isNotBlank() -> versionOverride
    isRelease -> baseVersion
    else -> "$baseVersion.devbuild-$buildNumber"
}

abstract class PrintVersionTask : DefaultTask() {
    @get:Input
    abstract val versionToPrint: Property<String>

    @TaskAction
    fun printVersion() {
        println(versionToPrint.get())
    }
}

group = "fr.heavencube.actionsstrator"
version = computedVersion

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.72-stable")
}

tasks {
    jar {
        enabled = false
    }

    shadowJar {
        archiveFileName.set("${rootProject.name}-${rootProject.version}.jar")
    }

    processResources {
        val expandProps = mapOf(
            "version" to project.version.toString()
        )

        inputs.properties(expandProps)

        filesMatching("paper-plugin.yml") {
            expand(expandProps)
        }
    }

    register<PrintVersionTask>("printVersion") {
        group = "versioning"
        description = "Affiche la version calculée du projet."
        versionToPrint.set(computedVersion)
    }

    build {
        dependsOn(shadowJar)
    }
}