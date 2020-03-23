package examples.polymorphicModels.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import javax.validation.constraints.NotNull
import kotlin.Int
import kotlin.String

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "generation",
    visible = true
)
@JsonSubTypes(
    JsonSubTypes.Type(
        value = PolymorphicTypeOne::class,
        name =
            "PolymorphicTypeOne"
    ),
    JsonSubTypes.Type(
        value = PolymorphicTypeTwo::class,
        name =
            "PolymorphicTypeTwo"
    )
)
sealed class PolymorphicSuperType(
    open val firstName: String,
    open val lastName: String
) {
    abstract val generation: String
}

data class PolymorphicTypeOne(
    @param:JsonProperty("first_name")
    @get:JsonProperty("first_name")
    @get:NotNull
    override val firstName: String,
    @param:JsonProperty("last_name")
    @get:JsonProperty("last_name")
    @get:NotNull
    override val lastName: String,
    @param:JsonProperty("child_one_name")
    @get:JsonProperty("child_one_name")
    val childOneName: String? = null
) : PolymorphicSuperType(firstName, lastName) {
    @get:JsonProperty("generation")
    @get:NotNull
    override val generation: String = "PolymorphicTypeOne"
}

data class PolymorphicTypeTwo(
    @param:JsonProperty("first_name")
    @get:JsonProperty("first_name")
    @get:NotNull
    override val firstName: String,
    @param:JsonProperty("last_name")
    @get:JsonProperty("last_name")
    @get:NotNull
    override val lastName: String,
    @param:JsonProperty("child_two_age")
    @get:JsonProperty("child_two_age")
    val childTwoAge: Int? = null
) : PolymorphicSuperType(firstName, lastName) {
    @get:JsonProperty("generation")
    @get:NotNull
    override val generation: String = "PolymorphicTypeTwo"
}
