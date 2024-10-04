plugins {
  id("buildlogic.java-application-conventions")
}

println("Building $version")
val functionalTest = "functionalTest"

sourceSets {
    create(functionalTest) {
    }
}

dependencies {
    "functionalTestImplementation"(project(":lib"))
//    sourceSets.named(functionalTest) {
//        implementation(project(":lib"))
//    }
}

tasks.register<JavaExec>("hello") {
    group = "dev-support"
    classpath = java.sourceSets[functionalTest].runtimeClasspath
    mainClass.set("com.exactpro.dump.Hello")
}
