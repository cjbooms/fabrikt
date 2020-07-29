package examples.putApi.service

import examples.putApi.models.Contributor
import kotlin.String

interface ContributorsService {
    fun update(
        contributor: Contributor,
        id: String,
        ifMatch: String
    ): Contributor
}
