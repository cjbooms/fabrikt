package com.cjbooms.fabrikt.generators

import com.cjbooms.fabrikt.cli.CodeGenerationType
import com.cjbooms.fabrikt.cli.ControllerCodeGenTargetType
import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.generators.controller.SpringControllerInterfaceGenerator
import com.cjbooms.fabrikt.model.SourceApi
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



    // global authentication tests
    @Test
    fun `ensure that global authentication set to any authentication will add the required parameter`() {
        val controller = setupTest("global_authentication_required.yml")

        val functionParameter = controller.flatMap { it.members }
            .flatMap { (it as TypeSpec).funSpecs.flatMap { it.parameters } }

        assertThat(functionParameter).anySatisfy{ parameter ->
            assertThat(parameter.name).isEqualTo("authentication")
        }
    }

    @Test
    fun `ensure that global authentication set to empty authentication will add the prohibited parameter`() {
        val controller = setupTest("global_authentication_prohibited.yml")

        val functionParameter = controller.flatMap { it.members }
            .flatMap { (it as TypeSpec).funSpecs.flatMap { it.parameters } }

        assertThat(functionParameter).noneSatisfy{ parameter ->
            assertThat(parameter.name).isEqualTo("authentication")
        }
    }

    @Test
    fun `ensure that global authentication set to any AND empty authentication will add the optional parameter`() {
        val controller = setupTest("global_authentication_optional.yml")

        val functionParameter = controller.flatMap { it.members }
            .flatMap { (it as TypeSpec).funSpecs.flatMap { it.parameters } }


        assertThat(functionParameter).anySatisfy{ parameter ->
            assertThat(parameter.name).isEqualTo("authentication")
            assertThat(parameter.type.isNullable)
        }
    }

    @Test
    fun `ensure that no authentication defined will add no parameter`() {
        val controller = setupTest("global_authentication_none.yml")

        val functionParameter = controller.flatMap { it.members }
            .flatMap { (it as TypeSpec).funSpecs.flatMap { it.parameters } }

        assertThat(functionParameter).noneSatisfy{ parameter ->
            assertThat(parameter.name).isEqualTo("authentication")
        }
    }


    // operation level authentication tests
    @Test
    fun `ensure that both global security and operation-level security being defined will result in the correctly generated code`() {
        val controller = setupTest("operation_authentication_with_global.yml")

        val prohibitedController = (controller.find{it.name == "ProhibitedController"}!!.members.single() as TypeSpec).funSpecs.single()

        assertThat(prohibitedController.parameters).noneSatisfy{parameter ->
            assertThat(parameter.name).isEqualTo("authentication")}


        val requiredController = (controller.find{it.name == "RequiredController"}!!.members.single() as TypeSpec).funSpecs.single()

        assertThat(requiredController.parameters).anySatisfy{parameter ->
            assertThat(parameter.name).isEqualTo("authentication")}


        val optionalController = (controller.find{it.name == "OptionalController"}!!.members.single() as TypeSpec).funSpecs.single()

        assertThat(optionalController.parameters).anySatisfy{parameter ->
            assertThat(parameter.name).isEqualTo("authentication")}


        val defaultController = (controller.find{it.name == "DefaultController"}!!.members.single() as TypeSpec).funSpecs.single()

        assertThat(defaultController.parameters).anySatisfy{parameter ->
            assertThat(parameter.name).isEqualTo("authentication")}


        val noneController = (controller.find{it.name == "NoneController"}!!.members.single() as TypeSpec).funSpecs.single()

        assertThat(noneController.parameters).noneSatisfy{parameter ->
            assertThat(parameter.name).isEqualTo("authentication")}


    }

    @Test
    fun `ensure that only operation-level security being defined will result in the correctly generated code`() {
        val controller = setupTest("operation_authentication_without_global.yml")

        val prohibitedController = (controller.find{it.name == "ProhibitedController"}!!.members.single() as TypeSpec).funSpecs.single()

        assertThat(prohibitedController.parameters).noneSatisfy{parameter ->
            assertThat(parameter.name).isEqualTo("authentication")}


        val requiredController = (controller.find{it.name == "RequiredController"}!!.members.single() as TypeSpec).funSpecs.single()

        assertThat(requiredController.parameters).anySatisfy{parameter ->
            assertThat(parameter.name).isEqualTo("authentication")}


        val optionalController = (controller.find{it.name == "OptionalController"}!!.members.single() as TypeSpec).funSpecs.single()

        assertThat(optionalController.parameters).anySatisfy{parameter ->
            assertThat(parameter.name).isEqualTo("authentication")}


        val noneController = (controller.find{it.name == "NoneController"}!!.members.single() as TypeSpec).funSpecs.single()

        assertThat(noneController.parameters).noneSatisfy{parameter ->
            assertThat(parameter.name).isEqualTo("authentication")}
    }

}
