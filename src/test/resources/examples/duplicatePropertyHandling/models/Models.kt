package examples.duplicatePropertyHandling.models

import com.fasterxml.jackson.annotation.JsonProperty
import kotlin.String

data class ContainsNestedAnyOfWithDupes(
    @param:JsonProperty("child_duplicate")
    @get:JsonProperty("child_duplicate")
    val childDuplicate: String? = null,
    @param:JsonProperty("top_level_duplicate")
    @get:JsonProperty("top_level_duplicate")
    val topLevelDuplicate: String? = null,
)

data class DuplicatesParent(
    @param:JsonProperty("child_duplicate")
    @get:JsonProperty("child_duplicate")
    val childDuplicate: String? = null,
    @param:JsonProperty("top_level_duplicate")
    @get:JsonProperty("top_level_duplicate")
    val topLevelDuplicate: String? = null,
)

data class FirstOneD(
    @param:JsonProperty("child_duplicate")
    @get:JsonProperty("child_duplicate")
    val childDuplicate: String? = null,
)

data class TheDuplicator(
    @param:JsonProperty("top_level_duplicate")
    @get:JsonProperty("top_level_duplicate")
    val topLevelDuplicate: String? = null,
    @param:JsonProperty("child_duplicate")
    @get:JsonProperty("child_duplicate")
    val childDuplicate: String? = null,
)
