package examples.jsonMergePatch.models

import com.fasterxml.jackson.annotation.JsonProperty
import org.openapitools.jackson.nullable.JsonNullable
import javax.validation.Valid
import kotlin.String

data class InnerMergePatch(
    @param:JsonProperty("p")
    @get:JsonProperty("p")
    val p: JsonNullable<String> = JsonNullable.undefined()
)

data class InnerNotMergePatch(
    @param:JsonProperty("p")
    @get:JsonProperty("p")
    val p: String? = null
)

data class InnerOnlyMergePatchInline(
    @param:JsonProperty("inner")
    @get:JsonProperty("inner")
    @get:Valid
    val inner: InnerOnlyMergePatchInlineInner? = null
)

data class InnerOnlyMergePatchInlineInner(
    @param:JsonProperty("p")
    @get:JsonProperty("p")
    val p: JsonNullable<String> = JsonNullable.undefined()
)

data class InnerOnlyMergePatchRef(
    @param:JsonProperty("inner")
    @get:JsonProperty("inner")
    @get:Valid
    val inner: InnerMergePatch? = null
)

data class NestedMergePatchInline(
    @param:JsonProperty("inner")
    @get:JsonProperty("inner")
    @get:Valid
    val inner: JsonNullable<NestedMergePatchInlineInner> = JsonNullable.undefined()
)

data class NestedMergePatchInlineInner(
    @param:JsonProperty("p")
    @get:JsonProperty("p")
    val p: JsonNullable<String> = JsonNullable.undefined()
)

data class NestedMergePatchRef(
    @param:JsonProperty("inner")
    @get:JsonProperty("inner")
    @get:Valid
    val inner: JsonNullable<InnerMergePatch> = JsonNullable.undefined()
)

data class NoMergePatchInline(
    @param:JsonProperty("inner")
    @get:JsonProperty("inner")
    @get:Valid
    val inner: NoMergePatchInlineInner? = null
)

data class NoMergePatchInlineInner(
    @param:JsonProperty("p")
    @get:JsonProperty("p")
    val p: String? = null
)

data class NoMergePatchRef(
    @param:JsonProperty("inner")
    @get:JsonProperty("inner")
    @get:Valid
    val inner: InnerNotMergePatch? = null
)

data class TopLevelLevelMergePatchInline(
    @param:JsonProperty("inner")
    @get:JsonProperty("inner")
    @get:Valid
    val inner: JsonNullable<TopLevelLevelMergePatchInlineInner> = JsonNullable.undefined()
)

data class TopLevelLevelMergePatchInlineInner(
    @param:JsonProperty("p")
    @get:JsonProperty("p")
    val p: String? = null
)

data class TopLevelLevelMergePatchRef(
    @param:JsonProperty("inner")
    @get:JsonProperty("inner")
    @get:Valid
    val inner: JsonNullable<InnerNotMergePatch> = JsonNullable.undefined()
)
