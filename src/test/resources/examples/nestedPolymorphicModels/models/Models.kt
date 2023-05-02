package examples.nestedPolymorphicModels.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonValue
import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.collections.Map

data class CommonObject(
    @param:JsonProperty("filed1")
    @get:JsonProperty("filed1")
    @get:NotNull
    val filed1: String,
    @param:JsonProperty("field2")
    @get:JsonProperty("field2")
    @get:NotNull
    val field2: String
)

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "firstLevelDiscriminator",
    visible = true
)
@JsonSubTypes(
    JsonSubTypes.Type(
        value = SecondLevelChild1::class,
        name =
        "secondLevelChild1"
    ),
    JsonSubTypes.Type(
        value = SecondLevelChild2::class,
        name =
        "secondLevelChild2"
    )
)
sealed class FirstLevelChild(
    @param:JsonProperty("rootField1")
    @get:JsonProperty("rootField1")
    @get:NotNull
    override val rootField1: String,
    @param:JsonProperty("rootField2")
    @get:JsonProperty("rootField2")
    override val rootField2: Boolean? = null,
    open val firstLevelField1: String,
    open val firstLevelField2: Int? = null
) : RootType(rootField1, rootField2) {
    @get:JsonProperty("rootDiscriminator")
    @get:NotNull
    override val rootDiscriminator: RootDiscriminator = RootDiscriminator.FIRST_LEVEL_CHILD

    abstract val firstLevelDiscriminator: FirstLevelDiscriminator
}

enum class FirstLevelDiscriminator(
    @JsonValue
    val value: String
) {
    SECOND_LEVEL_CHILD1("secondLevelChild1"),

    SECOND_LEVEL_CHILD2("secondLevelChild2");

    companion object {
        private val mapping: Map<String, FirstLevelDiscriminator> =
            values().associateBy(FirstLevelDiscriminator::value)

        fun fromValue(value: String): FirstLevelDiscriminator? = mapping[value]
    }
}

enum class RootDiscriminator(
    @JsonValue
    val value: String
) {
    FIRST_LEVEL_CHILD("firstLevelChild");

    companion object {
        private val mapping: Map<String, RootDiscriminator> =
            values().associateBy(RootDiscriminator::value)

        fun fromValue(value: String): RootDiscriminator? = mapping[value]
    }
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "rootDiscriminator",
    visible = true
)
@JsonSubTypes(JsonSubTypes.Type(value = FirstLevelChild::class, name = "firstLevelChild"))
sealed class RootType(
    open val rootField1: String,
    open val rootField2: Boolean? = null
) {
    abstract val rootDiscriminator: RootDiscriminator
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "secondLevelDiscriminator",
    visible = true
)
@JsonSubTypes(
    JsonSubTypes.Type(
        value = ThirdLevelChild11::class,
        name =
        "thirdLevelChild1"
    ),
    JsonSubTypes.Type(
        value = ThirdLevelChild12::class,
        name =
        "thirdLevelChild2"
    )
)
sealed class SecondLevelChild1(
    @param:JsonProperty("rootField1")
    @get:JsonProperty("rootField1")
    @get:NotNull
    override val rootField1: String,
    @param:JsonProperty("rootField2")
    @get:JsonProperty("rootField2")
    override val rootField2: Boolean? = null,
    @param:JsonProperty("firstLevelField1")
    @get:JsonProperty("firstLevelField1")
    @get:NotNull
    override val firstLevelField1: String,
    @param:JsonProperty("firstLevelField2")
    @get:JsonProperty("firstLevelField2")
    override val firstLevelField2: Int? = null,
    open val metadata: SecondLevelMetadata
) : FirstLevelChild(rootField1, rootField2, firstLevelField1, firstLevelField2) {
    @get:JsonProperty("firstLevelDiscriminator")
    @get:NotNull
    override val firstLevelDiscriminator: FirstLevelDiscriminator =
        FirstLevelDiscriminator.SECOND_LEVEL_CHILD1

    abstract val secondLevelDiscriminator: SecondLevelDiscriminator
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "secondLevelDiscriminator",
    visible = true
)
@JsonSubTypes(
    JsonSubTypes.Type(
        value = ThirdLevelChild21::class,
        name =
        "thirdLevelChild1"
    ),
    JsonSubTypes.Type(
        value = ThirdLevelChild22::class,
        name =
        "thirdLevelChild2"
    )
)
sealed class SecondLevelChild2(
    @param:JsonProperty("rootField1")
    @get:JsonProperty("rootField1")
    @get:NotNull
    override val rootField1: String,
    @param:JsonProperty("rootField2")
    @get:JsonProperty("rootField2")
    override val rootField2: Boolean? = null,
    @param:JsonProperty("firstLevelField1")
    @get:JsonProperty("firstLevelField1")
    @get:NotNull
    override val firstLevelField1: String,
    @param:JsonProperty("firstLevelField2")
    @get:JsonProperty("firstLevelField2")
    override val firstLevelField2: Int? = null,
    open val metadata: SecondLevelMetadata
) : FirstLevelChild(rootField1, rootField2, firstLevelField1, firstLevelField2) {
    @get:JsonProperty("firstLevelDiscriminator")
    @get:NotNull
    override val firstLevelDiscriminator: FirstLevelDiscriminator =
        FirstLevelDiscriminator.SECOND_LEVEL_CHILD2

    abstract val secondLevelDiscriminator: SecondLevelDiscriminator
}

