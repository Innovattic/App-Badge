import com.android.build.gradle.TestedExtension

buildscript {
    repositories {
        jcenter()
        google()
        mavenCentral()
        // Local maven for testing our plugin when published locally
        mavenLocal()
    }
    dependencies {
        classpath(BuildScriptPlugins.kotlin)
        classpath(BuildScriptPlugins.android)
        //classpath(BuildScriptPlugins.appBadge)
    }
}

allprojects {
    repositories {
        jcenter()
        google()
        mavenCentral()
    }
}

subprojects {
    afterEvaluate {
        extensions
            .findByType(TestedExtension::class.java)
            ?.apply {
                compileSdkVersion(30)

                defaultConfig {
                    minSdk = 21
                    targetSdk = 30
                    versionCode = 1
                    versionName = Versions.projectVersion
                }

                buildTypes {
                    maybeCreate("debug")
                    maybeCreate("release")
                }
                flavorDimensions("version")
                productFlavors {
                    maybeCreate("stage")
                    maybeCreate("prod")
                }
            }
    }
}

tasks.register("clean", Delete::class.java) {
    delete(rootProject.buildDir)
}
