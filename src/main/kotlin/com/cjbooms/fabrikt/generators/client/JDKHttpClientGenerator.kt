package com.cjbooms.fabrikt.generators.client

import com.cjbooms.fabrikt.cli.ClientCodeGenOptionType
import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.model.Clients
import com.cjbooms.fabrikt.model.GeneratedFile
import com.cjbooms.fabrikt.model.SourceApi
import java.nio.file.Path

class JDKHttpClientGenerator(
    packages: Packages,
    api: SourceApi,
    srcPath: Path,
) : ClientGenerator {
    private val simpleClientGenerator = JDKHttpSimpleClientGenerator(packages, api, srcPath)

    override fun generate(options: Set<ClientCodeGenOptionType>): Clients {
        val simpleClient = simpleClientGenerator.generateDynamicClientCode()

        return Clients(simpleClient)
    }

    override fun generateLibrary(options: Set<ClientCodeGenOptionType>): Collection<GeneratedFile> {

        return simpleClientGenerator.generateLibrary()
    }
}