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
    resolutionStrategy {
        val log4jVer = "[2.0,3.0["
        defaultVersion("org.apache.logging.log4j:log4j-api:$log4jVer")
        defaultVersion("org.apache.logging.log4j:log4j-core:$log4jVer")
        defaultVersion("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVer")

        defaultVersion("org.eclipse.jgit:org.eclipse.jgit:[6.0,7.0[")  // e.g. 6.10.0.202406032230-r

        // need version when employing resolutionStrategy, otherwise IllegalStateException: ...
        // Cannot find ... for constraint runtimeElements -> org.junit.platform:junit-platform-launcher:1.11.1
        defaultVersion("org.junit.platform:junit-platform-launcher:1.11.2")
    }
}

fun ResolutionStrategy.defaultVersion(module: String) {
    val (group, name, version) = module.split(':')
    eachDependency {
        if ((requested.group == group) && (requested.name == name) && (requested.version == "")) {
            useVersion(version)
            because("foo")
        }
    }
}
