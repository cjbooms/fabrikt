package com.cjbooms.fabrikt.generators

import com.cjbooms.fabrikt.cli.ClientCodeGenOptionType
import com.cjbooms.fabrikt.configurations.PackagesConfig
import com.cjbooms.fabrikt.model.Clients
import com.cjbooms.fabrikt.model.GeneratedFile
import com.cjbooms.fabrikt.model.SourceApi

class OkHttpClientGenerator(
    config: PackagesConfig,
    api: SourceApi
) {
    private val simpleClientGenerator = OkHttpSimpleClientGenerator(config, api)
    private val enhancedClientGenerator = OkHttpEnhancedClientGenerator(config, api)

    fun generate(options: Set<ClientCodeGenOptionType>): Clients {
        val simpleClient = simpleClientGenerator.generateDynamicClientCode()
        val enhancedClient = enhancedClientGenerator.generateDynamicClientCode(options)

        return Clients(enhancedClient.plus(simpleClient).toSet())
    }

    fun generateLibrary(options: Set<ClientCodeGenOptionType>): Collection<GeneratedFile> {
        val simpleClientLibrary = simpleClientGenerator.generateLibrary()
        val enhancedClientLibrary = enhancedClientGenerator.generateLibrary(options)

        return simpleClientLibrary.plus(enhancedClientLibrary)
    }
}
