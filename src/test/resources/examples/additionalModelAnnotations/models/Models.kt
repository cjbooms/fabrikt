package example.additionalannotation.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import com.fasterxml.jackson.`annotation`.JsonSubTypes
import com.fasterxml.jackson.`annotation`.JsonTypeInfo
import com.fasterxml.jackson.`annotation`.JsonValue
import example.Annotation1
import example.Annotation2
import java.time.OffsetDateTime
import java.util.UUID
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map

@Annotation1
@Annotation2
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "model_type",
    visible = true,
)
@JsonSubTypes(
    JsonSubTypes.Type(
        value = FirstModel::class,
        name =
        "first_model",
    ),
    JsonSubTypes.Type(
        value = SecondModel::class,
        name =
        "second_model",
    ),
    JsonSubTypes.Type(value = ThirdModel::class, name = "third_model"),
)
public sealed class Content(
    public open val id: String? = null,
    public open val firstAttr: OffsetDateTime? = null,
    public open val secondAttr: String? = null,
    public open val thirdAttr: ContentThirdAttr? = null,
    public open val etag: String? = null,
) {
    public abstract val modelType: ContentModelType
}

@Annotation1
@Annotation2
public enum class ContentModelType(
    @JsonValue
    public val `value`: String,
) {
    FIRST_MODEL("first_model"),
    SECOND_MODEL("second_model"),
    THIRD_MODEL("third_model"),
    ;

    public companion object {
        private val mapping: Map<String, ContentModelType> =
            entries.associateBy(ContentModelType::value)

        public fun fromValue(`value`: String): ContentModelType? = mapping[value]
    }
}

@Annotation1
@Annotation2
public enum class ContentThirdAttr(
    @JsonValue
    public val `value`: String,
) {
    ENUM_TYPE_1("enum_type_1"),
    ENUM_TYPE_2("enum_type_2"),
    ;

    public companion object {
        private val mapping: Map<String, ContentThirdAttr> =
            entries.associateBy(ContentThirdAttr::value)

        public fun fromValue(`value`: String): ContentThirdAttr? = mapping[value]
    }
}

@Annotation1
@Annotation2
public data class Failure(
    @param:JsonProperty("traceId")
    @get:JsonProperty("traceId")
    @get:NotNull
    public val traceId: UUID,
    @param:JsonProperty("errorCode")
    @get:JsonProperty("errorCode")
    @get:NotNull
    public val errorCode: String,
    @param:JsonProperty("error")
    @get:JsonProperty("error")
    @get:NotNull
    public val error: String,
    @param:JsonProperty("subType")
    @get:JsonProperty("subType")
    public val subType: String? = null,
)

@Annotation1
@Annotation2
public data class FirstModel(
    @param:JsonProperty("id")
    @get:JsonProperty("id")
    override val id: String? = null,
    @param:JsonProperty("first_attr")
    @get:JsonProperty("first_attr")
    override val firstAttr: OffsetDateTime? = null,
    @param:JsonProperty("second_attr")
    @get:JsonProperty("second_attr")
    override val secondAttr: String? = null,
    @param:JsonProperty("third_attr")
    @get:JsonProperty("third_attr")
    override val thirdAttr: ContentThirdAttr? = null,
    @param:JsonProperty("etag")
    @get:JsonProperty("etag")
    override val etag: String? = null,
    @param:JsonProperty("extra_first_attr")
    @get:JsonProperty("extra_first_attr")
    public val extraFirstAttr: List<String>? = null,
    @get:JsonProperty("model_type")
    @get:NotNull
    @param:JsonProperty("model_type")
    override val modelType: ContentModelType = ContentModelType.FIRST_MODEL,
) : Content(id, firstAttr, secondAttr, thirdAttr, etag)

@Annotation1
@Annotation2
public data class QueryResult(
    @param:JsonProperty("items")
    @get:JsonProperty("items")
    @get:NotNull
    @get:Size(min = 0)
    @get:Valid
    public val items: List<Content>,
)

@Annotation1
@Annotation2
public data class SecondModel(
    @param:JsonProperty("id")
    @get:JsonProperty("id")
    override val id: String? = null,
    @param:JsonProperty("first_attr")
    @get:JsonProperty("first_attr")
    override val firstAttr: OffsetDateTime? = null,
    @param:JsonProperty("second_attr")
    @get:JsonProperty("second_attr")
    override val secondAttr: String? = null,
    @param:JsonProperty("third_attr")
    @get:JsonProperty("third_attr")
    override val thirdAttr: ContentThirdAttr? = null,
    @param:JsonProperty("etag")
    @get:JsonProperty("etag")
    override val etag: String? = null,
    @param:JsonProperty("extra_first_attr")
    @get:JsonProperty("extra_first_attr")
    public val extraFirstAttr: String? = null,
    @param:JsonProperty("extra_second_attr")
    @get:JsonProperty("extra_second_attr")
    public val extraSecondAttr: Boolean? = null,
    @get:JsonProperty("model_type")
    @get:NotNull
    @param:JsonProperty("model_type")
    override val modelType: ContentModelType = ContentModelType.SECOND_MODEL,
) : Content(id, firstAttr, secondAttr, thirdAttr, etag)

@Annotation1
@Annotation2
public data class ThirdModel(
    @param:JsonProperty("id")
    @get:JsonProperty("id")
    override val id: String? = null,
    @param:JsonProperty("first_attr")
    @get:JsonProperty("first_attr")
    override val firstAttr: OffsetDateTime? = null,
    @param:JsonProperty("second_attr")
    @get:JsonProperty("second_attr")
    override val secondAttr: String? = null,
    @param:JsonProperty("third_attr")
    @get:JsonProperty("third_attr")
    override val thirdAttr: ContentThirdAttr? = null,
    @param:JsonProperty("etag")
    @get:JsonProperty("etag")
    override val etag: String? = null,
    @param:JsonProperty("extra_first_attr")
    @get:JsonProperty("extra_first_attr")
    public val extraFirstAttr: OffsetDateTime? = null,
    @param:JsonProperty("extra_second_attr")
    @get:JsonProperty("extra_second_attr")
    public val extraSecondAttr: Int? = null,
    @get:JsonProperty("model_type")
    @get:NotNull
    @param:JsonProperty("model_type")
    override val modelType: ContentModelType = ContentModelType.THIRD_MODEL,
) : Content(id, firstAttr, secondAttr, thirdAttr, etag)
