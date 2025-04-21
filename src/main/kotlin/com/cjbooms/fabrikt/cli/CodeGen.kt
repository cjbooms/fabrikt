package com.cjbooms.fabrikt.cli

import com.beust.jcommander.ParameterException
import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.generators.MutableSettings
import com.cjbooms.fabrikt.model.SourceApi
import java.nio.file.Files
import java.nio.file.Path
import java.util.logging.Logger

object CodeGen {

    private val logger = Logger.getGlobal()

    @JvmStatic
    fun main(args: Array<String>) {
        val codeGenArgs = CodeGenArgs.parse(args)

        if (Files.notExists(codeGenArgs.apiFile))
            throw ParameterException("Could not find api file '${codeGenArgs.apiFile}', Specify its location with the -a option. Use --help for further information.")

        generate(
            codeGenArgs.basePackage,
            codeGenArgs.apiFile.toAbsolutePath(),
            codeGenArgs.outputDirectory,
            codeGenArgs.targets,
            codeGenArgs.apiFragments.map { it.toFile().readText() },
            codeGenArgs.controllerOptions,
            codeGenArgs.controllerTarget,
            codeGenArgs.controllerClassAdditionalAnnotations,
            codeGenArgs.controllerMethodAdditionalAnnotations,
            codeGenArgs.modelOptions,
            codeGenArgs.modelSuffix,
            codeGenArgs.modelAdditionalAnnotations,
            codeGenArgs.clientOptions,
            codeGenArgs.clientTarget,
            codeGenArgs.clientClassAdditionalAnnotations,
            codeGenArgs.clientMethodAdditionalAnnotations,
            codeGenArgs.openfeignClientName,
            codeGenArgs.typeOverrides,
            codeGenArgs.srcPath,
            codeGenArgs.resourcesPath,
            codeGenArgs.validationLibrary,
            codeGenArgs.externalRefResolutionMode,
            codeGenArgs.serializationLibrary,
        )
    }

    private fun generate(
        basePackage: String,
        pathToApi: Path,
        outputDir: Path,
        codeGenTypes: Set<CodeGenerationType>,
        apiFragments: List<String> = emptyList(),
        controllerOptions: Set<ControllerCodeGenOptionType>,
        controllerTarget: ControllerCodeGenTargetType,
        controllerClassAdditionalAnnotations: List<String> = emptyList(),
        controllerMethodAdditionalAnnotations: List<String> = emptyList(),
        modelOptions: Set<ModelCodeGenOptionType>,
        modelSuffix: String,
        modelAnnotations: List<String> = emptyList(),
        clientOptions: Set<ClientCodeGenOptionType>,
        clientTarget: ClientCodeGenTargetType,
        clientClassAdditionalAnnotations: List<String> = emptyList(),
        clientMethodAdditionalAnnotations: List<String> = emptyList(),
        openfeignClientName: String,
        typeOverrides: Set<CodeGenTypeOverride>,
        srcPath: Path,
        resourcesPath: Path,
        validationLibrary: ValidationLibrary,
        externalRefResolutionMode: ExternalReferencesResolutionMode,
        serializationLibrary: SerializationLibrary,
    ) {
        MutableSettings.updateSettings(
            codeGenTypes,
            controllerOptions,
            controllerTarget,
            controllerClassAdditionalAnnotations,
            controllerMethodAdditionalAnnotations,
            modelOptions,
            modelSuffix,
            modelAnnotations,
            clientOptions,
            clientTarget,
            clientClassAdditionalAnnotations,
            clientMethodAdditionalAnnotations,
            openfeignClientName,
            typeOverrides,
            validationLibrary,
            externalRefResolutionMode,
            serializationLibrary,
        )

        val suppliedApi = pathToApi.toFile().readText()
        val baseDir = pathToApi.parent

        logger.info("Generating code and dumping to $outputDir/")

        val packages = Packages(basePackage)
        val sourceApi = SourceApi.create(suppliedApi, apiFragments, baseDir)
        val generator = CodeGenerator(packages, sourceApi, srcPath, resourcesPath)
        generator.generate().forEach { it.writeFileTo(outputDir.toFile()) }
    }
}
