import org.gradle.kotlin.dsl.`kotlin-dsl`

plugins {
    `kotlin-dsl`
}

repositories {
    // JCenter still necessary for org.jetbrains.kotlin:kotlin-compiler-embeddable:1.4.31
    jcenter()
    google()
}
