plugins {
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
}

gradlePlugin {
  plugins {
    register("semver-plugin") {
      id = "semver"
      implementationClass = "net.x320.build.SemVerGit"
    }
  }
}

dependencies {
    implementation("org.eclipse.jgit:org.eclipse.jgit:[6.0,7.0[")
}
