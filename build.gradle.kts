plugins {
    kotlin("jvm") version "2.4.0-Beta2"
}

kotlin {
    jvmToolchain(25)
}

// Change to true when releasing
val release = false
val majorVersion = "0.0.1"
val minorVersion = if (release) "stable" else "beta-" + (System.getenv("BUILD_NUMBER") ?: "localbuild")
version = "$majorVersion-$minorVersion"

group = "fr.heavencube.actionsstrator"

repositories {
  maven {
    name = "papermc"
    url = uri("https://repo.papermc.io/repository/maven-public/")
  }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.63-stable")
}

tasks {
    jar {
        archiveFileName.set("${rootProject.name}-v${rootProject.version}.jar")
    }
    processResources {
        val pluginVersion = rootProject.version.toString()
        inputs.property("version", pluginVersion)
        filesMatching("plugin.yml") {
            expand("version" to pluginVersion)
        }
    }
}
