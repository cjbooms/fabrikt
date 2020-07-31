package com.cjbooms.fabrikt.util

import com.cjbooms.fabrikt.model.SourceApi
import com.cjbooms.fabrikt.util.KaizenParserExtensions.getResourceName
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isSingleResource
import com.cjbooms.fabrikt.util.KaizenParserExtensions.safeName
import com.reprezen.kaizen.oasparser.model3.Operation
import com.reprezen.kaizen.oasparser.model3.Path

enum class SupportedOperation {
    // Top level resources
    CREATE,
    READ,
    UPDATE,
    QUERY,
    // Subresource which is not a top level resource
    CREATE_SUBRESOURCE,
    QUERY_SUBRESOURCE,
    UPDATE_SUBRESOURCE,
    READ_SUBRESOURCE,
    // Subresource which is a top level resource
    ADD_TOP_LEVEL_SUBRESOURCE,
    READ_TOP_LEVEL_SUBRESOURCE,
    QUERY_TOP_LEVEL_SUBRESOURCE,
    REMOVE_TOP_LEVEL_SUBRESOURCE,

    // Fallback operations when we can't understand what's happening with the API
    DEFAULT_GET,
    DEFAULT_PUT,
    DEFAULT_POST,
    DEFAULT_DELETE
}

object OperationUtils {

    private fun isTopLevelResource(path: Path): Boolean =
        path.pathString.split("/").filterNot { it.isEmpty() }.count() <= 2

    private fun isTopLevelSubResource(path: Path, api: SourceApi): Boolean =
        !isTopLevelResource(path) && isAlsoExposedAsTopLevelResource(
            path,
            api
        )

    private fun isNonTopLevelSubresource(path: Path, api: SourceApi): Boolean =
        !isTopLevelResource(path) && !isAlsoExposedAsTopLevelResource(
            path,
            api
        )

    private fun isAlsoExposedAsTopLevelResource(path: Path, api: SourceApi): Boolean {
        val resourceName = path.getResourceName()
        val indexInPath = path.pathString.split("/").indexOf(resourceName)
        return api.openApi3.paths
            .map { it.key }
            .filterNot { it == path.pathString }
            .map { it.split("/") }
            .any {
                val indexInDifferentPath = it.indexOf(resourceName)
                indexInDifferentPath > -1 && indexInDifferentPath < indexInPath
            }
    }

    private fun isGet(verb: String) = "GET" == verb.toUpperCase()
    private fun isPost(verb: String) = "POST" == verb.toUpperCase()
    private fun isPut(verb: String) = "PUT" == verb.toUpperCase()
    private fun isHead(verb: String) = "HEAD" == verb.toUpperCase()
    private fun isDelete(verb: String) = "DELETE" == verb.toUpperCase()

    // Top level resources
    private fun isRead(verb: String, path: Path): Boolean =
        isTopLevelResource(path) && path.isSingleResource() && isGet(
            verb
        )

    private fun isQuery(verb: String, path: Path, operation: Operation): Boolean =
        isTopLevelResource(path) && !path.isSingleResource() && isGet(
            verb
        ) && operation.isListResponseType()

    fun Operation.isListResponseType(): Boolean = maybeListResponseType() != null

    fun Operation.maybeListResponseType(): String? = responses
        .filter { it.key != "default" }
        .flatMap { resp -> resp.value.contentMediaTypes.map { it.value.schema.properties["items"]?.itemsSchema?.safeName() } }
        .asSequence()
        .filterNotNull()
        .firstOrNull()

    private fun isCreate(verb: String, path: Path): Boolean =
        isTopLevelResource(path) && !path.isSingleResource() && isPost(
            verb
        )

    private fun isUpdate(verb: String, path: Path): Boolean =
        isTopLevelResource(path) && path.isSingleResource() && isPut(
            verb
        )

    // Subresource which is not a top level resource

    private fun isReadSubresource(verb: String, path: Path, api: SourceApi): Boolean =
        isNonTopLevelSubresource(
            path,
            api
        ) && path.isSingleResource() && isGet(verb)

