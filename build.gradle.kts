plugins {
    kotlin("jvm") version "2.3.21"
}

group = "fr.heavencube.actionsstrator"
version = "1.0.0-SNAPSHOT"

repositories {
  maven {
    name = "papermc"
    url = uri("https://repo.papermc.io/repository/maven-public/")
  }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")
}

tasks.processResources {
    val props = mapOf("version" to project.version.toString())
    inputs.properties(props)
    filteringCharset = Charsets.UTF_8.name()
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}

kotlin {
    jvmToolchain(25)
}