package examples.okHttpClientMultiMediaType.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonValue
import java.time.OffsetDateTime
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map

data class AlternateResponseModel(
    @param:JsonProperty("extra_first_attr")
    @get:JsonProperty("extra_first_attr")
    val extraFirstAttr: OffsetDateTime? = null,
    @param:JsonProperty("extra_second_attr")
    @get:JsonProperty("extra_second_attr")
    val extraSecondAttr: Int? = null
)

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "model_type",
    visible = true
)
@JsonSubTypes(
    JsonSubTypes.Type(
        value = FirstModel::class,
        name =
        "first_model"
    ),
    JsonSubTypes.Type(
        value = SecondModel::class,
        name =
        "second_model"
    ),
    JsonSubTypes.Type(value = ThirdModel::class, name = "third_model")
)
sealed class Content(
    open val id: String? = null,
    open val firstAttr: OffsetDateTime? = null,
    open val secondAttr: String? = null,
    open val thirdAttr: ContentThirdAttr? = null,
    open val etag: String? = null
) {
    abstract val modelType: ContentModelType
}

enum class ContentModelType(
    @JsonValue
    val value: String
) {
    FIRST_MODEL("first_model"),

    SECOND_MODEL("second_model"),

    THIRD_MODEL("third_model");

    companion object {
        private val mapping: Map<String, ContentModelType> =
            values().associateBy(ContentModelType::value)

        fun fromValue(value: String): ContentModelType? = mapping[value]
    }
}

enum class ContentThirdAttr(
    @JsonValue
    val value: String
) {
    ENUM_TYPE_1("enum_type_1"),

    ENUM_TYPE_2("enum_type_2");

    companion object {
        private val mapping: Map<String, ContentThirdAttr> =
            values().associateBy(ContentThirdAttr::value)

        fun fromValue(value: String): ContentThirdAttr? = mapping[value]
    }
}

enum class ContentType(
    @JsonValue
    val value: String
) {
    APPLICATION_JSON("application/json"),

    APPLICATION_VND_CUSTOM_MEDIA_JSON("application/vnd.custom.media+json");

    companion object {
        private val mapping: Map<String, ContentType> = values().associateBy(ContentType::value)

        fun fromValue(value: String): ContentType? = mapping[value]
    }
}

enum class ContentTypeAccept(
    @JsonValue
    val value: String
) {
    APPLICATION_JSON("application/json"),

    APPLICATION_VND_CUSTOM_MEDIA_JSON("application/vnd.custom.media+json");

    companion object {
        private val mapping: Map<String, ContentTypeAccept> =
            values().associateBy(ContentTypeAccept::value)

        fun fromValue(value: String): ContentTypeAccept? = mapping[value]
    }
}

data class FirstModel(
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
    val extraFirstAttr: List<String>? = null
) : Content(id, firstAttr, secondAttr, thirdAttr, etag) {
    @get:JsonProperty("model_type")
    @get:NotNull
    override val modelType: ContentModelType = ContentModelType.FIRST_MODEL
}

data class OtherQueryResult(
    @param:JsonProperty("items")
    @get:JsonProperty("items")
    @get:NotNull
    @get:Size(min = 0)
    @get:Valid
    val items: List<AlternateResponseModel>
)

data class QueryResult(
    @param:JsonProperty("items")
    @get:JsonProperty("items")
    @get:NotNull
    @get:Size(min = 0)
    @get:Valid
    val items: List<Content>
)

data class SecondModel(
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
    val extraFirstAttr: String? = null,
    @param:JsonProperty("extra_second_attr")
    @get:JsonProperty("extra_second_attr")
    val extraSecondAttr: Boolean? = null
) : Content(id, firstAttr, secondAttr, thirdAttr, etag) {
    @get:JsonProperty("model_type")
    @get:NotNull
    override val modelType: ContentModelType = ContentModelType.SECOND_MODEL
}

data class ThirdModel(
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
    val extraFirstAttr: OffsetDateTime? = null,
    @param:JsonProperty("extra_second_attr")
    @get:JsonProperty("extra_second_attr")
    val extraSecondAttr: Int? = null
) : Content(id, firstAttr, secondAttr, thirdAttr, etag) {
    @get:JsonProperty("model_type")
    @get:NotNull
    override val modelType: ContentModelType = ContentModelType.THIRD_MODEL
}
