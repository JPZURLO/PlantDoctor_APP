pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // ▼▼▼ A LINHA DEVE ESTAR AQUI DENTRO ▼▼▼
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "Plantdoctor"
include(":app")