enum class SecondLevelDiscriminator(
    @JsonValue
    val value: String
) {
    THIRD_LEVEL_CHILD1("thirdLevelChild1"),

    THIRD_LEVEL_CHILD2("thirdLevelChild2");

    companion object {
        private val mapping: Map<String, SecondLevelDiscriminator> =
            values().associateBy(SecondLevelDiscriminator::value)

        fun fromValue(value: String): SecondLevelDiscriminator? = mapping[value]
    }
}

data class SecondLevelMetadata(
    @param:JsonProperty("obj")
    @get:JsonProperty("obj")
    @get:NotNull
    @get:Valid
    val obj: CommonObject
)

data class ThirdLevelChild11(
    @param:JsonProperty("rootField1")
    @get:JsonProperty("rootField1")
    @get:NotNull
    override val rootField1: String,
    @param:JsonProperty("rootField2")
    @get:JsonProperty("rootField2")
    override val rootField2: Boolean? = null,
    @param:JsonProperty("firstLevelField1")
    @get:JsonProperty("firstLevelField1")
    @get:NotNull
    override val firstLevelField1: String,
    @param:JsonProperty("firstLevelField2")
    @get:JsonProperty("firstLevelField2")
    override val firstLevelField2: Int? = null,
    @param:JsonProperty("metadata")
    @get:JsonProperty("metadata")
    @get:NotNull
    @get:Valid
    override val metadata: SecondLevelMetadata,
    @param:JsonProperty("creationDate")
    @get:JsonProperty("creationDate")
    @get:NotNull
    val creationDate: Int
) : SecondLevelChild1(rootField1, rootField2, firstLevelField1, firstLevelField2, metadata) {
    @get:JsonProperty("secondLevelDiscriminator")
    @get:NotNull
    override val secondLevelDiscriminator: SecondLevelDiscriminator =
        SecondLevelDiscriminator.THIRD_LEVEL_CHILD1
}

data class ThirdLevelChild12(
    @param:JsonProperty("rootField1")
    @get:JsonProperty("rootField1")
    @get:NotNull
    override val rootField1: String,
    @param:JsonProperty("rootField2")
    @get:JsonProperty("rootField2")
    override val rootField2: Boolean? = null,
    @param:JsonProperty("firstLevelField1")
    @get:JsonProperty("firstLevelField1")
    @get:NotNull
    override val firstLevelField1: String,
    @param:JsonProperty("firstLevelField2")
    @get:JsonProperty("firstLevelField2")
    override val firstLevelField2: Int? = null,
    @param:JsonProperty("metadata")
    @get:JsonProperty("metadata")
    @get:NotNull
    @get:Valid
    override val metadata: SecondLevelMetadata,
    @param:JsonProperty("isDeleted")
    @get:JsonProperty("isDeleted")
    @get:NotNull
    val isDeleted: Boolean
) : SecondLevelChild1(rootField1, rootField2, firstLevelField1, firstLevelField2, metadata) {
    @get:JsonProperty("secondLevelDiscriminator")
    @get:NotNull
    override val secondLevelDiscriminator: SecondLevelDiscriminator =
        SecondLevelDiscriminator.THIRD_LEVEL_CHILD2
}

data class ThirdLevelChild21(
    @param:JsonProperty("rootField1")
    @get:JsonProperty("rootField1")
    @get:NotNull
    override val rootField1: String,
    @param:JsonProperty("rootField2")
    @get:JsonProperty("rootField2")
    override val rootField2: Boolean? = null,
    @param:JsonProperty("firstLevelField1")
    @get:JsonProperty("firstLevelField1")
    @get:NotNull
    override val firstLevelField1: String,
    @param:JsonProperty("firstLevelField2")
    @get:JsonProperty("firstLevelField2")
    override val firstLevelField2: Int? = null,
    @param:JsonProperty("metadata")
    @get:JsonProperty("metadata")
    @get:NotNull
    @get:Valid
    override val metadata: SecondLevelMetadata,
    @param:JsonProperty("creationDate")
    @get:JsonProperty("creationDate")
    @get:NotNull
    val creationDate: Int
) : SecondLevelChild2(rootField1, rootField2, firstLevelField1, firstLevelField2, metadata) {
    @get:JsonProperty("secondLevelDiscriminator")
    @get:NotNull
    override val secondLevelDiscriminator: SecondLevelDiscriminator =
        SecondLevelDiscriminator.THIRD_LEVEL_CHILD1
}

data class ThirdLevelChild22(
    @param:JsonProperty("rootField1")
    @get:JsonProperty("rootField1")
    @get:NotNull
    override val rootField1: String,
    @param:JsonProperty("rootField2")
    @get:JsonProperty("rootField2")
    override val rootField2: Boolean? = null,
    @param:JsonProperty("firstLevelField1")
    @get:JsonProperty("firstLevelField1")
    @get:NotNull
    override val firstLevelField1: String,
    @param:JsonProperty("firstLevelField2")
    @get:JsonProperty("firstLevelField2")
    override val firstLevelField2: Int? = null,
    @param:JsonProperty("metadata")
    @get:JsonProperty("metadata")
    @get:NotNull
    @get:Valid
    override val metadata: SecondLevelMetadata,
    @param:JsonProperty("isDeleted")
    @get:JsonProperty("isDeleted")
    @get:NotNull
    val isDeleted: Boolean
) : SecondLevelChild2(rootField1, rootField2, firstLevelField1, firstLevelField2, metadata) {
    @get:JsonProperty("secondLevelDiscriminator")
    @get:NotNull
    override val secondLevelDiscriminator: SecondLevelDiscriminator =
        SecondLevelDiscriminator.THIRD_LEVEL_CHILD2
}
