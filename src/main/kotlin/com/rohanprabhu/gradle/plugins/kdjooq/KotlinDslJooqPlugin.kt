package com.rohanprabhu.gradle.plugins.kdjooq

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.logging.Logging
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.SourceSet

open class KotlinDslJooqPluginExtension(
    private val project: Project,
    private val jooqGeneratorRuntime: Configuration
) {
    companion object {
        const val DefaultJooqVersion = "3.12.3"
    }

    var jooqEdition : JooqEdition = JooqEdition.OpenSource
    var jooqVersion : String = DefaultJooqVersion
    var attachToCompileJava = true

    fun configuration(name: String, sourceSet: SourceSet, configure: JooqConfiguration.() -> Unit) {
        val configuration = JooqConfiguration(name, sourceSet).apply(configure)

        val task = project.tasks.create(configuration.taskName, JooqCodeGenerationTask::class.java).apply {
            description = "Generate jooq sources for config $name"
            group = "jooq-codegen"
            jooqConfiguration = configuration
            taskClasspath = jooqGeneratorRuntime
        }.apply {
            cleanGeneratedSources(project, this)
            configureSourceSet(project, this@KotlinDslJooqPluginExtension, configuration)
        }

        task.configureAdditionalInputs()
    }
}

class KotlinDslJooqPlugin : Plugin<Project> {
    private lateinit var project : Project
    private lateinit var jooqGeneratorRuntime : Configuration
    private lateinit var extension : KotlinDslJooqPluginExtension

    companion object {
        private val log = Logging.getLogger(KotlinDslJooqPlugin::class.java)

        const val ExtensionName = "jooqGenerator"
        const val BuildPhaseGradleConfigurationName = "jooqGeneratorRuntime"
    }

    override fun apply(project: Project) {
        this.project = project

        bootstrap()

        this.extension = project.extensions.create(
            ExtensionName, KotlinDslJooqPluginExtension::class.java,
            this.project,
            jooqGeneratorRuntime
        )

        manageJooqEditionAndVersion()
    }

    private fun bootstrap() {
        this.project.plugins.apply(JavaBasePlugin::class.java)
        addBuildPhaseConfiguration()
    }

    private fun addBuildPhaseConfiguration() {
        this.jooqGeneratorRuntime = this.project.configurations
            .create(BuildPhaseGradleConfigurationName)

        this.jooqGeneratorRuntime.description =
            "The classpath used to run the jooq generator. Your JDBC classes, generator extensions etc.," +
                "are to be added in this configuration to keep them separate from your build"

        this.jooqGeneratorRuntime.let {
            this.project.dependencies.add(it.name, "org.jooq:jooq-codegen")
        }
    }

    private fun manageJooqEditionAndVersion() {
        val groupIds = JooqEdition.values().map { it.editionArtifactGroup }.toSet()

        project.configurations.all {
            resolutionStrategy.eachDependency {
                if (groupIds.contains(requested.group) && requested.name.startsWith("jooq")) {
                    this.useTarget(
                        "${extension.jooqEdition.editionArtifactGroup}:${requested.name}:${extension.jooqVersion}"
                    )
                }
            }
        }
    }
}

private fun cleanGeneratedSources(project: Project, task: Task) {
    val cleanJooqSourcesTaskName = "clean" + task.name.capitalize()
    project.tasks.getByName(BasePlugin.CLEAN_TASK_NAME).dependsOn(cleanJooqSourcesTaskName)
    task.mustRunAfter(cleanJooqSourcesTaskName)
}

private fun configureSourceSet(project: Project, extension: KotlinDslJooqPluginExtension, jooqConfiguration: JooqConfiguration) {
    val sourceSet = jooqConfiguration.sourceSet

    sourceSet.java.srcDir(jooqConfiguration.configuration.generator.target.directory)
    if (extension.attachToCompileJava) {
        project.tasks.getByName(sourceSet.compileJavaTaskName).dependsOn(jooqConfiguration.taskName)
        project.tasks.findByName("compileKotlin")?.dependsOn(jooqConfiguration.taskName)
    }
}
