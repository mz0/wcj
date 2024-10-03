plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

gradlePlugin {
    plugins {
        register("semver-plugin") {
            id = "semver"
            implementationClass = "com.exactpro.build.SemVerPlugin"
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.0")
    implementation("org.eclipse.jgit:org.eclipse.jgit:[6.0,7.0[")
}
