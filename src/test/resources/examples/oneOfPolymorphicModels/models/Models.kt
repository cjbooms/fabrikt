package examples.oneOfPolymorphicModels.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.Int
import kotlin.String
import kotlin.collections.List

data class ContainsOneOfPolymorphicTypes(
    @param:JsonProperty("one_one_of")
    @get:JsonProperty("one_one_of")
    @get:Valid
    val oneOneOf: PolySuperType? = null,
    @param:JsonProperty("many_one_of")
    @get:JsonProperty("many_one_of")
    @get:Valid
    val manyOneOf: List<PolySuperType>? = null
)

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "generation",
    visible = true
)
@JsonSubTypes(
    JsonSubTypes.Type(
        value = PolyTypeOne::class,
        name =
            "PolyTypeOne"
    ),
    JsonSubTypes.Type(value = PolyTypeTwo::class, name = "PolyTypeTwo")
)
sealed class PolySuperType(
    open val firstName: String,
    open val lastName: String
) {
    abstract val generation: String
}

data class PolyTypeOne(
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
) : PolySuperType(firstName, lastName) {
    @get:JsonProperty("generation")
    @get:NotNull
    override val generation: String = "PolyTypeOne"
}

data class PolyTypeTwo(
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
) : PolySuperType(firstName, lastName) {
    @get:JsonProperty("generation")
    @get:NotNull
    override val generation: String = "PolyTypeTwo"
}
