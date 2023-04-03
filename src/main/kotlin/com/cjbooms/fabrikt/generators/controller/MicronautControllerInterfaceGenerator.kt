package com.cjbooms.fabrikt.generators.controller

import com.cjbooms.fabrikt.cli.ControllerCodeGenOptionType
import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.generators.GeneratorUtils.toIncomingParameters
import com.cjbooms.fabrikt.generators.GeneratorUtils.toKdoc
import com.cjbooms.fabrikt.generators.controller.ControllerGeneratorUtils.happyPathResponse
import com.cjbooms.fabrikt.generators.controller.ControllerGeneratorUtils.methodName
import com.cjbooms.fabrikt.generators.controller.ControllerGeneratorUtils.SecurityOption
import com.cjbooms.fabrikt.generators.controller.ControllerGeneratorUtils.securityOption
import com.cjbooms.fabrikt.generators.controller.metadata.JavaXAnnotations
import com.cjbooms.fabrikt.generators.controller.metadata.MicronautImports
import com.cjbooms.fabrikt.model.*
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isSingleResource
import com.cjbooms.fabrikt.util.KaizenParserExtensions.routeToPaths
import com.reprezen.kaizen.oasparser.model3.Operation
import com.reprezen.kaizen.oasparser.model3.Path
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.cjbooms.fabrikt.model.RequestParameter


class MicronautControllerInterfaceGenerator(
    private val packages: Packages,
    private val api: SourceApi,
    private val options: Set<ControllerCodeGenOptionType> = emptySet()
) : ControllerInterfaceGenerator(packages, api) {

    private val useSuspendModifier: Boolean
        get() = options.any { it == ControllerCodeGenOptionType.SUSPEND_MODIFIER }

    override fun generate(): MicronautControllers =
        MicronautControllers(
            api.openApi3.routeToPaths().map { (resourceName, paths) ->
                buildController(resourceName, paths.values)
            }.toSet()
        )

    override fun controllerBuilder(
        className: String,
        basePath: String
    ) =
        TypeSpec.interfaceBuilder(className)
            .addAnnotation(
                AnnotationSpec
                    .builder(MicronautImports.CONTROLLER)
                    .build()
            )

    override fun buildFunction(
        path: Path,
        op: Operation,
        verb: String,
    ): FunSpec {
        val methodName = methodName(op, verb, path.pathString.isSingleResource())
        val returnType = MicronautImports.RESPONSE.parameterizedBy(op.happyPathResponse(packages.base))
        val parameters = op.toIncomingParameters(packages.base, path.parameters, emptyList())

        // TODO set global Security
        //val hasGlobalSecurity = this.api.

        // Main method builder
        val funcSpec = FunSpec
            .builder(methodName)
            .addModifiers(KModifier.ABSTRACT)
            .addKdoc(op.toKdoc(parameters))
            .addMicronautFunAnnotation(op, verb, path.pathString)
            .apply {
                if (useSuspendModifier)
                    addModifiers(KModifier.SUSPEND)
            }
            .returns(returnType)

        // Function parameters
        parameters
            .map {
                when (it) {
                    is BodyParameter ->
                        it
                            .toParameterSpecBuilder()
                            .addAnnotation(
                                AnnotationSpec
                                    .builder(MicronautImports.BODY).build()
                            )
                            .addAnnotation(JavaXAnnotations.validBuilder().build())
                            .build()

                    is RequestParameter ->
                        it
                            .toParameterSpecBuilder()
                            .addValidationAnnotations(it)
                            .addMicronautParamAnnotation(it)
                            .build()
                }
            }
            .forEach { funcSpec.addParameter(it) }


        val security = op.getSecurityRequirements()
        val securityOption = security.securityOption()

        // TODO add correct param depending on case
        if(security.isNotEmpty()) {
            funcSpec.addParameter(
                ParameterSpec.builder("authentication", MicronautImports.AUTHENTICATION)
                    .build())
        }


        return funcSpec.build()
    }

    private fun FunSpec.Builder.addMicronautFunAnnotation(op: Operation, verb: String, path: String): FunSpec.Builder {
        val produces = op.responses
            .flatMap { it.value.contentMediaTypes.keys }
            .toTypedArray()

        val security = op.getSecurityRequirements()

        val consumes = op.requestBody
            .contentMediaTypes.keys
            .toTypedArray()

        this.addAnnotation(
            AnnotationSpec
                .builder(MicronautImports.HttpMethods.byName(verb))
                .addMember("uri = %S", path).build()
        )

        if (consumes.isNotEmpty()) {
            this.addAnnotation(
                AnnotationSpec
                    .builder(MicronautImports.CONSUMES)
                    .addMember(
                        "value = %L",
                        consumes.joinToString(
                            prefix = "[",
                            postfix = "]",
                            separator = ", ",
                            transform = { "\"$it\"" })
                    )
                    .build()
            )
        }

        if (produces.isNotEmpty()) {
            this.addAnnotation(
                AnnotationSpec
                    .builder(MicronautImports.PRODUCES)
                    .addMember(
                        "value = %L",
                        produces.joinToString(
                            prefix = "[",
                            postfix = "]",
                            separator = ", ",
                            transform = { "\"$it\"" })
                    )
                    .build()
            )
        }

        // TODO we want to check if security is Basic or empty Object
        if(security.isNotEmpty()) {
            this.addAnnotation(
                AnnotationSpec
                    .builder(MicronautImports.SECURED)
                    .addMember(
                        "SecurityRule.IS_AUTHENTICATED"
                    )
                    .build()
            )
        }


        return this
    }

    private fun ParameterSpec.Builder.addMicronautParamAnnotation(parameter: RequestParameter): ParameterSpec.Builder =
        when (parameter.parameterLocation) {
            QueryParam -> AnnotationSpec
                .builder(MicronautImports.QUERY_VALUE)

            HeaderParam -> AnnotationSpec
                .builder(MicronautImports.HEADER)

            PathParam -> AnnotationSpec
                .builder(MicronautImports.PATH_VARIABLE)
        }.let {
            it.addMember("value = %S", parameter.oasName)
            // here it will be added to each parameter

            if (parameter.defaultValue != null)
                it.addMember("defaultValue = %S", parameter.defaultValue)

            this.addAnnotation(it.build())
        }
}

data class MicronautControllers(val controllers: Collection<ControllerType>) : KotlinTypes(controllers)