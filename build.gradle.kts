plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.61" // Apply the Kotlin JVM plugin to add support for Kotlin.
    id("org.jmailen.kotlinter") version "2.3.2" // Lint and formatting for Kotlin using ktlint
    
   
    `java-library` // For API and implementation separation.
}

repositories {
    jcenter()
}

dependencies {
    val jacksonVersion = "2.9.8"

    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.github.jknack:handlebars:4.1.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
    implementation("com.beust:jcommander:1.74")
    implementation("com.reprezen.kaizen:openapi-parser:4.0.1-201809050415") { exclude(group = "junit") }
    implementation("com.reprezen.jsonoverlay:jsonoverlay:4.0.3")
    implementation("com.squareup:kotlinpoet:1.3.0") { exclude(module = "kotlin-stdlib-jre7") }
    implementation("com.google.flogger:flogger:0.4")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.1.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.1.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.1.0")
    testImplementation("org.assertj:assertj-core:3.14.0")
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
        dependsOn(formatKotlin)
    }
    withType<Test> {
        useJUnitPlatform()
    }

}