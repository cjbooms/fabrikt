package examples.customExtensions.models

import com.fasterxml.jackson.`annotation`.JsonInclude
import com.fasterxml.jackson.`annotation`.JsonProperty
import org.openapitools.jackson.nullable.JsonNullable
import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.String

public data class InnerMergePatch(
    @param:JsonProperty("p")
    @get:JsonProperty("p")
    public val p: JsonNullable<String> = JsonNullable.undefined(),
)

public data class InnerNotMergePatch(
    @param:JsonProperty("p")
    @get:JsonProperty("p")
    public val p: String? = null,
)

public data class InnerOnlyMergePatchInline(
    @param:JsonProperty("inner")
    @get:JsonProperty("inner")
    @get:Valid
    public val `inner`: InnerOnlyMergePatchInlineInner? = null,
)

public data class InnerOnlyMergePatchInlineInner(
    @param:JsonProperty("p")
    @get:JsonProperty("p")
    public val p: JsonNullable<String> = JsonNullable.undefined(),
)

public data class InnerOnlyMergePatchRef(
    @param:JsonProperty("inner")
    @get:JsonProperty("inner")
    @get:Valid
    public val `inner`: InnerMergePatch? = null,
)

public data class NestedMergePatchInline(
    @param:JsonProperty("inner")
    @get:JsonProperty("inner")
    @get:Valid
    public val `inner`: JsonNullable<NestedMergePatchInlineInner> = JsonNullable.undefined(),
)

public data class NestedMergePatchInlineInner(
    @param:JsonProperty("p")
    @get:JsonProperty("p")
    public val p: JsonNullable<String> = JsonNullable.undefined(),
)

public data class NestedMergePatchRef(
    @param:JsonProperty("inner")
    @get:JsonProperty("inner")
    @get:Valid
    public val `inner`: JsonNullable<InnerMergePatch> = JsonNullable.undefined(),
)

public data class NoMergePatchInline(
    @param:JsonProperty("inner")
    @get:JsonProperty("inner")
    @get:Valid
    public val `inner`: NoMergePatchInlineInner? = null,
)

public data class NoMergePatchInlineInner(
    @param:JsonProperty("p")
    @get:JsonProperty("p")
    public val p: String? = null,
)

public data class NoMergePatchRef(
    @param:JsonProperty("inner")
    @get:JsonProperty("inner")
    @get:Valid
    public val `inner`: InnerNotMergePatch? = null,
)

public data class NullabilityCheck(
    @param:JsonProperty("not-null-no-default-not-required")
    @get:JsonProperty("not-null-no-default-not-required")
    public val notNullNoDefaultNotRequired: JsonNullable<String> = JsonNullable.undefined(),
    @param:JsonProperty("nullable-no-default-not-required")
    @get:JsonProperty("nullable-no-default-not-required")
    public val nullableNoDefaultNotRequired: JsonNullable<String?> = JsonNullable.undefined(),
    @param:JsonProperty("not-null-with-default-not-required")
    @get:JsonProperty("not-null-with-default-not-required")
    @get:NotNull
    public val notNullWithDefaultNotRequired: JsonNullable<String> = JsonNullable.of(""),
    @param:JsonProperty("nullable-with-default-not-required")
    @get:JsonProperty("nullable-with-default-not-required")
    public val nullableWithDefaultNotRequired: JsonNullable<String?> = JsonNullable.of(""),
    @param:JsonProperty("not-null-no-default-required")
    @get:JsonProperty("not-null-no-default-required")
    @get:NotNull
    public val notNullNoDefaultRequired: String,
    @param:JsonProperty("nullable-no-default-required")
    @get:JsonProperty("nullable-no-default-required")
    public val nullableNoDefaultRequired: String?,
    @param:JsonProperty("not-null-with-default-required")
    @get:JsonProperty("not-null-with-default-required")
    @get:NotNull
    public val notNullWithDefaultRequired: String,
    @param:JsonProperty("nullable-with-default-required")
    @get:JsonProperty("nullable-with-default-required")
    public val nullableWithDefaultRequired: String?,
)

public data class ParentWithIncludeNonNullExtension(
    @param:JsonProperty("simpleNullable")
    @get:JsonProperty("simpleNullable")
    @param:JsonInclude(JsonInclude.Include.NON_NULL)
    public val simpleNullable: String? = null,
    @param:JsonProperty("simpleRequired")
    @get:JsonProperty("simpleRequired")
    @get:NotNull
    public val simpleRequired: String,
    @param:JsonProperty("childWithout")
    @get:JsonProperty("childWithout")
    @get:Valid
    @param:JsonInclude(JsonInclude.Include.NON_NULL)
    public val childWithout: ParentWithIncludeNonNullExtensionChildWithout? = null,
)

public data class ParentWithIncludeNonNullExtensionChildWithout(
    @param:JsonProperty("direct")
    @get:JsonProperty("direct")
    public val direct: String? = null,
)

public data class TopLevelLevelMergePatchInline(
    @param:JsonProperty("inner")
    @get:JsonProperty("inner")
    @get:Valid
    public val `inner`: JsonNullable<TopLevelLevelMergePatchInlineInner> = JsonNullable.undefined(),
)

public data class TopLevelLevelMergePatchInlineInner(
    @param:JsonProperty("p")
    @get:JsonProperty("p")
    public val p: String? = null,
)

public data class TopLevelLevelMergePatchRef(
    @param:JsonProperty("inner")
    @get:JsonProperty("inner")
    @get:Valid
    public val `inner`: JsonNullable<InnerNotMergePatch> = JsonNullable.undefined(),
)
