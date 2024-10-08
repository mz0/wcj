import com.exactrpo.buildext.useDefaultVersions

plugins {
  id("buildlogic.java-application-conventions")
}

println("Building version: $version")

val functionalTest = "functionalTest"
val functionalTestImplementation = "functionalTestImplementation"
sourceSets {
    create(functionalTest) {
    }
}

configurations.useDefaultVersions()

val libProject = project(":lib")
dependencies {
    // type-safe 'sourceSets.named(functionalTest) { implementation(libProject) }' is OK, except confuses IDEA 2024.2
    functionalTestImplementation(libProject)
}

tasks.register<JavaExec>("hello") {
    group = "dev-support"
    classpath = java.sourceSets[functionalTest].runtimeClasspath
    mainClass.set("com.exactpro.dump.Hello")
}
