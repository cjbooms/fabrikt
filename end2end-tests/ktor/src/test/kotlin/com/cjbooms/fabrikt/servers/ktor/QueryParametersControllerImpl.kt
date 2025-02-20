package com.cjbooms.fabrikt.servers.ktor

import com.example.controllers.QueryParamsController
import com.example.controllers.TypedApplicationCall
import com.example.models.EnumQueryParam
import com.example.models.QueryParamsResult
import io.mockk.CapturingSlot

class QueryParametersControllerImpl(
    private val nameCapturingSlot: CapturingSlot<String?>,
    private val orderCapturingSlot: CapturingSlot<EnumQueryParam?>,
): QueryParamsController {
    override suspend fun get(name: String, order: EnumQueryParam?, call: TypedApplicationCall<QueryParamsResult>) {
        nameCapturingSlot.captured = name
        orderCapturingSlot.captured = order
        call.respondTyped(QueryParamsResult("ok"))
    }
}