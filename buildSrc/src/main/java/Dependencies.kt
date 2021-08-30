object Versions {
    const val kotlin = "1.5.30"
    const val gradlePlugin = "7.0.1"
    const val projectVersion = "1.0.5"
}

object BuildScriptPlugins {
    const val android = "com.android.tools.build:gradle:${Versions.gradlePlugin}"
    const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
}

object Plugins {
    const val appBadgeId = "ru.cleverpumpkin.badge"
}
