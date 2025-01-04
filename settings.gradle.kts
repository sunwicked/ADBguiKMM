rootProject.name = "KmmDesktopDemo"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        mavenCentral()
    }
}

// Add these lines to include the modules
include(":shared")
include(":desktop") 