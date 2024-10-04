import org.gradle.kotlin.dsl.apply
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    java
}
val targetJRE = "11"

repositories {
    mavenCentral()
}

dependencies {
    val log4jVer = "[2.0,3.0["

    constraints {
        implementation("org.eclipse.jgit:org.eclipse.jgit:[6.0,7.0[") // e.g. 6.10.0.202406032230-r
        implementation("org.apache.logging.log4j:log4j-api:$log4jVer")
        runtimeOnly("org.apache.logging.log4j:log4j-core:$log4jVer")
        runtimeOnly("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVer")
    }


    testImplementation("org.assertj:assertj-core:3.25.3")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    targetCompatibility = JavaVersion.toVersion(targetJRE)
    sourceCompatibility = targetCompatibility
}

tasks.named<Test>("test") {
    useJUnitPlatform() // fix "automatic loading deprecated",  "No tests found"
    testLogging.showStandardStreams = true // show StdOut and StdErr on the console
}
