pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "hexagonal-architecture"

// common
include(":common")
include(":ulid")
project(":ulid").projectDir = file("./common/ulid")

// domain
include("domain")

// usecase
include("usecase")

// infrastructures
include(":infrastructure")
include(":persistence")
project(":persistence").projectDir = file("./infrastructure/persistence")

// bootstraps
include(":bootstrap")
include(":api")
project(":api").projectDir = file("./bootstrap/api")

include(":worker")
project(":worker").projectDir = file("./bootstrap/worker")
