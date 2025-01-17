val fabrikt: Configuration by configurations.creating

val generationDir = "$buildDir/generated"
val apiFile = "$projectDir/openapi/api.yaml"

sourceSets {
    main { java.srcDirs("$generationDir/src/main/kotlin") }
    test { java.srcDirs("$generationDir/src/test/kotlin") }
}

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.0.20"
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
    val generatePolymorphicModelsCode = createCodeGenerationTask(
        "generatepolymorphicCode",
        "com.example.polymorphicmodels",
        "${rootProject.projectDir}/src/test/resources/examples/polymorphicModels/api.yaml",
    )

    val generateDiscriminatedOneOfCode = createCodeGenerationTask(
        "discriminatedOneOfCode",
        "com.example.discriminatedoneof",
        "${rootProject.projectDir}/src/test/resources/examples/discriminatedOneOf/api.yaml",
        listOf("--http-model-opts", "SEALED_INTERFACES_FOR_ONE_OF")
    )

    val generateEnumCode = createCodeGenerationTask(
        "generateEnumCode",
        "com.example.enums",
        "$projectDir/openapi/enums/api.yaml"
    )

    val generateSampleCode = createCodeGenerationTask(
        "generateSampleCode",
        "com.example.sample",
        "$projectDir/openapi/sample/api.yaml"
    )

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
        dependsOn(generatePolymorphicModelsCode)
        dependsOn(generateDiscriminatedOneOfCode)
        dependsOn(generateEnumCode)
        dependsOn(generateSampleCode)
    }

    withType<Test> {
        useJUnitPlatform()
        jvmArgs = listOf("--add-opens=java.base/java.lang=ALL-UNNAMED", "--add-opens=java.base/java.util=ALL-UNNAMED")
    }
}

fun TaskContainer.createCodeGenerationTask(
    name: String,
    packageName: String,
    apiFilePath: String,
    opts: List<String> = emptyList()
): TaskProvider<JavaExec> = register(name, JavaExec::class) {
    inputs.files(apiFilePath)
    outputs.dir(generationDir)
    outputs.cacheIf { true }
    classpath = rootProject.files("./build/libs/fabrikt-${rootProject.version}.jar")
    mainClass.set("com.cjbooms.fabrikt.cli.CodeGen")
    args = listOf(
        "--output-directory", generationDir,
        "--base-package", packageName,
        "--api-file", apiFilePath,
        "--validation-library", "NO_VALIDATION",
        "--targets", "http_models",
        "--serialization-library", "KOTLINX_SERIALIZATION",
    ) + opts
    dependsOn(":jar")
    dependsOn(":shadowJar")
}
