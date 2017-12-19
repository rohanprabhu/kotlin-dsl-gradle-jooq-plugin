package com.rohanprabhu.gradle.plugins.kdjooq

import org.jooq.util.jaxb.Generator
import org.jooq.util.jaxb.Jdbc
import org.jooq.util.jaxb.Target

fun jooqCodegenConfiguration(
    configure: org.jooq.util.jaxb.Configuration.() -> Unit
) : org.jooq.util.jaxb.Configuration =
    org.jooq.util.jaxb.Configuration().apply { this.configure() }

fun jdbc(configure: Jdbc.() -> Unit) : Jdbc = Jdbc().apply { this.configure() }

fun generator(configure: Generator.() -> Unit) : Generator = Generator().apply { this.configure() }

fun target(configure: Target.() -> Unit) : Target = Target().apply { this.configure() }
