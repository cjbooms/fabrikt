val fabrikt: Configuration by configurations.creating

val generationDir = "$buildDir/generated"

sourceSets {
    main { java.srcDirs("$generationDir/src/main/kotlin") }
    test { java.srcDirs("$generationDir/src/test/kotlin") }
}

plugins {
    kotlin("jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

val jacksonVersion: String by rootProject.extra
val junitVersion: String by rootProject.extra

dependencies {
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("javax.validation:validation-api:2.0.1.Final")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testImplementation("org.assertj:assertj-core:3.24.2")
}

fun createGenerateCodeTask(name: String, apiFilePath: String, basePackage: String) =
    tasks.create(name, JavaExec::class) {
        inputs.files(file(apiFilePath))
        outputs.dir(generationDir)
        outputs.cacheIf { true }
        classpath = rootProject.files("./build/libs/fabrikt-${rootProject.version}.jar")
        mainClass.set("com.cjbooms.fabrikt.cli.CodeGen")
        args = listOf(
            "--output-directory", generationDir,
            "--base-package", basePackage,
            "--api-file", apiFilePath,
            "--targets", "http_models"
        )
        dependsOn(":jar")
        dependsOn(":shadowJar")
    }

tasks {
    val generateCodeTask = createGenerateCodeTask(
        "generateCode",
        "$projectDir/openapi/api.yaml",
        "com.example"
    )
    val generatePrimitiveTypesCodeTask = createGenerateCodeTask(
        "generatePrimitiveTypesCode",
        "${rootProject.projectDir}/src/test/resources/examples/primitiveTypes/api.yaml",
        "com.example.primitives"
    )

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
        dependsOn(generateCodeTask)
        dependsOn(generatePrimitiveTypesCodeTask)
    }

    withType<Test> {
        useJUnitPlatform()
        jvmArgs = listOf("--add-opens=java.base/java.lang=ALL-UNNAMED", "--add-opens=java.base/java.util=ALL-UNNAMED")
    }
}