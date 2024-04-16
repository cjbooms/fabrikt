package examples.noValidationAnnotations.models

import com.fasterxml.jackson.annotation.JsonProperty

data class ValidationAnnotationsFriends(
  @param:JsonProperty("first_name")
  @get:JsonProperty("first_name")
  val firstName: String,
  @param:JsonProperty("last_name")
  @get:JsonProperty("last_name")
  val lastName: String? = null
)

data class ValidationAnnotationsAddress(
  @param:JsonProperty("street")
  @get:JsonProperty("street")
  val street: String,
  @param:JsonProperty("city")
  @get:JsonProperty("city")
  val city: String,
  @param:JsonProperty("postal_code")
  @get:JsonProperty("postal_code")
  val postalCode: String
)

data class QualitiesValue(
  @param:JsonProperty("name")
  @get:JsonProperty("name")
  val name: String? = null,
  @param:JsonProperty("value")
  @get:JsonProperty("value")
  val value: String? = null
)
