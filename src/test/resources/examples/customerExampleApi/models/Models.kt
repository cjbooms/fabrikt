package examples.customerExampleApi.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import java.time.OffsetDateTime
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.collections.List

data class DeleteRequestQueryResult(
    @param:JsonProperty("prev")
    @get:JsonProperty("prev")
    val prev: String? = null,
    @param:JsonProperty("next")
    @get:JsonProperty("next")
    val next: String? = null,
    @param:JsonProperty("items")
    @get:JsonProperty("items")
    @get:NotNull
    @get:Size(min = 0)
    @get:Valid
    val items: List<DeleteRequest>
)

data class DataAccessRequestQueryResult(
    @param:JsonProperty("prev")
    @get:JsonProperty("prev")
    val prev: String? = null,
    @param:JsonProperty("next")
    @get:JsonProperty("next")
    val next: String? = null,
    @param:JsonProperty("items")
    @get:JsonProperty("items")
    @get:NotNull
    @get:Size(min = 0)
    @get:Valid
    val items: List<DataAccessRequest>
)

data class Customer(
    @param:JsonProperty("is_eligible")
    @get:JsonProperty("is_eligible")
    val isEligible: Boolean? = null,
    @param:JsonProperty("eligibility_results")
    @get:JsonProperty("eligibility_results")
    @get:Valid
    val eligibilityResults: List<EligibilityRule>? = null
)

data class DeleteRequest(
    @param:JsonProperty("id")
    @get:JsonProperty("id")
    val id: String? = null,
    @param:JsonProperty("customer_number")
    @get:JsonProperty("customer_number")
    val customerNumber: Int? = null,
    @param:JsonProperty("status")
    @get:JsonProperty("status")
    val status: RequestStatus? = null,
    @param:JsonProperty("expiry_date")
    @get:JsonProperty("expiry_date")
    val expiryDate: OffsetDateTime? = null,
    @param:JsonProperty("created")
    @get:JsonProperty("created")
    val created: OffsetDateTime? = null,
    @param:JsonProperty("modified")
    @get:JsonProperty("modified")
    val modified: OffsetDateTime? = null,
    @param:JsonProperty("created_by_uid")
    @get:JsonProperty("created_by_uid")
    val createdByUid: String? = null,
    @param:JsonProperty("modified_by_uid")
    @get:JsonProperty("modified_by_uid")
    val modifiedByUid: String? = null
)

enum class RequestStatus(
    @JsonValue
    val value: String
) {
    OPEN("OPEN"),

    IN_PROGRESS("IN_PROGRESS"),

    DONE("DONE"),

    CANCELLED("CANCELLED"),

    ERROR("ERROR");
}

data class DataAccessRequest(
    @param:JsonProperty("id")
    @get:JsonProperty("id")
    val id: String? = null,
    @param:JsonProperty("customer_number")
    @get:JsonProperty("customer_number")
    val customerNumber: Int? = null,
    @param:JsonProperty("status")
    @get:JsonProperty("status")
    val status: RequestStatus? = null,
    @param:JsonProperty("expiry_date")
    @get:JsonProperty("expiry_date")
    val expiryDate: OffsetDateTime? = null,
    @param:JsonProperty("created")
    @get:JsonProperty("created")
    val created: OffsetDateTime? = null,
    @param:JsonProperty("modified")
    @get:JsonProperty("modified")
    val modified: OffsetDateTime? = null,
    @param:JsonProperty("created_by_uid")
    @get:JsonProperty("created_by_uid")
    val createdByUid: String? = null,
    @param:JsonProperty("modified_by_uid")
    @get:JsonProperty("modified_by_uid")
    val modifiedByUid: String? = null
)

data class EligibilityRule(
    @param:JsonProperty("code")
    @get:JsonProperty("code")
    val code: String? = null,
    @param:JsonProperty("message")
    @get:JsonProperty("message")
    val message: String? = null,
    @param:JsonProperty("status")
    @get:JsonProperty("status")
    val status: EligibilityRuleStatus? = null
)

enum class EligibilityRuleStatus(
    @JsonValue
    val value: String
) {
    FAIL("fail"),

    PASS("pass");
}
