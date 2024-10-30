pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        //차트 라이브러리
        maven { setUrl("https://jitpack.io") }
    }
}

rootProject.name = "1215DDAY"
include(":app")
