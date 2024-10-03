import org.gradle.kotlin.dsl.apply
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.kotlin.jvm") apply false
}

repositories {
    mavenCentral()
}

dependencies {
    val log4jVer = "[2.0,3.0["

    constraints {
        implementation("org.apache.logging.log4j:log4j-api:$log4jVer")
        runtimeOnly("org.apache.logging.log4j:log4j-core:$log4jVer")
        runtimeOnly("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVer")
    }


    testImplementation("org.assertj:assertj-core:3.25.3")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

val targetJRE = "11"

java {
    targetCompatibility = JavaVersion.toVersion(targetJRE)
    sourceCompatibility = targetCompatibility
}

kotlin.compilerOptions.jvmTarget.set(JvmTarget.fromTarget(java.targetCompatibility.majorVersion))

tasks.named<Test>("test") {
    useJUnitPlatform() // fix "automatic loading deprecated",  "No tests found"
    testLogging.showStandardStreams = true // show StdOut and StdErr on the console
}
