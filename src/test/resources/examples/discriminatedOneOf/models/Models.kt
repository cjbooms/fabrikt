package examples.discriminatedOneOf.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonValue
import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.String
import kotlin.collections.Map

data class SomeObj(
    @param:JsonProperty("state")
    @get:JsonProperty("state")
    @get:NotNull
    @get:Valid
    val state: State
)

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "status",
    visible = true
)
@JsonSubTypes(
    JsonSubTypes.Type(value = StateA::class, name = "a"),
    JsonSubTypes.Type(
        value =
        StateB::class,
        name = "b"
    )
)
sealed interface State

object StateA : State

object StateB : State

enum class Status(
    @JsonValue
    val value: String
) {
    A("a"),

    B("b");

    companion object {
        private val mapping: Map<String, Status> = values().associateBy(Status::value)

        fun fromValue(value: String): Status? = mapping[value]
    }
}
