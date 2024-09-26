// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.3.2" apply false
    id("com.android.library") version "8.3.2" apply false
    id("com.google.devtools.ksp") version "1.9.23-1.0.19" apply false
    id("org.jetbrains.kotlin.android") version DependencyGradle.KOTLIN_VERSION apply false
    id("com.google.dagger.hilt.android") version DependencyGradle.HILT_VERSION apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}