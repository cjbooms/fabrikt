import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.61" // Apply the Kotlin JVM plugin to add support for Kotlin.
    id("org.jmailen.kotlinter") version "2.3.2" // Lint and formatting for Kotlin using ktlint
    id("com.github.johnrengelman.shadow") version "4.0.1"
   
    `java-library` // For API and implementation separation.
}

val executableName = "fabrikt"
group = "com.cjbooms"

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
    implementation("com.pinterest:ktlint:0.35.0") // In 0.36.0 programmatic linting is removed :'(

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.1.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.1.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.1.0")
    testImplementation("org.assertj:assertj-core:3.14.0")
    // Below dependencies are solely present so code examples in the test resources dir compile
    testImplementation("javax.validation:validation-api:2.0.1.Final")
}

tasks {
    val fatJar = getByName<ShadowJar>("shadowJar") {
        manifest {
            attributes["Main-Class"] = "com.cjbooms.fabrikt.cli.CodeGen"
            attributes["Implementation-Title"] = "fabrikt"
            attributes["Implementation-Version"] = project.version
        }
        archiveBaseName.set(executableName)
        archiveClassifier.set("")
    }

    val printCodeGenUsage by creating(JavaExec::class) {
        dependsOn(fatJar)
        classpath = project.files("./build/libs/$executableName.jar")
        main = "com.cjbooms.fabrikt.cli.CodeGen"
        args = listOf("--help")
    }

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