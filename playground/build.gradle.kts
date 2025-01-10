plugins {
    application
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

application {
    mainClass.set("PlaygroundApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
}

repositories {
    mavenCentral()
}

val ktorVersion: String by rootProject.extra

dependencies {
    implementation(project(":"))

    // ktor server
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-html-builder:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")

    // logging
    implementation("ch.qos.logback:logback-classic:1.5.6")

    implementation("com.squareup:kotlinpoet:1.14.2") { exclude(module = "kotlin-stdlib-jre7") }

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "PlaygroundApplicationKt"
    }
}

tasks.withType<CreateStartScripts> {
    dependsOn(tasks.shadowJar)
}

tasks.named("shadowJar") {
    dependsOn(":shadowJar")
}

