plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
}

group = "com.rohanprabhu"
version = "0.1"

gradlePlugin {
    (plugins) {
        "kotlinDslJooqPlugin" {
            id = "kotlin-dsl-jooq"
            implementationClass = "com.rohanprabhu.gradle.plugins.kdjooq.KotlinDslJooqPlugin"
        }
    }
}

val jooqVersion by project

repositories {
    mavenCentral()
}

dependencies {
    compile("org.jooq:jooq:$jooqVersion")
    compile("org.jooq:jooq-meta:$jooqVersion")
    compile("org.jooq:jooq-codegen:$jooqVersion")
}

publishing {
    repositories {
        maven(url = "build/repository")
    }
}
