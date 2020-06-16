import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        maven("https://plugins.gradle.org/m2/")
    }

    dependencies {
        classpath("com.gradle.publish:plugin-publish-plugin:0.9.9")
    }
}

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
    id("com.gradle.plugin-publish") version "0.12.0"
}

group = "com.rohanprabhu"
version = "0.4.6"

gradlePlugin {
    (plugins) {
        register("kotlinDslJooqPlugin") {
            id = "com.rohanprabhu.kotlin-dsl-jooq"
            implementationClass = "com.rohanprabhu.gradle.plugins.kdjooq.KotlinDslJooqPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/rohanprabhu/kotlin-dsl-gradle-jooq-plugin"
    vcsUrl = "https://github.com/rohanprabhu/kotlin-dsl-gradle-jooq-plugin"
    description = "jOOQ plugin aimed for use with kotlin-dsl gradle projects"
    tags = listOf("jooq", "kotlin-dsl")

    (plugins) {
        ("kotlinDslJooqPlugin") {
            displayName = "jOOQ plugin for Kotlin DSL"
        }
    }
}

val jooqVersion: String by project
val compileKotlin: KotlinCompile by project

compileKotlin.kotlinOptions.jvmTarget = "1.8"

tasks.withType<JavaCompile> {
    targetCompatibility = "1.8"
    sourceCompatibility = "1.8"
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    gradleApi()
    compile("org.jooq:jooq:$jooqVersion")
    compile("org.jooq:jooq-meta:$jooqVersion")
    compile("org.jooq:jooq-codegen:$jooqVersion")
    compile("javax.activation:activation:1.1.1")
    compile("javax.xml.bind:jaxb-api:2.3.0")
    compile("com.sun.xml.bind:jaxb-core:2.3.0.1")

    runtime("com.sun.xml.bind:jaxb-impl:2.3.0.1")
}

publishing {
    repositories {
        maven(url = "build/repository")
    }
}
