package com.rohanprabhu.gradle.plugins.kdjooq

import org.jooq.meta.jaxb.*
import org.jooq.meta.jaxb.Target
import org.jooq.util.xml.jaxb.*

class ForcedTypesListOps(
        list : List<ForcedType>,
        private val internalList: MutableList<ForcedType> = list.toMutableList()
) {
    fun forcedType(configure: ForcedType.() -> Unit) : Unit = ForcedType().apply(configure).let {
        internalList.add(it)
    }

    internal fun getList() =
        internalList.toList()
}

fun jooqCodegenConfiguration(
    configure: Configuration.() -> Unit
) : Configuration =
    Configuration().apply(configure)

fun Configuration.jdbc(configure: Jdbc.() -> Unit) : Jdbc = Jdbc().apply(configure).also { this.jdbc = it }

fun Configuration.generator(configure: Generator.() -> Unit) : Generator =
        Generator().apply(configure).also { this.generator = it }

fun Generator.target(configure: Target.() -> Unit) : Target = Target().apply(configure).also { this.target = it }

fun Generator.database(configure: Database.() -> Unit) : Database =
        Database().apply(configure).also { this.database = it}

fun Generator.strategy(configure: Strategy.() -> Unit) : Strategy = Strategy().apply(configure).also {
    this.strategy = it
}

fun Generator.generate(configure: Generate.() -> Unit) : Generate = Generate().apply(configure).also {
    this.generate = it
}

fun MutableList<ForcedType>.forcedType(configure: ForcedType.() -> Unit) : ForcedType =
        ForcedType().apply(configure).also {
            this.add(it)
        }

fun Configuration.enumType(configure: EnumType.() -> Unit) : EnumType = EnumType().apply(configure)

fun Configuration.dbProperty(configure: Property.() -> Unit) : Property = Property().apply(configure)

fun Jdbc.dbProperty(key: String, value: String) = Property().apply { this.key = key; this.value = value }

fun Database.schema(configure: Schema.() -> Unit) : Schema = Schema().apply(configure).also {
    this.schema = it
}

fun Configuration.catalog(configure: Catalog.() -> Unit) : Catalog = Catalog().apply(configure)

fun Database.forcedTypes(vararg forcedType: ForcedType) = forcedType.toMutableList().also {
    this.forcedTypes = it
}

fun Configuration.forcedTypes(configure: ForcedTypesListOps.() -> Unit) : List<ForcedType> =
    ForcedTypesListOps(emptyList()).apply(configure).getList()

operator fun Jdbc?.invoke(configure: Jdbc.() -> Unit) : Jdbc = (this ?: Jdbc()).apply(configure)
operator fun Generator?.invoke(configure: Generator.() -> Unit) : Generator = (this ?: Generator()).apply(configure)
operator fun Target?.invoke(configure: Target.() -> Unit) : Target = (this ?: Target()).apply(configure)
operator fun Database?.invoke(configure: Database.() -> Unit) : Database = (this ?: Database()).apply(configure)
operator fun Generate?.invoke(configure: Generate.() -> Unit) : Generate = (this ?: Generate()).apply(configure)
operator fun EnumType?.invoke(configure: EnumType.() -> Unit) : EnumType = (this ?: EnumType()).apply(configure)
operator fun Property?.invoke(configure: Property.() -> Unit) : Property = (this ?: Property()).apply(configure)
operator fun Schema?.invoke(configure: Schema.() -> Unit) : Schema = (this ?: Schema()).apply(configure)
operator fun Catalog?.invoke(configure: Catalog.() -> Unit) : Catalog = (this ?: Catalog()).apply(configure)
operator fun List<ForcedType>?.invoke(configure: ForcedTypesListOps.() -> Unit) : List<ForcedType> =
    ForcedTypesListOps((this ?: emptyList())).apply(configure).getList()
operator fun JooqCodeGenerationTask.invoke(configure: JooqCodeGenerationTask.() -> Unit) : JooqCodeGenerationTask =
    this.apply(configure)
