plugins {
    java
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("com.gradleup.shadow") version "9.4.1"
}

group = "fr.heavencube.actionsstrator"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
  maven("https://repo.papermc.io/repository/maven-public/")
  maven {
    name = "papermc"
    url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
  compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")
}

java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}
tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}