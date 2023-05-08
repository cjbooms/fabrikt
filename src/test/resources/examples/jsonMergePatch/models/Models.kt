package examples.jsonMergePatch.models

import com.fasterxml.jackson.annotation.JsonProperty
import org.openapitools.jackson.nullable.JsonNullable
import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.String

data class InnerMergePatch(
    @param:JsonProperty("p")
    @get:JsonProperty("p")
    val p: JsonNullable<String?> = JsonNullable.undefined()
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
    val p: JsonNullable<String?> = JsonNullable.undefined()
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
    val inner: JsonNullable<NestedMergePatchInlineInner?> = JsonNullable.undefined()
)

data class NestedMergePatchInlineInner(
    @param:JsonProperty("p")
    @get:JsonProperty("p")
    val p: JsonNullable<String?> = JsonNullable.undefined()
)

data class NestedMergePatchRef(
    @param:JsonProperty("inner")
    @get:JsonProperty("inner")
    @get:Valid
    val inner: JsonNullable<InnerMergePatch?> = JsonNullable.undefined()
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

data class NullabilityCheck(
    @param:JsonProperty("not-null-no-default-not-required")
    @get:JsonProperty("not-null-no-default-not-required")
    val notNullNoDefaultNotRequired: JsonNullable<String?> = JsonNullable.undefined(),
    @param:JsonProperty("nullable-no-default-not-required")
    @get:JsonProperty("nullable-no-default-not-required")
    val nullableNoDefaultNotRequired: JsonNullable<String?> = JsonNullable.undefined(),
    @param:JsonProperty("not-null-with-default-not-required")
    @get:JsonProperty("not-null-with-default-not-required")
    @get:NotNull
    val notNullWithDefaultNotRequired: JsonNullable<String> = JsonNullable.of(""),
    @param:JsonProperty("nullable-with-default-not-required")
    @get:JsonProperty("nullable-with-default-not-required")
    @get:NotNull
    val nullableWithDefaultNotRequired: JsonNullable<String> = JsonNullable.of(""),
    @param:JsonProperty("not-null-no-default-required")
    @get:JsonProperty("not-null-no-default-required")
    val notNullNoDefaultRequired: JsonNullable<String?> = JsonNullable.undefined(),
    @param:JsonProperty("nullable-no-default-required")
    @get:JsonProperty("nullable-no-default-required")
    val nullableNoDefaultRequired: JsonNullable<String?> = JsonNullable.undefined(),
    @param:JsonProperty("not-null-with-default-required")
    @get:JsonProperty("not-null-with-default-required")
    @get:NotNull
    val notNullWithDefaultRequired: JsonNullable<String> = JsonNullable.of(""),
    @param:JsonProperty("nullable-with-default-required")
    @get:JsonProperty("nullable-with-default-required")
    @get:NotNull
    val nullableWithDefaultRequired: JsonNullable<String> = JsonNullable.of("")
)

data class TopLevelLevelMergePatchInline(
    @param:JsonProperty("inner")
    @get:JsonProperty("inner")
    @get:Valid
    val inner: JsonNullable<TopLevelLevelMergePatchInlineInner?> = JsonNullable.undefined()
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
    val inner: JsonNullable<InnerNotMergePatch?> = JsonNullable.undefined()
)
