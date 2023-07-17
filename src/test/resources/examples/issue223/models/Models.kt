package examples.issue223.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonValue
import java.util.UUID
import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.Boolean
import kotlin.String
import kotlin.collections.Map

data class CreateCow(
    @param:JsonProperty("doesTip")
    @get:JsonProperty("doesTip")
    val doesTip: Boolean? = null,
    @get:JsonProperty("mammalType")
    @get:NotNull
    @param:JsonProperty("mammalType")
    override val mammalType: MammalType = MammalType.COW
) : CreateMammal()

data class CreateDog(
    @param:JsonProperty("doesBark")
    @get:JsonProperty("doesBark")
    val doesBark: Boolean? = null,
    @get:JsonProperty("mammalType")
    @get:NotNull
    @param:JsonProperty("mammalType")
    override val mammalType: MammalType = MammalType.DOG
) : CreateMammal()

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "mammalType",
    visible = true
)
@JsonSubTypes(
    JsonSubTypes.Type(value = CreateDog::class, name = "DOG"),
    JsonSubTypes.Type(
        value =
        CreateCow::class,
        name = "COW"
    )
)
sealed class CreateMammal() {
    abstract val mammalType: MammalType
}

data class CreateMammalRequest(
    @param:JsonProperty("createMammal")
    @get:JsonProperty("createMammal")
    @get:NotNull
    @get:Valid
    val createMammal: CreateMammal,
    @param:JsonProperty("requestId")
    @get:JsonProperty("requestId")
    @get:NotNull
    val requestId: UUID
)

data class MammalCreateFailure(
    @param:JsonProperty("error")
    @get:JsonProperty("error")
    @get:NotNull
    val error: String
)

data class MammalCreated(
    @param:JsonProperty("message")
    @get:JsonProperty("message")
    @get:NotNull
    val message: String
)

enum class MammalType(
    @JsonValue
    val value: String
) {
    DOG("DOG"),

    COW("COW");

    companion object {
        private val mapping: Map<String, MammalType> = values().associateBy(MammalType::value)

        fun fromValue(value: String): MammalType? = mapping[value]
    }
}
