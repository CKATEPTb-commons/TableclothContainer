import proguard.gradle.ProGuardTask

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.guardsquare:proguard-gradle:7.2.1")
    }
}

plugins {
    id("java")
    id("maven-publish")
    id("com.github.johnrengelman.shadow").version("7.1.0")
    id("io.github.gradle-nexus.publish-plugin").version("1.1.0")
}

group = "dev.ckateptb.common"
version = "2.0.1-SNAPSHOT"


val rootPackage = "${project.group}.${project.name.toLowerCase()}"
val internal = "${rootPackage}.internal"

repositories {
    mavenCentral()
    maven("https://repo.animecraft.fun/repository/maven-snapshots/")
}

dependencies {
    compileOnly("dev.ckateptb.common:TableclothEvent:1.0.1-SNAPSHOT")
    implementation("com.google.guava:guava:32.0.1-jre")
    implementation("org.picocontainer:picocontainer:2.15")

    compileOnly("org.projectlombok:lombok:1.18.22")
    annotationProcessor("org.projectlombok:lombok:1.18.22")
}

tasks {
    shadowJar {
        relocate("com", "${internal}.com")
        relocate("javax", "${internal}.javax")
        relocate("org", "${internal}.org")
    }
    register<ProGuardTask>("shrink") {
        dependsOn(shadowJar)
        injars(shadowJar.get().outputs.files)
        outjars("${project.buildDir}/libs/${project.name}-${project.version}.jar")

        ignorewarnings()

        libraryjars("${System.getProperty("java.home")}/jmods")

        keep(mapOf("includedescriptorclasses" to true), "public class !${internal}.** { *; }")
        keepattributes("RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations,RuntimeVisibleTypeAnnotations")

        dontobfuscate()
        dontoptimize()
    }
    build {
        dependsOn("shrink")
    }
    publish {
        dependsOn("shrink")
    }
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

publishing {
    publications {
        publications.create<MavenPublication>("mavenJava") {
            artifacts {
                artifact(tasks.getByName("shrink").outputs.files.singleFile)
            }
        }
    }
}

nexusPublishing {
    repositories {
        create("myNexus") {
            nexusUrl.set(uri("https://repo.animecraft.fun/"))
            snapshotRepositoryUrl.set(uri("https://repo.animecraft.fun/repository/maven-snapshots/"))
            username.set(System.getenv("NEXUS_USERNAME"))
            password.set(System.getenv("NEXUS_PASSWORD"))
        }
    }
}