import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import io.papermc.paperweight.core.tasks.patching.ApplyBasePatches
import io.papermc.paperweight.core.tasks.patching.ApplyFeaturePatches
import io.papermc.paperweight.tasks.RebuildBaseGitPatches
import io.papermc.paperweight.core.tasks.patching.RebuildFilePatches
import io.papermc.paperweight.tasks.RebuildGitPatches
import io.papermc.paperweight.tasks.CreatePublisherJar

plugins {
    java
    id("io.canvasmc.weaver.patcher") version "2.1.5-SNAPSHOT" // always keep in check with canvas' actual used release
}

val paperMavenPublicUrl = "https://repo.papermc.io/repository/maven-public/"

paperweight {
    upstreams.register("canvas") {
        repo = github("CraftCanvasMC", "Canvas")
        ref = providers.gradleProperty("canvasCommit")

        patchFile {
            path = "canvas-server/build.gradle.kts"
            outputFile = file("baguette-server/build.gradle.kts")
            patchFile = file("baguette-server/build.gradle.kts.patch")
        }
        patchFile {
            path = "canvas-api/build.gradle.kts"
            outputFile = file("baguette-api/build.gradle.kts")
            patchFile = file("baguette-api/build.gradle.kts.patch")
        }
        patchRepo("paperApi") {
            upstreamPath = "paper-api"
            patchesDir = file("baguette-api/paper-patches")
            outputDir = file("paper-api")
        }
        patchRepo("foliaApi") {
            upstreamPath = "folia-api"
            patchesDir = file("baguette-api/folia-patches")
            outputDir = file("folia-api")
        }
        patchDir("canvasApi") {
            upstreamPath = "canvas-api"
            excludes = listOf("build.gradle.kts", "build.gradle.kts.patch", "paper-patches", "folia-patches")
            patchesDir = file("baguette-api/canvas-patches")
            outputDir = file("canvas-api")
	}
    }
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    repositories {
        mavenCentral()
        maven(paperMavenPublicUrl)
    }

    tasks.withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
    tasks.withType<JavaCompile>().configureEach {
        options.encoding = Charsets.UTF_8.name()
        options.release = 21
        options.isFork = true
        options.compilerArgs.addAll(listOf("-Xlint:-deprecation", "-Xlint:-removal"))
    }
    tasks.withType<Javadoc>().configureEach {
        options.encoding = Charsets.UTF_8.name()
    }
    tasks.withType<ProcessResources>().configureEach {
        filteringCharset = Charsets.UTF_8.name()
    }
    tasks.withType<Test>().configureEach {
        testLogging {
            showStackTraces = true
            exceptionFormat = TestExceptionFormat.FULL
            events(TestLogEvent.STANDARD_OUT)
        }
    }
}
allprojects {
    // This block is for the "filter patches" setting
    // The filter patches setting controls whether empty patches should be deleted automatically or not
    // This settings default to true but it can sometimes break git's 3way
    tasks.withType<RebuildBaseGitPatches>().configureEach {
        filterPatches = true
    }
    tasks.withType<RebuildGitPatches>().configureEach {
        filterPatches = true
    }
    // Note: In default paperweight there's no such thing as filtering per-file patches as there's no need for it
    // but due to our changes it is possible to encounter a rare edge case (that also exists in pw but is unreachable) where there are per-file patches generated for files that you didn't make changes to
    // It only happens when there are empty files created in base patches, as the version of our diff library produces patches even when the base source and modified source dont differ but both contain an empty file
    tasks.withType<RebuildFilePatches>().configureEach {
        filterPatches = true
    }
    // This block on the other hand showcases how to enable an opt-in property which changes the way base and feature patches apply;
    // By default when there are conflicts the patch fails to apply *completely* and doesn't continue the apply when there is even one conflicting hunk detected in a 100
    // The `emitRejects` property allows to change it to make it instead *always* continue the apply even when most hunks dont apply and leaves it in a partially applied state while emitting .rej files next to and named the same as the file in which the hunk failed
    // It can be useful if you have a lot of involving patches that break on upstream updates frequently, so this way everything that can apply, gets applied and the rest is emitted as .rej files you can apply manually while the patch application is paused and later just continue the git am session
    // there are also more verbose details provided in the log file, such as the exact code snippets; see the console output on where to find it
    tasks.withType<ApplyBasePatches>().configureEach {
        emitRejects = false
    }
    tasks.withType<ApplyFeaturePatches>().configureEach {
        emitRejects = false
    }
}

// Weaver also provides an useful `build(Mojmap/Reobf)PublisherJar` task which generates a paperclip jar with the build number or whatever input you give it
// An example configuration is shown here

// custom input for publisherJar
val buildNumber = providers.environmentVariable("BUILD_NUM").orElse("no-build")
val jarName = buildNumber.map { build -> "libs/output-$build-test.jar" }

subprojects {
    tasks.withType<CreatePublisherJar>().configureEach {
        outputZip.set(layout.buildDirectory.file(jarName))
    }
}
// The default output of the task is as follows `[project name lowercased]-build.[the build number or local when there's none].jar`
// Following that, we can deduct that the name for our Baguette fork would be as follows `baguette-build.1.jar` or `baguette-build.local.jar` when in a dev environment
// The build number by default is taken from the environment variable `BUILD_NUMBER`
// If you wish to keep it just delete this block