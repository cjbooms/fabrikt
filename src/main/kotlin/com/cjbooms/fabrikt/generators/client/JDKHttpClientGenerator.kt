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
    override fun generate(options: Set<ClientCodeGenOptionType>): Clients {
        TODO("Not yet implemented")
    }

    override fun generateLibrary(options: Set<ClientCodeGenOptionType>): Collection<GeneratedFile> {
        TODO("Not yet implemented")
    }
}