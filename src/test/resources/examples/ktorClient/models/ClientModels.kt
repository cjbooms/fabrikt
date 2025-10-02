package examples.ktorClient.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.Double
import kotlin.String
import kotlin.collections.Map

@Serializable
public data class Item(
    @SerialName("id")
    public val id: String,
    @SerialName("name")
    public val name: String,
    @SerialName("description")
    public val description: String? = null,
    @SerialName("price")
    public val price: Double,
)

public enum class SortOrder(
    public val `value`: String,
) {
    @SerialName("asc")
    ASC("asc"),

    @SerialName("desc")
    DESC("desc"),
    ;

    public companion object {
        private val mapping: Map<String, SortOrder> = entries.associateBy(SortOrder::value)

        public fun fromValue(`value`: String): SortOrder? = mapping[value]
    }
}
