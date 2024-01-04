import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.21"
    application
}

group = "me.levan"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.3.9") // For integration with Reactor
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx2:1.3.9") // For integration with RxJava
//
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.9") // For testing coroutines
//
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.3.9") // For Android Play Services integration

}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClassName = "MainKt"
}