Gradle Plugin for adding a badge with version to app icons

![alt text](img/ic_launcher_round.png)

# Compatibility
Gradle 7.0.2

Android Gradle Plugin 7.0.1.
# How to add
Add this to your project's **build.gradle**
```
buildscript {
    repositories {
        maven { url 'https://jitpack.io' }
    }
    dependencies {
        implementation 'com.github.Innovattic:App-Badge:TAG'
    }
}
```
Then apply plugin in your app's **build.gradle**
```
apply plugin: "ru.cleverpumpkin.badge"
```

# Configuration
```
android {
    buildTypes {
        debug {}
        release {}
    }
    productFlavors {
        stage {}
        production {}
    }
}

badge {
    buildTypes {
        debug {
            enabled = true
            text = "debug"
        }
        // Do not add badge in release build type.
    }
    // or
    productFlavors {
        stage {
            enabled = true
            text = "stage"
        }

        production {
            enabled = true
            text = "production"
        }
    }
    // or
    // Variants has the highest priority. If variants config specified,
    // others will be ignored.
    variants {
        stageDebug {
            enabled = true
            text = "stageDebug"
        }
    }
}
```
### Custom icons
You can specify manually which icons to process:
```
badge {
    iconNames = ["@mipmap/ic_launcher_cusom"]
}
```

### Note
If you're using plugin in a library module and use icons from this
module in you app module, you need to specify icon names in library
module.

## Styling
You can specify text size, label color, text color.
Gravity customization isn't available. Bottom used by default.
```
badge {
    buildTypes {
        debug {
            enabled = true
            text = "debug"
            fontSize = 12 // Default value: 10
            
            // Note that colors in format "#XXX" not supported,
            // you have to specify colors as "#XXXXXX".
            textColor = "#FFFFFF"
            labelColor = "#000000"
        }
    }
}
```

# Sample
You can find the sample in a separate repo:
https://github.com/Innovattic/App-Badge-Sample

# Development

To test the plugin, you first need to locally publish the plugin.
Run: `./gradlew publishToMavenLocal`
This will publish the plugin to the local `/.m2` folder on your machine.
Add the `mavenLocal` repository to a client project to test this local version,
which will read from this `/.m2` folder.

Every time you change the plugin, you need to re-run `./gradlew publishToMavenLocal` in order to update the
version of the plugin published locally on your machine.

## Developed by
Sergey Chuprin - <gregamer@gmail.com>
## Maintained by
CleverPumpkin – https://cleverpumpkin.ru
## With contributions from
Innovattic - https://www.innovattic.com/

Nathan Bruning

Luke Needham
