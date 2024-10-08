package com.exactrpo.buildext

import org.gradle.api.logging.Logging
import org.gradle.util.Configurable

fun <T> Configurable<T>.useDefaultVersions() {
    Logging.getLogger("foo").lifecycle("using default versions as defined in buildSrc")
}
