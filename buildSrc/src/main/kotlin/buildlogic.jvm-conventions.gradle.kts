plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.assertj:assertj-core:3.26.3")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    targetCompatibility = JavaVersion.VERSION_11
    sourceCompatibility = targetCompatibility
}

tasks.named<Test>("test") {
    useJUnitPlatform() // fix "automatic loading deprecated",  "No tests found"
    testLogging.showStandardStreams = true // show StdOut and StdErr on the console
}

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.version == "") {
            val version = findDefaultVersionInCatalog(requested.group, requested.name)
            useVersion(version.version)
            because(version.because)
        }
    }
}

data class DefaultVersion(val version: String, val because: String)

fun findDefaultVersionInCatalog(group: String, name: String): DefaultVersion {
    val ga = group + ":" + name
    if (defaultVersions.containsKey(ga)) {
        return DefaultVersion(version = defaultVersions.get(ga)!!, because = "buildLogic")
    }
    throw IllegalStateException("\"$ga\" version is not configured")
}

val log4jVer = "[2.0,3.0["
val defaultVersions = mapOf(
    "org.apache.logging.log4j:log4j-api" to log4jVer,
    "org.apache.logging.log4j:log4j-core" to log4jVer,
    "org.apache.logging.log4j:log4j-slf4j-impl" to log4jVer,
    "org.eclipse.jgit:org.eclipse.jgit" to "[6.0,7.0[",  // e.g. 6.10.0.202406032230-r

    // need version when employing resolutionStrategy, otherwise IllegalStateException: ...
    // Cannot find ... for constraint runtimeElements -> org.junit.platform:junit-platform-launcher:1.11.1
    "org.junit.platform:junit-platform-launcher" to "1.11.2",
)
