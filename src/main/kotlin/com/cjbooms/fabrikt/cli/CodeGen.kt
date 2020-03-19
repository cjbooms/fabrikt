package com.cjbooms.fabrikt.cli

import com.beust.jcommander.ParameterException
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
            codeGenArgs.clientOptions
        )
    }

    private fun generate(
        basePackage: String,
        pathToApi: Path,
        outputDir: Path,
        codeGenTypes: Set<CodeGenerationType>,
        apiFragments: List<String> = emptyList(),
        clientOptions: Set<ClientCodeGenOptionType>
    ) {
        val suppliedApi = pathToApi.toFile().readText()
        val baseDir = pathToApi.parent

        logger.info("Generating code and dumping to $outputDir/")

        val sourceApi = SourceApi.create(
                        baseApi = suppliedApi,
                        apiFragments = apiFragments,
                        baseDir = baseDir
                )

        CodeGenerator.generate(
                basePackage,
                sourceApi,
                codeGenTypes,
                clientOptions
        ).forEach { it.writeFileTo(outputDir.toFile()) }
    }
}
