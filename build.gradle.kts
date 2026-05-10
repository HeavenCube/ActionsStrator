plugins {
    kotlin("jvm") version "2.3.21"
}

kotlin {
    jvmToolchain(25)
}

// Change to true when releasing
val release = false
val majorVersion = "1.0.0"
val minorVersion = if (release) "Release" else "SNAPSHOT-" + (System.getenv("BUILD_NUMBER") ?: "local")

group = "fr.heavencube.actionsstrator"
version = "$majorVersion-$minorVersion"

repositories {
  maven {
    name = "papermc"
    url = uri("https://repo.papermc.io/repository/maven-public/")
  }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")
}

tasks {
    jar {
        archiveFileName.set("ActionsStrator-${rootProject.version}.jar")
    }
    processResources {
        val pluginVersion = rootProject.version.toString()
        inputs.property("version", pluginVersion)
        filesMatching("paper-plugin.yml") {
            expand("version" to pluginVersion)
        }
    }
}