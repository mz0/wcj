import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("buildlogic.java-common-conventions")
    id("org.jetbrains.kotlin.jvm")
}

kotlin.compilerOptions.jvmTarget.set(JvmTarget.fromTarget(java.targetCompatibility.majorVersion))
