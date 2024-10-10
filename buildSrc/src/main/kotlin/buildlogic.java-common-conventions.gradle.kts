plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.junit.jupiter:junit-jupiter")
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

dependencyLocking {
    lockMode = LockMode.STRICT
}

configurations.all {
    resolutionStrategy {
        activateDependencyLocking() // TODO https://docs.gradle.org/current/userguide/dependency_locking.html

        val log4jVer = "[2.0,3.0["
        defaultVersion("org.apache.logging.log4j:log4j-api:$log4jVer")
        defaultVersion("org.apache.logging.log4j:log4j-core:$log4jVer")
        defaultVersion("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVer")

        defaultVersion("org.eclipse.jgit:org.eclipse.jgit:[6.0,7.0[")  // e.g. 6.10.0.202406032230-r

        defaultVersion("org.assertj:assertj-core:3.26.3")

        // change the next two versions in sync to not introduce new deps e.g. 'org.apiguardian:apiguardian-api'
        val jupiterVer = "5.11.2"
        val junitPlatform = "1.11.2"
        defaultVersion("org.junit.jupiter:junit-jupiter-api:$jupiterVer")
        defaultVersion("org.junit.jupiter:junit-jupiter-params:$jupiterVer")

        // need version when employing resolutionStrategy, otherwise IllegalStateException: ...
        // Cannot find ... for constraint runtimeElements -> org.junit.platform:junit-platform-launcher:1.11.1
        defaultVersion("org.junit.platform:junit-platform-launcher:$junitPlatform")
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
