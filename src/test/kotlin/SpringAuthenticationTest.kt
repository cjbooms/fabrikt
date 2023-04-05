package com.cjbooms.fabrikt.generators

import com.cjbooms.fabrikt.cli.CodeGenerationType
import com.cjbooms.fabrikt.cli.ControllerCodeGenTargetType
import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.generators.controller.SpringControllerInterfaceGenerator
import com.cjbooms.fabrikt.generators.controller.SpringControllers
import com.cjbooms.fabrikt.generators.controller.metadata.SpringImports
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
class SpringAuthenticationTest {
    private val basePackage = "authenticationTest"
    private lateinit var generated: Collection<FileSpec>

    @Suppress("unused")
    private fun testCases(): Stream<String> = Stream.of(
        "authenticationTest"
    )

    private fun setupAuthenticationTest() {
        val api = SourceApi(readTextResource("/authenticationTest/global_authentication_none.yml"))
        generated = SpringControllerInterfaceGenerator(Packages(basePackage), api).generate().files
    }

    private fun setupTest(testPath: String): Collection<FileSpec> {
        val api = SourceApi(readTextResource("/authenticationTest/$testPath"))
        return SpringControllerInterfaceGenerator(Packages(basePackage), api).generate().files
    }

    @BeforeEach
    fun init() {
        MutableSettings.updateSettings(
            genTypes = setOf(CodeGenerationType.CONTROLLERS),
            controllerTarget = ControllerCodeGenTargetType.SPRING
        )
    }


    /*
    // @Test
    @ParameterizedTest
    @MethodSource("testCases")
    fun `correct models are generated for different OpenApi Specifications`(testCaseName: String) {
        val basePackage = "authenticationTest"
        val api = SourceApi(readTextResource("/authenticationTest/global_authentication_none.yml"))
        val expectedControllers = readTextResource("/authenticationTest/controller/spring/Controllers.kt")

        val controllers = SpringControllerInterfaceGenerator(
            Packages(basePackage),
            api
        ).generate().toSingleFile()

        assertThat(controllers).isEqualTo(expectedControllers)
    }*/



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

        val functionParameter = controller.flatMap { it.members }
            .flatMap { (it as TypeSpec).funSpecs.flatMap { it.parameters } }

        assertThat(functionParameter).anySatisfy{ member ->
            assertThat(member.name).isEqualTo("authentication")
        }
    }

    @Test
    fun `ensure that global authentication set to empty authentication will add the prohibited parameter`() {
        val controller = setupTest("global_authentication_prohibited.yml")

        val functionParameter = controller.flatMap { it.members }
            .flatMap { (it as TypeSpec).funSpecs.flatMap { it.parameters } }

        assertThat(functionParameter).noneSatisfy{ member ->
            assertThat(member.name).isEqualTo("authentication")
        }
    }

    @Test
    fun `ensure that global authentication set to any AND empty authentication will add the optional parameter`() {
        val controller = setupTest("global_authentication_optional.yml")

        val functionParameter = controller.flatMap { it.members }
            .flatMap { (it as TypeSpec).funSpecs.flatMap { it.parameters } }


        assertThat(functionParameter).anySatisfy{ member ->
            assertThat(member.name).isEqualTo("authentication")
            assertThat(member.type.isNullable == true)
        }
    }

    @Test
    fun `ensure that no authentication defined will add no parameter`() {
        val controller = setupTest("global_authentication_none.yml")

        val functionParameter = controller.flatMap { it.members }
            .flatMap { (it as TypeSpec).funSpecs.flatMap { it.parameters } }

        assertThat(functionParameter).noneSatisfy{ member ->
            assertThat(member.name).isEqualTo("authentication")
        }
    }

    @Test
    fun `ensure that global security and operational security defined will set the correct parameters `() {
        val controller = setupTest("operation_authentication_with_global.yml")

        val prohibitedController = (controller.find({it.name == "ProhibitedController"})!!.members.single() as TypeSpec).funSpecs.single()
        val requiredController = (controller.find({it.name == "RequiredController"})!!.members.single() as TypeSpec).funSpecs.single()
        val optionalController = (controller.find({it.name == "OptionalController"})!!.members.single() as TypeSpec).funSpecs.single()
        val defaultController = (controller.find({it.name == "DefaultController"})!!.members.single() as TypeSpec).funSpecs.single()
        val noneController = (controller.find({it.name == "NoneController"})!!.members.single() as TypeSpec).funSpecs.single()

        assertThat(prohibitedController.parameters).noneSatisfy{member ->
            assertThat(member.name).isEqualTo("authentication")}
        assertThat(requiredController.parameters).anySatisfy{member ->
            assertThat(member.name).isEqualTo("authentication")}
        assertThat(optionalController.parameters).anySatisfy{member ->
            assertThat(member.name).isEqualTo("authentication")}
        assertThat(noneController.parameters).noneSatisfy{member ->
            assertThat(member.name).isEqualTo("authentication")}
        // expect authenticated because of the global setting
        assertThat(defaultController.parameters).anySatisfy{member ->
            assertThat(member.name).isEqualTo("authentication")}

    }

    @Test
    fun `ensure that ONLY operational security defined will set the correct parameters `() {
        val controller = setupTest("operation_authentication_without_global.yml")

        val prohibitedController = (controller.find({it.name == "ProhibitedController"})!!.members.single() as TypeSpec).funSpecs.single()
        val requiredController = (controller.find({it.name == "RequiredController"})!!.members.single() as TypeSpec).funSpecs.single()
        val optionalController = (controller.find({it.name == "OptionalController"})!!.members.single() as TypeSpec).funSpecs.single()
        val noneController = (controller.find({it.name == "NoneController"})!!.members.single() as TypeSpec).funSpecs.single()

        assertThat(prohibitedController.parameters).noneSatisfy{member ->
            assertThat(member.name).isEqualTo("authentication")}
        assertThat(requiredController.parameters).anySatisfy{member ->
            assertThat(member.name).isEqualTo("authentication")}
        assertThat(optionalController.parameters).anySatisfy{member ->
            assertThat(member.name).isEqualTo("authentication")}
        assertThat(noneController.parameters).noneSatisfy{member ->
            assertThat(member.name).isEqualTo("authentication")}

    }

    private fun SpringControllers.toSingleFile(): String {
        val destPackage = if (controllers.isNotEmpty()) controllers.first().destinationPackage else ""
        val singleFileBuilder = FileSpec.builder(destPackage, "dummyFilename")
        controllers.forEach {
            val builder = singleFileBuilder
                .addImport(SpringImports.Static.REQUEST_METHOD.first, SpringImports.Static.REQUEST_METHOD.second)
                .addImport(
                    SpringImports.Static.RESPONSE_STATUS.first,
                    SpringImports.Static.RESPONSE_STATUS.second
                )
                .addType(it.spec)
            builder.build()
        }
        return Linter.lintString(singleFileBuilder.build().toString())
    }


}
