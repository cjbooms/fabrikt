package examples.oneOfPolymorphicModels.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import com.fasterxml.jackson.`annotation`.JsonSubTypes
import com.fasterxml.jackson.`annotation`.JsonTypeInfo
import com.fasterxml.jackson.`annotation`.JsonValue
import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map

public data class ChildTypeA(
    @param:JsonProperty("some_string")
    @get:JsonProperty("some_string")
    @get:NotNull
    public val someString: String,
    @get:JsonProperty("type")
    @get:NotNull
    @param:JsonProperty("type")
    public override val type: ParentType = ParentType.CHILD_TYPE_A,
) : ParentSpec()

public data class ChildTypeB(
    @param:JsonProperty("some_int")
    @get:JsonProperty("some_int")
    @get:NotNull
    public val someInt: Int,
    @get:JsonProperty("type")
    @get:NotNull
    @param:JsonProperty("type")
    public override val type: ParentType = ParentType.CHILD_TYPE_B,
) : ParentSpec()

public data class ContainsOneOfPolymorphicTypes(
    @param:JsonProperty("one_one_of")
    @get:JsonProperty("one_one_of")
    @get:Valid
    public val oneOneOf: PolymorphicSuperTypeOne? = null,
    @param:JsonProperty("many_one_of")
    @get:JsonProperty("many_one_of")
    @get:Valid
    public val manyOneOf: List<PolymorphicSuperTypeOne>? = null,
)

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true,
)
@JsonSubTypes(
    JsonSubTypes.Type(
        value = ChildTypeA::class,
        name =
        "CHILD_TYPE_A",
    ),
    JsonSubTypes.Type(value = ChildTypeB::class, name = "CHILD_TYPE_B"),
)
public sealed class ParentSpec() {
    public abstract val type: ParentType
}

public enum class ParentType(
    @JsonValue
    public val `value`: String,
) {
    CHILD_TYPE_A("CHILD_TYPE_A"),
    CHILD_TYPE_B("CHILD_TYPE_B"),
    ;

    public companion object {
        private val mapping: Map<String, ParentType> = values().associateBy(ParentType::value)

        public fun fromValue(`value`: String): ParentType? = mapping[value]
    }
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "shared",
    visible = true,
)
@JsonSubTypes(
    JsonSubTypes.Type(
        value = PolymorphicTypeOneA::class,
        name =
        "PolymorphicTypeOneA",
    ),
    JsonSubTypes.Type(
        value = PolymorphicTypeOneB::class,
        name =
        "PolymorphicTypeOneB",
    ),
)
public sealed class PolymorphicSuperTypeOne() {
    public abstract val shared: String
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "shared",
    visible = true,
)
@JsonSubTypes(
    JsonSubTypes.Type(
        value = PolymorphicTypeTwoA::class,
        name =
        "PolymorphicTypeTwoA",
    ),
    JsonSubTypes.Type(
        value = PolymorphicTypeTwoB::class,
        name =
        "PolymorphicTypeTwoB",
    ),
)
public sealed class PolymorphicSuperTypeTwo() {
    public abstract val shared: String
}

public data class PolymorphicTypeOneA(
    @param:JsonProperty("whateverA")
    @get:JsonProperty("whateverA")
    public val whateverA: String? = null,
    @get:JsonProperty("shared")
    @get:NotNull
    @param:JsonProperty("shared")
    public override val shared: String = "PolymorphicTypeOneA",
) : PolymorphicSuperTypeOne()

public data class PolymorphicTypeOneB(
    @param:JsonProperty("whateverB")
    @get:JsonProperty("whateverB")
    public val whateverB: Int? = null,
    @get:JsonProperty("shared")
    @get:NotNull
    @param:JsonProperty("shared")
    public override val shared: String = "PolymorphicTypeOneB",
) : PolymorphicSuperTypeOne()

public data class PolymorphicTypeTwoA(
    @param:JsonProperty("whateverC")
    @get:JsonProperty("whateverC")
    public val whateverC: String? = null,
    @get:JsonProperty("shared")
    @get:NotNull
    @param:JsonProperty("shared")
    public override val shared: String = "PolymorphicTypeTwoA",
) : PolymorphicSuperTypeTwo()

public data class PolymorphicTypeTwoB(
    @param:JsonProperty("whateverD")
    @get:JsonProperty("whateverD")
    public val whateverD: Int? = null,
    @get:JsonProperty("shared")
    @get:NotNull
    @param:JsonProperty("shared")
    public override val shared: String = "PolymorphicTypeTwoB",
) : PolymorphicSuperTypeTwo()
