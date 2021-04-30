package examples.defaultValues.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import javax.validation.constraints.NotNull
import kotlin.Boolean
import kotlin.Int
import kotlin.String

data class PersonWithDefaults(
    @param:JsonProperty("name")
    @get:JsonProperty("name")
    @get:NotNull
    val name: String,
    @param:JsonProperty("age")
    @get:JsonProperty("age")
    val age: Int? = 18,
    @param:JsonProperty("height_category")
    @get:JsonProperty("height_category")
    val heightCategory: HeightCategory? = HeightCategory.TALL,
    @param:JsonProperty("has_hair")
    @get:JsonProperty("has_hair")
    val hasHair: Boolean? = true,
    @param:JsonProperty("catch_phrase")
    @get:JsonProperty("catch_phrase")
    val catchPhrase: String? = "Cowabunga Dude"
)

enum class HeightCategory(
    @JsonValue
    val value: String
) {
    TALL("tall"),

    SHORT("short");
}

data class ObjectWithRequiredDefaultProperties(
    @param:JsonProperty("name")
    @get:JsonProperty("name")
    @get:NotNull
    val name: String,
    @param:JsonProperty("objectType")
    @get:JsonProperty("objectType")
    @get:NotNull
    val objectType: String = "myObjectType",
    @param:JsonProperty("anotherNonRequiredProperty")
    @get:JsonProperty("anotherNonRequiredProperty")
    val anotherNonRequiredProperty: String? = null
)
