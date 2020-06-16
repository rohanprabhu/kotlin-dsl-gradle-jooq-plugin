package com.rohanprabhu.gradle.plugins.kdjooq

import com.rohanprabhu.gradle.plugins.kdjooq.util.Objects
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.*
import org.gradle.process.ExecResult
import org.gradle.process.JavaExecSpec
import org.jooq.codegen.GenerationTool
import org.jooq.meta.jaxb.Configuration
import java.io.File
import javax.xml.XMLConstants
import javax.xml.bind.JAXBContext
import javax.xml.validation.SchemaFactory

open class JooqCodeGenerationTask : DefaultTask() {
    @Internal
    lateinit var jooqConfiguration : JooqConfiguration

    @Classpath
    @InputFiles
    lateinit var taskClasspath : FileCollection

    @Internal
    var javaExecAction: Action<in JavaExecSpec>? = null

    @Internal
    var execResultHandler : Action<in ExecResult>? = null

    @get:Input
    lateinit var databaseSourceLocations : List<Any>

    @TaskAction
    fun generateSources() {
        val configFile = File(project.buildDir, "tmp/jooq/config-${jooqConfiguration.configName}.xml")
        writeConfigFile(configFile)
        val execResult = executeJooq(configFile)

        execResultHandler?.execute(execResult)
    }

    fun configureAdditionalInputs() {
        databaseSourceLocations = jooqConfiguration.databaseSources
        jooqConfiguration.databaseSources.forEach { inputs.dir(it) }
    }

    @Input
    fun getConfigHash() : Int = Objects.deepHashCode(jooqConfiguration.configuration)

    @OutputDirectory
    fun getOutputDirectory() : File =
        project.file(jooqConfiguration.configuration.generator.target.directory)

    private fun writeConfigFile(file: File) {
        val xsdCodegenVersion = Class.forName("org.jooq.Constants")
                .getDeclaredField("XSD_CODEGEN")
                .get(null)

        val schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
            .newSchema(GenerationTool::class.java.getResource("/xsd/${xsdCodegenVersion}"))

        val marshaller = JAXBContext.newInstance(Configuration::class.java).let {
            it.createMarshaller().apply { setSchema(schema) }
        }

        file.parentFile.mkdirs()
        marshaller.marshal(jooqConfiguration.configuration, file)
    }

    private fun executeJooq(file: File) : ExecResult =
        project.javaexec {
            println(taskClasspath.files.map { it.name })
            main = GenerationTool::class.qualifiedName
            classpath = taskClasspath
            args = listOf(file.absolutePath)

            javaExecAction?.execute(this)
        }
}
