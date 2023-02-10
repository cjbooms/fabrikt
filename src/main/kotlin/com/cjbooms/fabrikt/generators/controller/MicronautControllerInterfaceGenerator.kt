package com.cjbooms.fabrikt.generators.controller

import com.cjbooms.fabrikt.cli.ControllerCodeGenOptionType
import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.generators.GeneratorUtils.toIncomingParameters
import com.cjbooms.fabrikt.generators.GeneratorUtils.toKdoc
import com.cjbooms.fabrikt.generators.controller.ControllerGeneratorUtils.controllerName
import com.cjbooms.fabrikt.generators.controller.ControllerGeneratorUtils.happyPathResponse
import com.cjbooms.fabrikt.generators.controller.ControllerGeneratorUtils.methodName
import com.cjbooms.fabrikt.generators.controller.metadata.JavaXAnnotations
import com.cjbooms.fabrikt.generators.controller.metadata.MicronautImports
import com.cjbooms.fabrikt.model.*
import com.cjbooms.fabrikt.util.KaizenParserExtensions.basePath
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isSingleResource
import com.cjbooms.fabrikt.util.KaizenParserExtensions.routeToPaths
import com.reprezen.kaizen.oasparser.model3.Operation
import com.reprezen.kaizen.oasparser.model3.Path
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

class MicronautControllerInterfaceGenerator(
    private val packages: Packages,
    private val api: SourceApi,
    private val options: Set<ControllerCodeGenOptionType> = emptySet()
) : ControllerInterfaceGenerator {

    private val useSuspendModifier: Boolean
        get() = options.any { it == ControllerCodeGenOptionType.SUSPEND_MODIFIER }

    override fun generate(): MicronautControllers =
        MicronautControllers(
            api.openApi3.routeToPaths().map { (resourceName, paths) ->
                buildController(resourceName, paths.values)
            }.toSet()
        )

    private fun buildController(resourceName: String, paths: Collection<Path>): ControllerType {
        val typeBuilder: TypeSpec.Builder = controllerBuilder(
            className = controllerName(resourceName),
            basePath = api.openApi3.basePath()
        )

        paths.flatMap { path ->
            path.operations
                .filter { it.key.toUpperCase() != "HEAD" }
                .map { op ->
                    buildFunction(
                        path,
                        op.value,
                        op.key,
                    )
                }
        }.forEach { typeBuilder.addFunction(it) }

        return ControllerType(
            typeBuilder.build(),
            packages.base
        )
    }

    private fun controllerBuilder(
        className: String,
        basePath: String
    ) =
        TypeSpec.interfaceBuilder(className)
            .addAnnotation(
                AnnotationSpec
                    .builder(MicronautImports.CONTROLLER)
                    .build()
            )

    private fun buildFunction(
        path: Path,
        op: Operation,
        verb: String,
    ): FunSpec {
        val methodName = methodName(op, verb, path.pathString.isSingleResource())
        val returnType = MicronautImports.RESPONSE.parameterizedBy(op.happyPathResponse(packages.base))
        val parameters = op.toIncomingParameters(packages.base, path.parameters, emptyList())
        val securityRequirements = op.securityRequirements

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

        // Authentication parameter for OIDC
        if (securityRequirements.any { "OpenID" in it.requirements.keys }) {
            funcSpec.addParameter(
                ParameterSpec.builder("authentication", MicronautImports.AUTHENTICATION)
                    .build()
            )
        }

        return funcSpec.build()
    }

    private fun FunSpec.Builder.addMicronautFunAnnotation(op: Operation, verb: String, path: String): FunSpec.Builder {
        val produces = op.responses
            .flatMap { it.value.contentMediaTypes.keys }
            .toTypedArray()

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

        return this
    }

    private fun ParameterSpec.Builder.addValidationAnnotations(parameter: RequestParameter): ParameterSpec.Builder {
        if (parameter.minimum != null) this.addAnnotation(JavaXAnnotations.min(parameter.minimum.toInt()))
        if (parameter.maximum != null) this.addAnnotation(JavaXAnnotations.max(parameter.maximum.toInt()))
        if (parameter.typeInfo.isComplexType) this.addAnnotation(JavaXAnnotations.validBuilder().build())
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

            if (parameter.defaultValue != null)
                it.addMember("defaultValue = %S", parameter.defaultValue)

            if (parameter.typeInfo is KotlinTypeInfo.Date)
                this.addAnnotation(
                    AnnotationSpec
                        .builder(MicronautImports.DATE_TIME_FORMAT)
                        .addMember("iso = %L", MicronautImports.DateTimeFormat.ISO_DATE)
                        .build()
                )
            else if (parameter.typeInfo is KotlinTypeInfo.DateTime)
                this.addAnnotation(
                    AnnotationSpec
                        .builder(MicronautImports.DATE_TIME_FORMAT)
                        .addMember("iso = %L", MicronautImports.DateTimeFormat.ISO_DATE_TIME)
                        .build()
                )

            this.addAnnotation(it.build())
        }
}

data class MicronautControllers(val controllers: Collection<ControllerType>) : KotlinTypes(controllers) {
    override val files: Collection<FileSpec> = super.files.map {
        it.toBuilder()
            /*.addImport(MicronautImports.Static.REQUEST_METHOD.first, MicronautImports.Static.REQUEST_METHOD.second)
            .addImport(
                MicronautImports.Static.RESPONSE_STATUS.first,
                MicronautImports.Static.RESPONSE_STATUS.second
            )*/
            .build()
    }
}