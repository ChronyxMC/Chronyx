import java.util.Locale

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            name = "canvasmc"
            url = uri("https://maven.canvasmc.io/releases")
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

if (!file(".git").exists()) {
    val errorText = """
        
        =====================[ ERROR ]=====================
         The Chronyx project directory is not a properly cloned Git repository.
         
         In order to build Chronyx from source you must clone
         the Chronyx repository using Git, not download a code
         zip from GitHub.
         
         Built Chronyx jars are available for download at
         https://chronyxmc.io/downloads
         
         See https://github.com/chronyxmc/chronyx/blob/HEAD/CONTRIBUTING.md
         for further information on building and modifying Chronyx.
        ===================================================
    """.trimIndent()
    error(errorText)
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "chronyx"
for (name in listOf("canvas-api", "canvas-server", "canvas-test-plugin")) {
    val projName = name.lowercase(Locale.ENGLISH)
    include(projName)
    findProject(":$projName")!!.projectDir = file(name)
}
