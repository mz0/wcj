package com.exactrpo.buildext

import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.logging.Logging

fun ConfigurationContainer.useDefaultVersions() {
    Logging.getLogger("").lifecycle("using default versions as defined in buildSrc")
}
