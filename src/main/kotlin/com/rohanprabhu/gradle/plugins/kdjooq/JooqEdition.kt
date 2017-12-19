package com.rohanprabhu.gradle.plugins.kdjooq

enum class JooqEdition(
    val editionArtifactGroup: String
) {
    OpenSource("org.jooq"),
    Professional("org.jooq.pro"),
    ProfessionalJava6("org.jooq.pro-java-6"),
    Trial("org.jooq.trial")
}