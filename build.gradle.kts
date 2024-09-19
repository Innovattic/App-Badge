buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class.java) {
    delete(rootProject.layout.buildDirectory)
}
