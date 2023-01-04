plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
    id("kotlinx-serialization")
    kotlin("native.cocoapods")
    kotlin("kapt")
}

group = "com.programmersbox"
version = "1.0-SNAPSHOT"

@OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
kotlin {
    android()
    jvm("desktop")
    js(IR) {
        browser()
    }
    ios()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    explicitApi()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "common"
            isStatic = true
        }
        extraSpecAttributes["resources"] = "['src/commonMain/resources/**', 'src/iosMain/resources/**']"
    }

    val ktorVersion = extra["ktor.version"] as String
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.ui)
                api(compose.foundation)
                api(compose.materialIconsExtended)
                api(compose.material)
                api(compose.material3)
                api("io.ktor:ktor-client-core:$ktorVersion")
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
                api("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                api("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                api("io.ktor:ktor-client-logging:$ktorVersion")
                api("org.ocpsoft.prettytime:prettytime:5.0.2.Final")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.appcompat:appcompat:1.5.1")
                api("androidx.core:core-ktx:1.9.0")
                api("io.ktor:ktor-client-android:$ktorVersion")

                api("androidx.core:core-ktx:1.9.0")
                api("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")
                api("androidx.lifecycle:lifecycle-runtime-compose:2.6.0-alpha03")
                api("io.coil-kt:coil-compose:2.2.2")
                api("io.coil-kt:coil-gif:2.2.2")
                api("com.google.accompanist:accompanist-flowlayout:0.28.0")
                api("com.google.accompanist:accompanist-navigation-material:0.28.0")
                api("androidx.navigation:navigation-compose:2.5.3")
                api("com.fragula2:fragula-compose:2.4.1")

                val markwonVersion = "4.6.2"
                api("io.noties.markwon:core:$markwonVersion")
                api("io.noties.markwon:ext-strikethrough:$markwonVersion")
                api("io.noties.markwon:ext-tables:$markwonVersion")
                api("io.noties.markwon:html:$markwonVersion")
                api("io.noties.markwon:linkify:$markwonVersion")
                api("io.noties.markwon:image-coil:$markwonVersion")
                api("io.noties.markwon:syntax-highlight:$markwonVersion") {
                    exclude("org.jetbrains", "annotations-java5")
                }
                configurations["kapt"].dependencies.add(
                    org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency(
                        "io.noties",
                        "prism4j-bundler",
                        "2.0.0"
                    )
                )

                api("pl.droidsonroids.gif:android-gif-drawable:1.2.25")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
            }
        }
        val desktopMain by getting {
            dependencies {
                api(compose.preview)
                api("io.ktor:ktor-client-cio:$ktorVersion")

                api(compose.desktop.components.splitPane)
                api("org.jetbrains.kotlinx:kotlinx-html-jvm:0.8.0")
                api("me.friwi:jcefmaven:108.4.13")
                api("com.alialbaali.kamel:kamel-image:0.4.1")
            }
        }
        val desktopTest by getting

        val jsMain by getting {
            dependencies {
                api(compose.web.core)
                api("io.ktor:ktor-client-js:$ktorVersion")
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by getting {
            dependencies {
                api("io.ktor:ktor-client-darwin:$ktorVersion")
            }
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
    }
}

android {
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 24
        targetSdk = 33
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}