plugins {
    id("com.gradle.plugin-publish") version "0.10.0"
    `kotlin-dsl`
    `maven-publish`
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
    options.release.set(8)
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

group = Plugins.appBadgeId

// Add info for publication to plugin portal.
pluginBundle {
    vcsUrl = "https://github.com/CleverPumpkin/App-Badge"
    website = "https://github.com/CleverPumpkin/App-Badge"
    description = "This is an Android gradle plugin that allows you to overlay " +
            "text on top of an android application\'s icon"
    tags = listOf("android", "icon", "generator", "badge", "label", "version")
}

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
