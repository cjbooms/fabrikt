# Fabrikt - Fabricates Kotlin code from OpenApi3 specifications

Generates Kotlin code from OpenApi3 specifications, ensuring code matches API contracts. This library was built at [Zalando Tech](https://opensource.zalando.com/) and is battle-tested in production there. It is particulary well suited to API's built according to Zalando's [REST API guidelines](https://opensource.zalando.com/restful-api-guidelines/). It is [available on Maven Central](https://search.maven.org/artifact/com.cjbooms/fabrikt) at the following coordinates:

```
<dependency>
  <groupId>com.cjbooms</groupId>
  <artifactId>fabrikt</artifactId>
</dependency>
```

## Features

The library currently has support for generating:

* **Jackson annotated data classes**
* **Spring MVC annotated controller interfaces**
* **OkHttp Client** - with the option for a resilience4j fault-tolerance wrapper

## Examples

The test directory forms a living documentation full of [code examples](src/test/resources/examples) generated from different OpenApi3 permutations. 


## Configuration

This section documents the available CLI parameters for controlling what gets generated. This documentation is generated using: `./gradlew printCodeGenUsage`

 | Parameter              | Description
 | ---------------------- | ----------------------
 |   `--api-file`         | This must be a valid Open API v3 spec. All code generation will be based off this input.
 |   `--api-fragment`     | A partial Open API v3 fragment, to be combined with the primary API for code generation purposes.
 | * `--base-package`     | The base package which all code will be generated under.
 |   `--http-client-opts` | Select the options for the http client code that you want to be generated.
 |                        | CHOOSE ANY OF:
 |                        |   `RESILIENCE4J` - Generates a fault tolerance service for the client using the following library "io.github.resilience4j:resilience4j-all:+"
 |   `--http-model-opts`  | Select the options for the http models that you want to be generated.
 |                        | CHOOSE ANY OF:
 |                        |   `X_EXTENSIBLE_ENUMS` - This option treats x-extensible-enums as enums
 |                        |   `JAVA_SERIALIZATION` - This option adds Java Serializable interface to the generated models
 |                        |   `QUARKUS_REFLECTION` - This option adds @RegisterForReflection to the generated models. Requires dependency "'io.quarkus:quarkus-core:+"
 |   `--output-directory` | Allows the generation dir to be overridden. Defaults to current dir
 |   `--targets`          | Targets are the parts of the application that you want to be generated.
 |                        | CHOOSE ANY OF:
 |                        |   `HTTP_MODELS` - Jackson annotated data classes to represent the schema objects defined in the input.
 |                        |   `CONTROLLERS` - Spring annotated HTTP controllers for each of the endpoints defined in the input.
 |                        |   `CLIENT` - Simple http rest client.
 |                        |   `QUARKUS_REFLECTION_CONFIG` - This options generates the reflection-config.json file for quarkus integration projects


## Examples

### Gradle Task

Here is an example of a Gradle task with code generated to the `build/generated` directory, and execution linked to the compile task. 

```kotlin
val fabrikt: Configuration by configurations.creating

val generationDir = "$buildDir/generated"

sourceSets {
    main { java.srcDirs("$generationDir/src/main/kotlin") }
    test { java.srcDirs("$generationDir/src/test/kotlin") }
    ...
}

tasks {   
    ...
    val generateCode by creating(JavaExec::class) {
        classpath(fabrikt)
        main = "com.cjbooms.fabrikt.cli.CodeGen"
        args = listOf(
            "--output-directory", generationDir,
            "--base-package", "com.example",
            "--api-file", "/path-to-api/open-api.yaml",
            "--targets", "http_models",
            "--targets", "client",
            "--http-client-opts", "resilience4j"
        )
    }
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
        dependsOn(generateCode)
    }
}

dependencies {
     fabric("com.cjbooms:fabrikt:+") // This should be pinned  
     ...
}
```
### Command Line
```
   java -jar fabrikt.jar \
             --output-directory '/tmp' \
             --base-package 'com.example' \
             --api-file '/path-to-api/open-api.yaml' \
             --targets 'client' \
             --targets 'http_models' \
             --http-client-opts resilience4j
```
