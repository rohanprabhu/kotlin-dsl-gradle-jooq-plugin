package com.rohanprabhu.gradle.plugins.kdjooq

import org.jooq.util.jaxb.*
import org.jooq.util.jaxb.Target

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
    configure: org.jooq.util.jaxb.Configuration.() -> Unit
) : org.jooq.util.jaxb.Configuration =
    org.jooq.util.jaxb.Configuration().apply(configure)

fun jdbc(configure: Jdbc.() -> Unit) : Jdbc = Jdbc().apply(configure)

fun generator(configure: Generator.() -> Unit) : Generator = Generator().apply(configure)

fun target(configure: Target.() -> Unit) : Target = Target().apply(configure)

fun database(configure: Database.() -> Unit) : Database = Database().apply(configure)

fun strategy(configure: Strategy.() -> Unit) : Strategy = Strategy().apply(configure)

fun generate(configure: Generate.() -> Unit) : Generate = Generate().apply(configure)

fun forcedType(configure: ForcedType.() -> Unit) : ForcedType = ForcedType().apply(configure)

fun enumType(configure: EnumType.() -> Unit) : EnumType = EnumType().apply(configure)

fun dbProperty(configure: Property.() -> Unit) : Property = Property().apply(configure)

fun dbProperty(key: String, value: String) = Property().apply { this.key = key; this.value = value }

fun schema(configure: Schema.() -> Unit) : Schema = Schema().apply(configure)

fun catalog(configure: Catalog.() -> Unit) : Catalog = Catalog().apply(configure)

fun forcedTypes(vararg forcedType: ForcedType) = forcedType.toList()

fun forcedTypes(configure: ForcedTypesListOps.() -> Unit) : List<ForcedType> =
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
