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
    val oneOneOf: PolymorphicSuperTypeOne? = null,
    @param:JsonProperty("many_one_of")
    @get:JsonProperty("many_one_of")
    @get:Valid
    val manyOneOf: List<PolymorphicSuperTypeOne>? = null
)

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "shared",
    visible = true
)
@JsonSubTypes(
    JsonSubTypes.Type(
        value = PolymorphicTypeOneA::class,
        name =
        "PolymorphicTypeOneA"
    ),
    JsonSubTypes.Type(
        value = PolymorphicTypeOneB::class,
        name =
        "PolymorphicTypeOneB"
    )
)
sealed class PolymorphicSuperTypeOne() {
    abstract val shared: String
}

data class PolymorphicTypeOneA(
    @param:JsonProperty("whateverA")
    @get:JsonProperty("whateverA")
    val whateverA: String? = null
) : PolymorphicSuperTypeOne() {
    @get:JsonProperty("shared")
    @get:NotNull
    override val shared: String = "PolymorphicTypeOneA"
}

data class PolymorphicTypeOneB(
    @param:JsonProperty("whateverB")
    @get:JsonProperty("whateverB")
    val whateverB: Int? = null
) : PolymorphicSuperTypeOne() {
    @get:JsonProperty("shared")
    @get:NotNull
    override val shared: String = "PolymorphicTypeOneB"
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "shared",
    visible = true
)
@JsonSubTypes(
    JsonSubTypes.Type(
        value = PolymorphicTypeTwoA::class,
        name =
        "PolymorphicTypeTwoA"
    ),
    JsonSubTypes.Type(
        value = PolymorphicTypeTwoB::class,
        name =
        "PolymorphicTypeTwoB"
    )
)
sealed class PolymorphicSuperTypeTwo() {
    abstract val shared: String
}

data class PolymorphicTypeTwoA(
    @param:JsonProperty("whateverC")
    @get:JsonProperty("whateverC")
    val whateverC: String? = null
) : PolymorphicSuperTypeTwo() {
    @get:JsonProperty("shared")
    @get:NotNull
    override val shared: String = "PolymorphicTypeTwoA"
}

data class PolymorphicTypeTwoB(
    @param:JsonProperty("whateverD")
    @get:JsonProperty("whateverD")
    val whateverD: Int? = null
) : PolymorphicSuperTypeTwo() {
    @get:JsonProperty("shared")
    @get:NotNull
    override val shared: String = "PolymorphicTypeTwoB"
}