    private fun isQuerySubresource(verb: String, path: Path, api: SourceApi, operation: Operation): Boolean =
        isNonTopLevelSubresource(
            path,
            api
        ) && !path.isSingleResource() && isGet(verb) && operation.isListResponseType()

    private fun isCreateSubresource(verb: String, path: Path, api: SourceApi): Boolean =
        isNonTopLevelSubresource(
            path,
            api
        ) && !path.isSingleResource() && isPost(verb)

    private fun isUpdateSubresource(verb: String, path: Path, api: SourceApi): Boolean =
        isNonTopLevelSubresource(
            path,
            api
        ) && path.isSingleResource() && isPut(verb)

    // Subresource which is a top level resource

    private fun isReadTopLevelSubresource(verb: String, path: Path, api: SourceApi): Boolean =
        isTopLevelSubResource(
            path,
            api
        ) && path.isSingleResource() && isGet(verb)

    private fun isQueryTopLevelSubresource(verb: String, path: Path, api: SourceApi): Boolean =
        isTopLevelSubResource(
            path,
            api
        ) && !path.isSingleResource() && isGet(verb)

    private fun isRemoveSubresource(verb: String, path: Path, api: SourceApi): Boolean =
        isTopLevelSubResource(
            path,
            api
        ) && path.isSingleResource() && isDelete(verb)

    private fun isAddSubresource(verb: String, path: Path, api: SourceApi): Boolean =
        isTopLevelSubResource(
            path,
            api
        ) && path.isSingleResource() && isPut(verb)

    private fun isHead(verb: String, path: Path): Boolean =
        path.isSingleResource() && isHead(verb)

    fun supportedOperationFrom(verb: String, operation: Operation, path: Path, api: SourceApi): SupportedOperation? {
        return when {
            isRead(
                verb,
                path
            ) -> SupportedOperation.READ
            isHead(
                verb,
                path
            ) -> SupportedOperation.READ
            isQuery(
                verb,
                path,
                operation
            ) -> SupportedOperation.QUERY
            isCreate(
                verb,
                path
            ) -> SupportedOperation.CREATE
            isUpdate(
                verb,
                path
            ) -> SupportedOperation.UPDATE

            isReadSubresource(
                verb,
                path,
                api
            ) -> SupportedOperation.READ_SUBRESOURCE
            isQuerySubresource(
                verb,
                path,
                api,
                operation
            ) -> SupportedOperation.QUERY_SUBRESOURCE
            isCreateSubresource(
                verb,
                path,
                api
            ) -> SupportedOperation.CREATE_SUBRESOURCE
            isUpdateSubresource(
                verb,
                path,
                api
            ) -> SupportedOperation.UPDATE_SUBRESOURCE

            isReadTopLevelSubresource(
                verb,
                path,
                api
            ) -> SupportedOperation.READ_TOP_LEVEL_SUBRESOURCE
            isQueryTopLevelSubresource(
                verb,
                path,
                api
            ) -> SupportedOperation.QUERY_TOP_LEVEL_SUBRESOURCE
            isAddSubresource(
                verb,
                path,
                api
            ) -> SupportedOperation.ADD_TOP_LEVEL_SUBRESOURCE
            isRemoveSubresource(
                verb,
                path,
                api
            ) -> SupportedOperation.REMOVE_TOP_LEVEL_SUBRESOURCE

            else -> when {
                isGet(verb) -> SupportedOperation.DEFAULT_GET
                isHead(verb) -> SupportedOperation.DEFAULT_GET
                isPut(verb) -> SupportedOperation.DEFAULT_PUT
                isPost(verb) -> SupportedOperation.DEFAULT_POST
                isDelete(verb) -> SupportedOperation.DEFAULT_DELETE
                else -> null
            }
        }
    }

    fun isResponseBodyPresent(operation: Operation): Boolean =
        operation.responses["200"] != null
}
