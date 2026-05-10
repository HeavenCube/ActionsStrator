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

tasks.named<Jar>("jar") {
    destinationDirectory.set(file("build"))
}

tasks.named<Jar>("shadowJar") {
    archiveClassifier.set("")
}

tasks.register<Copy>("devJar") {
    val shadowJarTask = tasks.named<Jar>("shadowJar")
    from(layout.projectDirectory.dir("build/libs")) {
        rename { fileName: String ->
            fileName.replace(shadowJarTask.get().archiveFileName.get(), "${shadowJarTask.get().archiveBaseName.get()}-SNAPSHOT.jar")
        }
    }
    include(shadowJarTask.get().archiveFileName.get())
    into(file("run/plugins"))
    dependsOn(tasks.named("build"))
}