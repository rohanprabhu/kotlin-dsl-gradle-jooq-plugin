package com.rohanprabhu.gradle.plugins.kdjooq

import org.gradle.api.tasks.SourceSet
import org.jooq.util.jaxb.Configuration
import java.io.File

class JooqConfiguration(
    val configName: String,
    val sourceSet: SourceSet
) {
    var databaseSources : List<File> = emptyList()

    val taskName = "jooq-codegen-$configName"
    lateinit var configuration: Configuration

    override fun toString(): String {
        return "JooqConfiguration(configuration=$configuration, sourceSet=$sourceSet)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JooqConfiguration

        if (configName != other.configName) return false
        if (configuration != other.configuration) return false
        if (sourceSet != other.sourceSet) return false

        return true
    }

    override fun hashCode(): Int {
        var result = configName.hashCode()
        result = 31 * result + configuration.hashCode()
        result = 31 * result + sourceSet.hashCode()
        return result
    }
}
