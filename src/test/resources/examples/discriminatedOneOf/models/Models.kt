package examples.discriminatedOneOf.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import com.fasterxml.jackson.`annotation`.JsonSubTypes
import com.fasterxml.jackson.`annotation`.JsonTypeInfo
import com.fasterxml.jackson.`annotation`.JsonValue
import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.String
import kotlin.collections.Map

public data class SomeObj(
    @param:JsonProperty("state")
    @get:JsonProperty("state")
    @get:NotNull
    @get:Valid
    public val state: State,
)

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "status",
    visible = true,
)
@JsonSubTypes(
    JsonSubTypes.Type(value = StateA::class, name = "a"),
    JsonSubTypes.Type(
        value =
        StateB::class,
        name = "b",
    ),
)
public sealed interface State

public data class StateA(
    @get:JsonProperty("status")
    @get:NotNull
    @param:JsonProperty("status")
    public val status: Status = Status.A,
) : State

public data class StateB(
    @get:JsonProperty("mode")
    @get:NotNull
    public val mode: StateBMode,
    @get:JsonProperty("status")
    @get:NotNull
    @param:JsonProperty("status")
    public val status: Status = Status.B,
) : State

public enum class StateBMode(
    @JsonValue
    public val `value`: String,
) {
    MODE1("mode1"),
    MODE2("mode2"),
    ;

    public companion object {
        private val mapping: Map<String, StateBMode> = values().associateBy(StateBMode::value)

        public fun fromValue(`value`: String): StateBMode? = mapping[value]
    }
}

public enum class Status(
    @JsonValue
    public val `value`: String,
) {
    A("a"),
    B("b"),
    ;

    public companion object {
        private val mapping: Map<String, Status> = values().associateBy(Status::value)

        public fun fromValue(`value`: String): Status? = mapping[value]
    }
}
