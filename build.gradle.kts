import com.programmersbox.ProjectInfoExtension
import com.programmersbox.ProjectInfoPlugin

group "com.programmersbox"
version "1.0-SNAPSHOT"

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven(url = "https://jitpack.io")
    }
}

plugins {
    kotlin("jvm") apply false
    kotlin("multiplatform") apply false
    kotlin("android") apply false
    id("com.android.application") apply false
    id("com.android.library") apply false
    id("org.jetbrains.compose") apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.20" apply false
}

subprojects {
    afterEvaluate {
        apply(plugin = "io.github.jakepurple13.ProjectInfo")
        if (plugins.findPlugin(ProjectInfoPlugin::class) != null) {
            setupProjectInfo()
        }
    }
}

fun Project.setupProjectInfo(): Unit = (this as ExtensionAware).extensions.configure(
    "projectInfo",
    Action<ProjectInfoExtension> {
        fileLineCountValidation {
            lineCountToFlag = 100
            red()
        }
        showTopCount = 3
        filter {
            excludeFileTypes("png", "ico", "icns")
        }
    }
)

buildscript {
    dependencies {
        classpath("io.github.jakepurple13.ProjectInfo:projectinfoplugin:1.1.1")
    }
}