plugins {
  java
}

apply(plugin = "semver")
println("Building $version")

sourceSets {
    create("functionalTest") {
        java.srcDir("functionalTest/java")
        resources.srcDir("functionalTest/testResources")
    }
}

task<JavaExec>("hello") {
    group = "dev-support"
    classpath = java.sourceSets["functionalTest"].runtimeClasspath
    mainClass.set("com.exactpro.dump.Hello")
}


val log4jVer = "[2.0,3.0["

subprojects {
    apply(plugin = "java")

    java {
        targetCompatibility = JavaVersion.VERSION_11
        sourceCompatibility = JavaVersion.VERSION_11
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation("org.apache.logging.log4j:log4j-api:$log4jVer")

        runtimeOnly("org.apache.logging.log4j:log4j-core:$log4jVer")
        runtimeOnly("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVer")

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
