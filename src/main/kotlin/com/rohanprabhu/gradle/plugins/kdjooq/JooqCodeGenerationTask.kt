package com.rohanprabhu.gradle.plugins.kdjooq

import nu.studer.gradle.util.Objects
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.*
import org.gradle.process.JavaExecSpec
import org.jooq.Constants
import org.jooq.util.GenerationTool
import org.jooq.util.jaxb.Configuration
import java.io.File
import javax.xml.XMLConstants
import javax.xml.bind.JAXBContext
import javax.xml.validation.SchemaFactory

open class JooqCodeGenerationTask : DefaultTask() {
    lateinit var jooqConfiguration : JooqConfiguration

    @Classpath
    @InputFiles
    lateinit var taskClasspath : FileCollection

    @TaskAction
    fun generateSources() {
        val configFile = File(project.buildDir, "tmp/jooq/config-${jooqConfiguration.configName}.xml")
        writeConfigFile(configFile)
        executeJooq(configFile)
    }

    @Input
    fun getConfigHash() : Int = Objects.deepHashCode(jooqConfiguration)

    @OutputDirectory
    fun getOutputDirectory() : File =
        project.file(jooqConfiguration.configuration.generator.target.directory)

    private fun writeConfigFile(file: File) {
        val schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
            .newSchema(GenerationTool::class.java.getResource("/xsd/${Constants.XSD_CODEGEN}"))

        val marshaller = JAXBContext.newInstance(Configuration::class.java).let {
            it.createMarshaller().apply { setSchema(schema) }
        }

        file.parentFile.mkdirs()
        marshaller.marshal(jooqConfiguration.configuration, file)
    }

    private fun executeJooq(file: File) {
        project.javaexec(object : Action<JavaExecSpec> {
            override fun execute(t: JavaExecSpec) {
                t.main = "org.jooq.util.GenerationTool"
                t.classpath = taskClasspath
                t.args = listOf(file.absolutePath)
            }
        })
    }
}