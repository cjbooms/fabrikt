package examples.githubApi.service

import examples.githubApi.models.BulkEntityDetails
import examples.githubApi.models.Contributor
import examples.githubApi.models.ContributorQueryResult
import examples.githubApi.models.EventResults
import examples.githubApi.models.Organisation
import examples.githubApi.models.OrganisationQueryResult
import examples.githubApi.models.PullRequest
import examples.githubApi.models.PullRequestQueryResult
import examples.githubApi.models.Repository
import examples.githubApi.models.RepositoryQueryResult
import examples.githubApi.models.StatusQueryParam
import java.net.URI
import kotlin.Boolean
import kotlin.Int
import kotlin.Pair
import kotlin.String
import kotlin.collections.List

interface InternalEventsService {
    fun create(bulkEntityDetails: BulkEntityDetails): Pair<URI, EventResults?>
}

interface ContributorsService {
    fun query(
        limit: Int,
        xFlowId: String?,
        includeInactive: Boolean?,
        cursor: String?
    ): ContributorQueryResult

    fun create(
        contributor: Contributor,
        xFlowId: String?,
        idempotencyKey: String?
    ): Pair<URI, Contributor?>

    fun read(
        id: String,
        status: StatusQueryParam,
        xFlowId: String?,
        ifNoneMatch: String?
    ): Contributor

    fun update(
        contributor: Contributor,
        id: String,
        ifMatch: String,
        xFlowId: String?,
        idempotencyKey: String?
    ): Contributor
}

interface OrganisationsService {
    fun query(
        limit: Int,
        xFlowId: String?,
        includeInactive: Boolean?,
        cursor: String?
    ): OrganisationQueryResult

    fun create(
        organisation: Organisation,
        xFlowId: String?,
        idempotencyKey: String?
    ): Pair<URI, Organisation?>

    fun read(
        id: String,
        status: StatusQueryParam,
        xFlowId: String?,
        ifNoneMatch: String?
    ): Organisation

    fun update(
        organisation: Organisation,
        id: String,
        ifMatch: String,
        xFlowId: String?,
        idempotencyKey: String?
    ): Organisation
}

interface OrganisationsContributorsService {
    fun query(
        parentId: String,
        limit: Int,
        xFlowId: String?,
        includeInactive: Boolean?,
        cursor: String?
    ): ContributorQueryResult

    fun read(
        parentId: String,
        id: String,
        xFlowId: String?,
        ifNoneMatch: String?
    ): Contributor

    fun addSubresource(
        parentId: String,
        id: String,
        ifMatch: String,
        xFlowId: String?,
        idempotencyKey: String?
    )

    fun removeSubresource(
        parentId: String,
        id: String,
        xFlowId: String?
    )
}

interface RepositoriesService {
    fun query(
        limit: Int,
        xFlowId: String?,
        slug: List<String>?,
        name: List<String>?,
        includeInactive: Boolean?,
        cursor: String?
    ): RepositoryQueryResult

    fun create(
        repository: Repository,
        xFlowId: String?,
        idempotencyKey: String?
    ): Pair<URI, Repository?>

    fun read(
        id: String,
        status: StatusQueryParam,
        xFlowId: String?,
        ifNoneMatch: String?
    ): Repository

    fun update(
        repository: Repository,
        id: String,
        ifMatch: String,
        xFlowId: String?,
        idempotencyKey: String?
    ): Repository
}

interface RepositoriesPullRequestsService {
    fun query(
        parentId: String,
        limit: Int,
        xFlowId: String?,
        includeInactive: Boolean?,
        cursor: String?
    ): PullRequestQueryResult

    fun create(
        pullRequest: PullRequest,
        parentId: String,
        xFlowId: String?,
        idempotencyKey: String?
    ): Pair<URI, PullRequest?>

    fun read(
        parentId: String,
        id: String,
        xFlowId: String?,
        ifNoneMatch: String?
    ): PullRequest

    fun update(
        pullRequest: PullRequest,
        parentId: String,
        id: String,
        ifMatch: String,
        xFlowId: String?,
        idempotencyKey: String?
    ): PullRequest
}
