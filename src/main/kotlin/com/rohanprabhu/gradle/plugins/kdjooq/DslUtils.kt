package com.rohanprabhu.gradle.plugins.kdjooq

import org.jooq.meta.jaxb.*
import org.jooq.meta.jaxb.Target
import org.jooq.util.xml.jaxb.*

class ForcedTypesListOps(
    private val database: Database
) {
    fun forcedType(configure: ForcedType.() -> Unit) : Unit = ForcedType().apply(configure).let {
        database.forcedTypes.add(it)
    }
}

class EnumTypesListOps(
    private val database: Database
) {
    fun enumType(configure: EnumType.() -> Unit) : Unit = EnumType().apply(configure).let {
        database.enumTypes.add(it)
    }
}

class JdbcPropertiesOps(
    private val jdbc: Jdbc
) {
    fun property(configure: Property.() -> Unit) = Property().apply(configure)
}

fun jooqCodegenConfiguration(
    configure: Configuration.() -> Unit
) : Configuration =
    Configuration().apply(configure)

fun jdbcConfig(configure: Jdbc.() -> Unit) = Jdbc().apply(configure)
fun Configuration.jdbc(configure: Jdbc.() -> Unit) {
    jdbcConfig(configure).also { this.jdbc = it }
}

fun Jdbc.properties(configure: JdbcPropertiesOps.() -> Unit) = JdbcPropertiesOps(this).apply(configure)

fun generatorConfig(configure: Generator.() -> Unit) = Generator().apply(configure)
fun Configuration.generator(configure: Generator.() -> Unit) {
    generatorConfig(configure).also {
        it.database = it.database ?: Database()
        this.generator = it
    }
}

fun targetConfig(configure: Target.() -> Unit) = Target().apply(configure)
fun Generator.target(configure: Target.() -> Unit) {
    targetConfig(configure).also { this.target = it }
}

fun databaseConfig(configure: Database.() -> Unit) = Database().apply(configure)
fun Generator.database(configure: Database.() -> Unit) {
    databaseConfig(configure).also { this.database = it }
}

fun strategyConfig(configure: Strategy.() -> Unit) = Strategy().apply(configure)
fun Generator.strategy(configure: Strategy.() -> Unit) {
    strategyConfig(configure).also {
        this.strategy = it
    }
}

fun generateConfig(configure: Generate.() -> Unit) = Generate().apply(configure)
fun Generator.generate(configure: Generate.() -> Unit) {
    generateConfig(configure).also {
        this.generate = it
    }
}

fun forcedTypeConfig(configure: ForcedType.() -> Unit) = ForcedType().apply(configure)
fun Database.forcedTypes(configure: ForcedTypesListOps.() -> Unit) = ForcedTypesListOps(this).apply(configure)

fun enumTypeConfig(configure: EnumType.() -> Unit) = EnumType().apply(configure)
fun Database.enumTypes(configure: ForcedTypesListOps.() -> Unit) = ForcedTypesListOps(this).apply(configure)
