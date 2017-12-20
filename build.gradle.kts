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
    id("com.gradle.plugin-publish") version "0.9.9"
}

group = "com.rohanprabhu"
version = "0.2"

gradlePlugin {
    (plugins) {
        "kotlinDslJooqPlugin" {
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
        "kotlinDslJooqPlugin" {
            id = "com.rohanprabhu.kotlin-dsl-jooq"
            displayName = "jOOQ plugin for Kotlin DSL"
        }
    }
}

val jooqVersion by project

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    gradleApi()
    compile("org.jooq:jooq:$jooqVersion")
    compile("org.jooq:jooq-meta:$jooqVersion")
    compile("org.jooq:jooq-codegen:$jooqVersion")
}

publishing {
    repositories {
        maven(url = "build/repository")
    }
}
