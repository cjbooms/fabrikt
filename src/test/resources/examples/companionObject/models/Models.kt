package examples.companionObject.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonValue
import javax.validation.constraints.NotNull
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.Map

data class Cat(
    @param:JsonProperty("id")
    @get:JsonProperty("id")
    @get:NotNull
    override val id: Long,
    @param:JsonProperty("name")
    @get:JsonProperty("name")
    @get:NotNull
    override val name: String,
    @param:JsonProperty("tag")
    @get:JsonProperty("tag")
    override val tag: String? = null,
    @param:JsonProperty("hunts")
    @get:JsonProperty("hunts")
    val hunts: Boolean? = null,
    @param:JsonProperty("age")
    @get:JsonProperty("age")
    val age: Int? = null
) : Pet(id, name, tag) {
    @get:JsonProperty("petType")
    @get:NotNull
    override val petType: String = "Cat"

    companion object
}

data class Dog(
    @param:JsonProperty("id")
    @get:JsonProperty("id")
    @get:NotNull
    override val id: Long,
    @param:JsonProperty("name")
    @get:JsonProperty("name")
    @get:NotNull
    override val name: String,
    @param:JsonProperty("tag")
    @get:JsonProperty("tag")
    override val tag: String? = null,
    @param:JsonProperty("bark")
    @get:JsonProperty("bark")
    val bark: Boolean? = null,
    @param:JsonProperty("breed")
    @get:JsonProperty("breed")
    val breed: DogBreed? = null
) : Pet(id, name, tag) {
    @get:JsonProperty("petType")
    @get:NotNull
    override val petType: String = "Dog"

    companion object
}

enum class DogBreed(
    @JsonValue
    val value: String
) {
    DINGO("Dingo"),

    HUSKY("Husky"),

    RETRIEVER("Retriever"),

    SHEPHERD("Shepherd");

    companion object {
        private val mapping: Map<String, DogBreed> = values().associateBy(DogBreed::value)

        fun fromValue(value: String): DogBreed? = mapping[value]
    }
}

data class Error(
    @param:JsonProperty("code")
    @get:JsonProperty("code")
    @get:NotNull
    val code: Int,
    @param:JsonProperty("message")
    @get:JsonProperty("message")
    @get:NotNull
    val message: String
) {
    companion object
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "petType",
    visible = true
)
@JsonSubTypes(
    JsonSubTypes.Type(value = Cat::class, name = "Cat"),
    JsonSubTypes.Type(
        value =
        Dog::class,
        name = "Dog"
    )
)
sealed class Pet(
    open val id: Long,
    open val name: String,
    open val tag: String? = null
) {
    abstract val petType: String
}
