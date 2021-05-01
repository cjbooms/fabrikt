# Fabrikt `/ˈfa-brikt/` - Kotlin code from OpenApi3 specifications

This library was built to take advantage of the complex modeling features available in OpenApi3. It generates null-safe Kotlin models with advanced support for features suchs as: enumerations, sealed classes, polymorphic models (`@JsonSubTypes`), maps of maps. 

The team that built this tool initially contributed to the Kotkin code generation ability in [OpenApiTools](https://github.com/OpenAPITools/openapi-generator), but reached the limits of what could be achieved with template-based generation. This library levarages the rich OpenApi3 model provided by [KaiZen-OpenApi-Parser](https://github.com/RepreZen/KaiZen-OpenApi-Parser) and uses [Kotlin Poet](https://square.github.io/kotlinpoet/) to programatically construct Kotlin classes for maximum flexability. 

It was built at [Zalando Tech](https://opensource.zalando.com/) and is battle-tested in production there. It is particulary well suited to API's built according to Zalando's [REST API guidelines](https://opensource.zalando.com/restful-api-guidelines/). It is [available on Maven Central](https://search.maven.org/artifact/com.cjbooms/fabrikt) at the following coordinates:

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

### Example Generation

The test directory forms a living documentation full of [code examples](src/test/resources/examples) generated from different OpenApi3 permutations. 

Below showcases one single example from this directory, which uses an enum discriminator to generate polymorphic sealed classes:
```
openapi: 3.0.0
components:
  schemas:
    PolymorphicEnumDiscriminator:
      type: object
      discriminator:
        propertyName: some_enum
        mapping:
          obj_one: '#/components/schemas/ConcreteImplOne'
          obj_two: '#/components/schemas/ConcreteImplTwo'
      properties:
        some_enum:
          $ref: '#/components/schemas/EnumDiscriminator'
    ConcreteImplOne:
      allOf:
        - $ref: '#/components/schemas/PolymorphicEnumDiscriminator'
        - type: object
          properties:
            some_prop:
              type: string
    ConcreteImplTwo:
      allOf:
        - $ref: '#/components/schemas/PolymorphicEnumDiscriminator'
        - type: object
          properties:
            some_prop:
              type: string
    EnumDiscriminator:
      type: string
      enum:
        - obj_one
        - obj_two
```
```
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "some_enum",
    visible = true
)
@JsonSubTypes(
    JsonSubTypes.Type(
        value = ConcreteImplOne::class,
        name =
            "obj_one"
    ),
    JsonSubTypes.Type(value = ConcreteImplTwo::class, name = "obj_two")
)
sealed class PolymorphicEnumDiscriminator() {
    abstract val someEnum: EnumDiscriminator
}

enum class EnumDiscriminator(
    @JsonValue
    val value: String
) {
    OBJ_ONE("obj_one"),

    OBJ_TWO("obj_two");
}

data class ConcreteImplOne(
    @param:JsonProperty("some_prop")
    @get:JsonProperty("some_prop")
    val someProp: String? = null
) : PolymorphicEnumDiscriminator() {
    @get:JsonProperty("some_enum")
    @get:NotNull
    override val someEnum: EnumDiscriminator = EnumDiscriminator.OBJ_ONE
}

data class ConcreteImplTwo(
    @param:JsonProperty("some_prop")
    @get:JsonProperty("some_prop")
    val someProp: String? = null
) : PolymorphicEnumDiscriminator() {
    @get:JsonProperty("some_enum")
    @get:NotNull
    override val someEnum: EnumDiscriminator = EnumDiscriminator.OBJ_TWO
}
```


## Usage Instructions

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


### Command Line
Fabrikt is packaged as an executable jar, allowing it to be integrated into any build tool. The CLI can be invoked as follows:
```
   java -jar fabrikt.jar \
             --output-directory '/tmp' \
             --base-package 'com.example' \
             --api-file '/path-to-api/open-api.yaml' \
             --targets 'client' \
             --targets 'http_models' \
             --http-client-opts resilience4j
```

### Gradle example

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

## Publishing
This library is published to [Sonatype's OSS](https://oss.sonatype.org/#welcome) staging repository using Github actions when a release is drafted. It can be manually promoted from there to the release repository which is indexed by Maven Central.

