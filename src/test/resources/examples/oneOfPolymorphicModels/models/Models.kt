package examples.oneOfPolymorphicModels.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonValue
import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map

data class ChildTypeA(
    @param:JsonProperty("some_string")
    @get:JsonProperty("some_string")
    @get:NotNull
    val someString: String,
    @get:JsonProperty("type")
    @get:NotNull
    override val type: ParentType = ParentType.CHILD_TYPE_A
) : ParentSpec()

data class ChildTypeB(
    @param:JsonProperty("some_int")
    @get:JsonProperty("some_int")
    @get:NotNull
    val someInt: Int,
    @get:JsonProperty("type")
    @get:NotNull
    override val type: ParentType = ParentType.CHILD_TYPE_B
) : ParentSpec()

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
    property = "type",
    visible = true
)
@JsonSubTypes(
    JsonSubTypes.Type(
        value = ChildTypeA::class,
        name =
        "CHILD_TYPE_A"
    ),
    JsonSubTypes.Type(value = ChildTypeB::class, name = "CHILD_TYPE_B")
)
sealed class ParentSpec() {
    abstract val type: ParentType
}

enum class ParentType(
    @JsonValue
    val value: String
) {
    CHILD_TYPE_A("CHILD_TYPE_A"),

    CHILD_TYPE_B("CHILD_TYPE_B");

    companion object {
        private val mapping: Map<String, ParentType> = values().associateBy(ParentType::value)

        fun fromValue(value: String): ParentType? = mapping[value]
    }
}

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

data class PolymorphicTypeOneA(
    @param:JsonProperty("whateverA")
    @get:JsonProperty("whateverA")
    val whateverA: String? = null,
    @get:JsonProperty("shared")
    @get:NotNull
    override val shared: String = "PolymorphicTypeOneA"
) : PolymorphicSuperTypeOne()

data class PolymorphicTypeOneB(
    @param:JsonProperty("whateverB")
    @get:JsonProperty("whateverB")
    val whateverB: Int? = null,
    @get:JsonProperty("shared")
    @get:NotNull
    override val shared: String = "PolymorphicTypeOneB"
) : PolymorphicSuperTypeOne()

data class PolymorphicTypeTwoA(
    @param:JsonProperty("whateverC")
    @get:JsonProperty("whateverC")
    val whateverC: String? = null,
    @get:JsonProperty("shared")
    @get:NotNull
    override val shared: String = "PolymorphicTypeTwoA"
) : PolymorphicSuperTypeTwo()

data class PolymorphicTypeTwoB(
    @param:JsonProperty("whateverD")
    @get:JsonProperty("whateverD")
    val whateverD: Int? = null,
    @get:JsonProperty("shared")
    @get:NotNull
    override val shared: String = "PolymorphicTypeTwoB"
) : PolymorphicSuperTypeTwo()
