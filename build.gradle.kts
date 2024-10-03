plugins {
  java
}

apply(plugin = "semver")
println("Building $version")
val functionalTest = "functionalTest"

sourceSets {
    create(functionalTest) {
    }
}
repositories {
    mavenCentral()
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


subprojects {
    apply(plugin = "java")

    java {
        targetCompatibility = JavaVersion.VERSION_11
        sourceCompatibility = targetCompatibility
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        testImplementation("org.assertj:assertj-core:3.25.3")
        testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    tasks {
        test {
            useJUnitPlatform() // fix "automatic loading deprecated",  "No tests found"
            testLogging.showStandardStreams = true // show StdOut and StdErr on the console
        }
    }
}
