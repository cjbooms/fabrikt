import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.61" // Apply the Kotlin JVM plugin to add support for Kotlin.
    id("org.jmailen.kotlinter") version "2.3.2" // Lint and formatting for Kotlin using ktlint
    id("com.github.johnrengelman.shadow") version "4.0.1"
    id("org.jetbrains.dokka") version "0.10.1"
    id("com.palantir.git-version") version "0.12.3"
    id("maven-publish")
    id("signing")

    `java-library` // For API and implementation separation.
}

val executableName = "fabrikt"

group = "com.cjbooms"
val gitVersion: groovy.lang.Closure<*> by extra
version = gitVersion.call()

val projectUrl = "https://github.com/cjbooms/fabrikt"
val projectScmUrl = "scm:https://cjbooms@github.com/cjbooms/fabrikt.git"
val projectScmConUrl = "scm:https://cjbooms@github.com/cjbooms/fabrikt.git"
val projectScmDevUrl = "scm:git://github.com/cjbooms/fabrikt.git"
val projectIssueUrl = "https://github.com/cjbooms/fabrikt/issues"
val projectName = "Fabrikt"
val projectDesc = "Fabricates Kotlin code from OpenApi3 specifications"
val projectLicenseName = "Apache License 2.0"
val projectLicenseUrl = "https://opensource.org/licenses/Apache-2.0"

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
    testImplementation("org.springframework:spring-webmvc:5.1.9.RELEASE")
    testImplementation("com.squareup.okhttp3:okhttp:4.9.1")
}

tasks {
    val shadowJar by getting(ShadowJar::class) {
        manifest {
            attributes["Main-Class"] = "com.cjbooms.fabrikt.cli.CodeGen"
            attributes["Implementation-Title"] = "fabrikt"
            attributes["Implementation-Version"] = project.version
            attributes["Built-JDK"] =  System.getProperty("java.version")
            attributes["Built-Gradle"] =  gradle.gradleVersion
        }
        archiveBaseName.set(executableName)
        archiveClassifier.set("")
    }

    val dokka by getting(DokkaTask::class) {
        outputFormat = "html"
        outputDirectory = "$buildDir/dokka"
    }

    val sourcesJar by creating(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets.getByName("main").allSource)
    }

    val kotlinDocJar by creating(Jar::class) {
        group = JavaBasePlugin.DOCUMENTATION_GROUP
        description = "Assembles Kotlin docs with Dokka"
        archiveClassifier.set("javadoc")
        from(dokka)
        dependsOn(dokka)
    }

    val printCodeGenUsage by creating(JavaExec::class) {
        dependsOn(shadowJar)
        classpath = project.files("./build/libs/$executableName-$version.jar")
        main = "com.cjbooms.fabrikt.cli.CodeGen"
        args = listOf("--help")
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
        }
        dependsOn(formatKotlin)
    }

    withType<Test> {
        useJUnitPlatform()
    }
}

publishing {
    repositories {
        maven {
            name = "OSSRH"
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
            credentials {
                username = System.getenv("OSSRH_USERNAME")
                password = System.getenv("OSSRH_PASSWORD")
            }
        }
    }

    publications {
        create<MavenPublication>("fabrikt") {
            artifact(tasks["shadowJar"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["kotlinDocJar"])

            pom {
                name.set(projectName)
                description.set(projectDesc)
                url.set(projectUrl)
                inceptionYear.set("2020")
                licenses {
                    license {
                        name.set(projectLicenseName)
                        url.set(projectLicenseUrl)
                    }
                }
                developers {
                    developer {
                        id.set("cjbooms")
                        name.set("Conor Gallaguer")
                        email.set("cjbooms@gmail.com")
                    }
                    developer {
                        id.set("averabaq")
                        name.set("Alejandro Vera-Baquero")
                        email.set("averabaq@gmail.com")
                    }
                }
                scm {
                    connection.set(projectScmConUrl)
                    developerConnection.set(projectScmDevUrl)
                    url.set(projectScmUrl)
                }

            }
        }
    }
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)

    sign(publishing.publications["fabrikt"])
}
