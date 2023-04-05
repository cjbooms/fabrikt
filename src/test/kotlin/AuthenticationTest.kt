package com.cjbooms.fabrikt.generators

import com.cjbooms.fabrikt.cli.CodeGenerationType
import com.cjbooms.fabrikt.cli.ControllerCodeGenTargetType
import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.generators.controller.MicronautControllerInterfaceGenerator
import com.cjbooms.fabrikt.generators.controller.MicronautControllers
import com.cjbooms.fabrikt.model.Destinations.controllersPackage
import com.cjbooms.fabrikt.model.SourceApi
import com.cjbooms.fabrikt.util.Linter
import com.cjbooms.fabrikt.util.ResourceHelper.readTextResource
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthenticationTest {
    private val basePackage = "authenticationTest"
    private lateinit var generated: Collection<FileSpec>

    @Suppress("unused")
    private fun testCases(): Stream<String> = Stream.of(
        "authenticationTest"
    )

    private fun setupAuthenticationTest() {
        val api = SourceApi(readTextResource("/authenticationTest/global_authentication_none.yml"))
        generated = MicronautControllerInterfaceGenerator(Packages(basePackage), api).generate().files
    }

    private fun setupTest(testPath: String): Collection<FileSpec> {
        val api = SourceApi(readTextResource("/authenticationTest/$testPath"))
        return MicronautControllerInterfaceGenerator(Packages(basePackage), api).generate().files
    }

    @BeforeEach
    fun init() {
        MutableSettings.updateSettings(
            genTypes = setOf(CodeGenerationType.CONTROLLERS),
            controllerTarget = ControllerCodeGenTargetType.MICRONAUT
        )
    }

    // @Test

    @ParameterizedTest
    @MethodSource("testCases")
    fun `correct models are generated for different OpenApi Specifications`(testCaseName: String) {
        val basePackage = "authenticationTest"
        val api = SourceApi(readTextResource("/authenticationTest/global_authentication_none.yml"))
        val expectedControllers = readTextResource("/authenticationTest/controllers/micronaut/Controllers.kt")

        val controllers = MicronautControllerInterfaceGenerator(
            Packages(basePackage),
            api
        ).generate().toSingleFile()


        assertThat(controllers).isEqualTo(expectedControllers)
    }



    @Test
    fun `the correct controllers are generated`() {
        setupAuthenticationTest()
        assertThat(generated.map { it.name })
            .containsOnly(
                "TestController"
            )
    }

    @Test
    fun `ensure package is correct`() {
        setupAuthenticationTest()
        assertThat(generated.map { it.packageName }.distinct())
            .containsOnly(controllersPackage(basePackage))
    }


    // global authentication tests
    @Test
    fun `ensure that global authentication set to any authentication will add the required parameter`() {
        val controller = setupTest("global_authentication_required.yml")
        val functionAnnotations = controller.flatMap { it.members }
            .flatMap { (it as TypeSpec).funSpecs.flatMap { it.annotations } }

        val functionParameter = controller.flatMap { it.members }
            .flatMap { (it as TypeSpec).funSpecs.flatMap { it.parameters } }

        assertThat(functionAnnotations).anySatisfy{member ->
            assertThat(member.className.canonicalName).isEqualTo("io.micronaut.security.annotation.Secured")
            assertThat(member.members).anyMatch{ it.toString() == "SecurityRule.IS_AUTHENTICATED"}
        }

        assertThat(functionParameter).anySatisfy{ member ->
            assertThat(member.name).isEqualTo("authentication")
        }
    }

    @Test
    fun `ensure that global authentication set to empty authentication will add the prohibited parameter`() {
        val controller = setupTest("global_authentication_prohibited.yml")
        val functionAnnotations = controller.flatMap { it.members }
            .flatMap { (it as TypeSpec).funSpecs.flatMap { it.annotations } }

        val functionParameter = controller.flatMap { it.members }
            .flatMap { (it as TypeSpec).funSpecs.flatMap { it.parameters } }

        assertThat(functionAnnotations).anySatisfy{member ->
            assertThat(member.className.canonicalName).isEqualTo("io.micronaut.security.annotation.Secured")
            assertThat(member.members).anyMatch{ it.toString() == "SecurityRule.IS_ANONYMOUS"}
        }

        assertThat(functionParameter).noneSatisfy{ member ->
            assertThat(member.name).isEqualTo("authentication")
        }
    }

    @Test
    fun `ensure that global authentication set to any and empty authentication will add the optional parameter`() {
        val controller = setupTest("global_authentication_optional.yml")
        val functionAnnotations = controller.flatMap { it.members }
            .flatMap { (it as TypeSpec).funSpecs.flatMap { it.annotations } }

        val functionParameter = controller.flatMap { it.members }
            .flatMap { (it as TypeSpec).funSpecs.flatMap { it.parameters } }

        assertThat(functionAnnotations).anySatisfy{member ->
            assertThat(member.className.canonicalName).isEqualTo("io.micronaut.security.annotation.Secured")
            assertThat(member.members).anyMatch{ it.toString() == "SecurityRule.IS_AUTHENTICATED, SecurityRule.IS_ANONYMOUS"}
        }

        // TODO here we would like to check that the correct one is set to true
        assertThat(functionParameter).anySatisfy{ member ->
            assertThat(member.name).isEqualTo("authentication")
            assertThat(member.type.isNullable == true)
        }
    }

    @Test
    fun `ensure that no authentication set will add no parameter`() {
        val controller = setupTest("global_authentication_none.yml")
        val functionAnnotations = controller.flatMap { it.members }
            .flatMap { (it as TypeSpec).funSpecs.flatMap { it.annotations } }

        val functionParameter = controller.flatMap { it.members }
            .flatMap { (it as TypeSpec).funSpecs.flatMap { it.parameters } }

        assertThat(functionAnnotations).noneSatisfy{member ->
            assertThat(member.className.canonicalName).isEqualTo("io.micronaut.security.annotation.Secured")
            assertThat(member.members).anyMatch{ it.toString() == "SecurityRule.IS_AUTHENTICATED"}
            assertThat(member.members).anyMatch{ it.toString() == "SecurityRule.IS_ANONYMOUS"}
        }

        // TODO here we would like to check that the correct one is set to true
        assertThat(functionParameter).noneSatisfy{ member ->
            assertThat(member.name).isEqualTo("authentication")
        }
    }

    @Test
    fun `ensure that global security and operational security set will set the correct parameters `() {
        val controller = setupTest("operation_authentication_with_global.yml")
        val functionAnnotations = controller.flatMap { it.members }
            .flatMap { (it as TypeSpec).funSpecs.flatMap { it.annotations } }

        val functionParameter = controller.flatMap { it.members }
            .flatMap { (it as TypeSpec).funSpecs.flatMap { it.parameters } }


        assertThat(functionAnnotations).anySatisfy{member ->
            assertThat(member.className.canonicalName).isEqualTo("io.micronaut.security.annotation.Secured")
            //assertThat(member.members).areExactly(2,member.toString() == "SecurityRule.IS_AUTHENTICATED")
            assertThat(member.members).anyMatch{ it.toString() == "SecurityRule.IS_ANONYMOUS"}
            assertThat(member.members).anyMatch{ it.toString() == "SecurityRule.IS_ANONYMOUS,SecurityRule.IS_AUTHENTICATED"}
        }

        // TODO here we would like to check that the correct one is set to true
        assertThat(functionParameter).noneSatisfy{ member ->
            assertThat(member.name).isEqualTo("authentication")
        }


    }

    private fun MicronautControllers.toSingleFile(): String {
        val destPackage = if (controllers.isNotEmpty()) controllers.first().destinationPackage else ""
        val singleFileBuilder = FileSpec.builder(destPackage, "dummyFilename")
        controllers.forEach {
            singleFileBuilder.addType(it.spec).build()
        }
        return Linter.lintString(singleFileBuilder.build().toString())
    }

}
