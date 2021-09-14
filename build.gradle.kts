import com.android.build.gradle.TestedExtension

buildscript {
    repositories {
        jcenter()
        google()
        mavenCentral()
    }
    dependencies {
        classpath(BuildScriptPlugins.kotlin)
        classpath(BuildScriptPlugins.android)
    }
}

allprojects {
    repositories {
        jcenter()
        google()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class.java) {
    delete(rootProject.buildDir)
}
