plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

rootProject.name = "fabrikt"

include(
    "end2end-tests:okhttp",
    "end2end-tests:openfeign",
    "end2end-tests:ktor",
    "end2end-tests:models-jackson",
    "end2end-tests:models-kotlinx",
    "playground",
)
