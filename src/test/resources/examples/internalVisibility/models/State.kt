package examples.internalVisibility.models

import com.fasterxml.jackson.`annotation`.JsonSubTypes
import com.fasterxml.jackson.`annotation`.JsonTypeInfo

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.EXISTING_PROPERTY,
  property = "status",
  visible = true,
)
@JsonSubTypes(JsonSubTypes.Type(value = StateA::class, name = "a"),JsonSubTypes.Type(value =
    StateB::class, name = "b"))
internal sealed interface State
