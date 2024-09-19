plugins {
    id("com.gradle.plugin-publish") version "1.3.0"
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
    implementation(BuildScriptPlugins.android)
}

// Make sure we're compatible with Java 1.8 and higher, even if we're building on a newer java version
tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = "11"
    targetCompatibility = "11"
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
    }
}

group = Plugins.appBadgeId

// Create plugin itself.
gradlePlugin {
    plugins {
        create("appBadgePlugin") {
            id = Plugins.appBadgeId
            displayName = "App Badge Generator"
            version = Versions.projectVersion
            implementationClass = "ru.cleverpumpkin.appbadge.AppBadgePlugin"
        }
    }
}

tasks.withType(Javadoc::class.java) {
    enabled = false
}
