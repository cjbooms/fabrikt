val fabrikt: Configuration by configurations.creating

val generationDir = "$buildDir/generated"
val apiFile = "$projectDir/openapi/api.yaml"

sourceSets {
    main { java.srcDirs("$generationDir/src/main/kotlin") }
    test { java.srcDirs("$generationDir/src/test/kotlin") }
}

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.20" // Apply the Kotlin JVM plugin to add support for Kotlin.
    kotlin("plugin.serialization") version "1.8.20"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

val junitVersion: String by rootProject.extra
val kotlinxSerializationVersion: String by rootProject.extra
val kotlinxDateTimeVersion: String by rootProject.extra

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDateTimeVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testImplementation("org.assertj:assertj-core:3.24.2")
}

tasks {

    val generateCode by creating(JavaExec::class) {
        inputs.files(apiFile)
        outputs.dir(generationDir)
        outputs.cacheIf { true }
        classpath = rootProject.files("./build/libs/fabrikt-${rootProject.version}.jar")
        mainClass.set("com.cjbooms.fabrikt.cli.CodeGen")
        args = listOf(
            "--output-directory", generationDir,
            "--base-package", "com.example",
            "--api-file", apiFile,
            "--validation-library", "NO_VALIDATION",
            "--targets", "http_models",
            "--serialization-library", "KOTLINX_SERIALIZATION",
            "--http-model-opts", "SEALED_INTERFACES_FOR_ONE_OF",
        )
        dependsOn(":jar")
        dependsOn(":shadowJar")
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
        dependsOn(generateCode)
    }


    withType<Test> {
        useJUnitPlatform()
        jvmArgs = listOf("--add-opens=java.base/java.lang=ALL-UNNAMED", "--add-opens=java.base/java.util=ALL-UNNAMED")

    }
}