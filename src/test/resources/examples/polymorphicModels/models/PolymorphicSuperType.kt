package examples.polymorphicModels.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import com.fasterxml.jackson.`annotation`.JsonSubTypes
import com.fasterxml.jackson.`annotation`.JsonTypeInfo
import javax.validation.constraints.NotNull
import kotlin.Int
import kotlin.String

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.EXISTING_PROPERTY,
  property = "generation",
  visible = true,
)
@JsonSubTypes(JsonSubTypes.Type(value = PolymorphicTypeOne::class, name =
    "PolymorphicTypeOne"),JsonSubTypes.Type(value = PolymorphicTypeTwo::class, name =
    "polymorphic_type_two"))
public sealed class PolymorphicSuperType(
  public open val firstName: String,
  public open val lastName: String,
) {
  public abstract val generation: String
}

public data class PolymorphicTypeOneRef(
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
  public val childOneName: String? = null,
  @get:JsonProperty("generation")
  @get:NotNull
  @param:JsonProperty("generation")
  override val generation: String = "PolymorphicTypeOne",
) : PolymorphicSuperType(firstName, lastName)

public data class PolymorphicTypeOneAnotherRef(
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
  public val childOneName: String? = null,
  @get:JsonProperty("generation")
  @get:NotNull
  @param:JsonProperty("generation")
  override val generation: String = "PolymorphicTypeOne",
) : PolymorphicSuperType(firstName, lastName)

public data class PolymorphicTypeTwoRef(
  @param:JsonProperty("first_name")
  @get:JsonProperty("first_name")
  @get:NotNull
  override val firstName: String,
  @param:JsonProperty("last_name")
  @get:JsonProperty("last_name")
  @get:NotNull
  override val lastName: String,
  @param:JsonProperty("some_integer_propery")
  @get:JsonProperty("some_integer_propery")
  public val someIntegerPropery: Int? = null,
  @param:JsonProperty("child_two_age")
  @get:JsonProperty("child_two_age")
  public val childTwoAge: Int? = null,
  @get:JsonProperty("generation")
  @get:NotNull
  @param:JsonProperty("generation")
  override val generation: String = "polymorphic_type_two",
) : PolymorphicSuperType(firstName, lastName)

public data class PolymorphicTypeOne(
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
  public val childOneName: String? = null,
  @get:JsonProperty("generation")
  @get:NotNull
  @param:JsonProperty("generation")
  override val generation: String = "PolymorphicTypeOne",
) : PolymorphicSuperType(firstName, lastName)

public data class PolymorphicTypeTwo(
  @param:JsonProperty("first_name")
  @get:JsonProperty("first_name")
  @get:NotNull
  override val firstName: String,
  @param:JsonProperty("last_name")
  @get:JsonProperty("last_name")
  @get:NotNull
  override val lastName: String,
  @param:JsonProperty("some_integer_propery")
  @get:JsonProperty("some_integer_propery")
  public val someIntegerPropery: Int? = null,
  @param:JsonProperty("child_two_age")
  @get:JsonProperty("child_two_age")
  public val childTwoAge: Int? = null,
  @get:JsonProperty("generation")
  @get:NotNull
  @param:JsonProperty("generation")
  override val generation: String = "polymorphic_type_two",
) : PolymorphicSuperType(firstName, lastName)
