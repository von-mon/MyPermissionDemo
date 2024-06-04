pluginManagement {
    repositories {
        maven {
            url = uri("https://maven.aliyun.com/nexus/content/repositories/google")

        }
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://repo1.maven.org/maven2/") }
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
    }
}

rootProject.name = "MyPermissionDemo"
include(":app")
include(":lib_permission")
 