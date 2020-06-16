# kotlin-dsl-jooq-plugin

A plugin that closely mirrors the de-facto (but non-official) [gradle plugin for jOOQ](https://github.com/etiennestuder/gradle-jooq-plugin)  by *etiennestuder*. While the gradle plugin can be used while using kotlin-dsl it can get very difficult to use it because it employs the dynamic method mechanism of groovy, which the kotlin runtime cannot use to figure out the types and most of the types have to be annotated by the developer. This plugins also adds a couple of extra features that some use cases might require.

**Author** Rohan Prabhu (rohan@rohanprabhu.com)

**License** Apache 2.0 (https://www.apache.org/licenses/LICENSE-2.0)

**Latest Version** 0.4.6 | *Legacy Version* 0.3.1

## Contributors

A huge thank you for the kind people submitting PRs and continuously improving this plugin (in no particular order).
If you see yours or anybodys name missing, please submit a PR marking a change in this section.

1. Nigel Gott [@nigel-gott](https://github.com/nigel-gott)
2. Aldo Borrerro [@aldoborrero](https://github.com/aldoborrero)
3. Ivan [@devshift](https://github.com/devshift)
4. Anuraag Agrawal [@anuraaga](https://github.com/anuraaga)
5. Davin Kevin [@davinkevin](https://github.com/davinkevin)

## What it provides

1. Configure jooq code generation against a target database.
2. Task dependency management, specifically its dependencies on `compileJava` and `compileKotlin`. As opposed to the gradle plugin, this plugin also configures the dependencies for the `compileKotlin` task.
3. Lock version of jooq throughout the project in all configurations.
4. Specify additional source directories which could be treated as an input for the code generation task (this is useful when you are using database migrations).

Similar to the gradle plugin, you can specify multiple jooq configurations and they will be mapped to different gradle tasks which will individually generate the code.

## Usage

To apply the plugin, use the gradle plugin syntax:

    plugins {
        id("com.rohanprabhu.kotlin-dsl-jooq") version "0.4.6"
    }

If you want to use older versions of jOOQ (i.e. 3.10 and older) use plugin version "0.3.1". Do note that 0.3.1
does not support Java 9 and requires a slightly different configuration syntax.

Once the plugin is applied, the minimum configuration required to generate sources are:

    import com.rohanprabhu.gradle.plugins.kdjooq.*

    jooqGenerator {
        configuration("primary", project.java.sourcesets.getByName("main")) {
            configuration = jooqCodegenConfiguration {
                jdbc {
                    username = "rohan"
                    password = "password"
                    driver   = "org.postgresql.Driver"
                    url      = "jdbc:postgresql://localhost:5432/example_database"
                }

                generator {
                    target {
                        packageName = "com.example.jooq"
                        directory   = "${project.buildDir}/generated/jooq/primary"
                    }
                    
                    database {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                    }
                }
            }
        }
    }
    
> If you are using the legacy version of this plugin, then you will need to assign these properties
> inside the config, i.e.
>
>```
> jooqGenerator {
>    configuration("primary", project.java.sourceSets.getByName("main")) {
>        configuration = jooqCodegenConfiguration {
>            jdbc = jdbc { ...
>```
>
> The last line in the new config does not require the assignment part `jdbc = ...`
>

The code generator is run in a classpath of its own, which is specified using `jooqGeneratorRuntime`. So add your JDBC dependencies (like JDBC drivers) in the `jooqGeneratorRuntime` configuration in the `dependencies` block:

    dependencies {
        jooqGeneratorRuntime("org.postgresql:postgresql:42.1")
    }

This will generate a task called `jooq-codegen-primary` which will generate the jooq code into the directory as specified under `target`. The `configuration` object is the jaxb `Configuration` [provided by `jooq`](http://grepcode.com/file/repo1.maven.org/maven2/org.jooq/jooq-meta/3.5.2/org/jooq/util/jaxb/Configuration.java).

If you have multiple configurations you need to generate the code for, use:

    jooqGenerator {
        configuration("primary", project.java.sourcesets.getByName("main")) { ... }
        configuration("analytics", project.java.sourcesets.getByName("main")) { ... }
    }

This will create two tasks `jooq-codegen-primary` and `jooq-codegen-analytics`. Do note that both of them will be a dependency of tasks `compileJava` and `compileKotlin` so you would rarely need to call these tasks directly.

Do note that if you are using multiple source sets for your project, you might want to select the one you intend and pass it as the second argument of the `configuration(..)` function. The generated sources are added to the source set that you specify, so that they are included as a part of your compile phase.

### Configuration style

There are different ways to write the configuration, the one which was shown above:

    jooqGenerator {
        configuration("primary", project.java.sourceSets.getByName("main")) {
            configuration = jooqCodegenConfiguration {
                jdbc {
                    username = 'rohan'
                    ...
                }

                generator {
                    ...
                }
            }
        }
    }

Since the `configuration` is simply the `Configuration` from `org.jooq.util.jaxb`, you can construct the object any way you want:

    jooqGenerator {
        configuration("primary", project.java.sourceSets.getByName("main")) {
            configuration = Configuration().apply {
                jdbc = Jdbc().withDriver("org.postgresql.Driver")
                ...
            }
        }
    }

The first example uses the DSL convenience utilities that are provided as part of this plugin and they essentially
create the jaxb package components for you and assign it to the configuration. From 0.4.2 onwards, if you want to
re-use certain objects, most common properties have a mirror function with `Config` suffixed to it that can
generate such an object for you. For example, to create a common jdbc config object, you can use
the `jdbcConfig` function:

    val commonJdbc = jdbcConfig {
         username = "rohan"
         password = "password"
    }

    configuration("primary", project.java.sourceSets.main()) {
        configuration = jooqCodegenConfiguration {
            jdbc = commonJdbc

            generator {
                ...
            }
        }
    }

    configuration("analytics", project.java.sourceSets.main()) {
        configuration = jooqCodegenConfiguration {
            jdbc = commonJdbc

            generator {
                ...
            }
        }
    }

> If you are using the legacy version, the function name is called `jdbc` itself.

These methods are available for almost all types that are used by the `org.jooq.util.jaxb.Configuration` object.

### Locking the jooq version

To specify a jooq version and edition to use, specify in the top-level `jooqGenerator` configuration block:

    jooqGenerator {
        jooqEdition = JooqEdition.OpenSource
        jooqVersion = "3.10.1"
    }

Once this is specified, any jooq dependency across configurations does not need the version to be specified:

    dependencies {
        compile("org.jooq:jooq")
    }

This will automatically pick-up the version you have specified in the configuration. Do note that there is currently no way to disable this automatic version locking. Also, the name `jooqGenerator` is a bit misleading to its alternative action of locking a version number throughout the code base. I intend to rename it in the next version.

## Using forced types

As has been mentioned before, since they are simply objects from the jooq package, you could simply set the forced types as:

    database {
        forcedTypes = listOf(ForcedType().apply { .. }, ForcedType().apply { .. })
    }

but keeping in lines with the kotlin-dsl and the overall gradle convention (also mirroring sort of close to what the gradle plugin offers), you could:

    database {
        isIncludeIndexes = true
        // other database parameters here

        forcedTypes {
            forcedType {
                userType = "com.fasterxml.jackson.databind.JsonNode"
                expression = ".*"
                binding = "com.example.PostgreJSONGsonBinding"
                types = "JSONB?"
            }

            forcedType {
                name = "varchar"
                expression = ".*"
                types = "INET"
            }
        }
    }

**NOTE** Since `CustomType` directive is now deprecated in jooq, no convenience functions are provided for the same, but you can still use it if you wish using:

    database {
        customTypes = listOf(... construct your list here ...)
    }

## Other generation parameters

To fine tune the code generation parameters:

    generator {
        database {
            ...
        }

        generate {
            relations = true
            deprecated = false
            records = true
            immutablePojos = false
            ...
        }
    }

## Task management

The tasks that are mapped are automatically set as dependencies for the `compileKotlin` and `compileJava` tasks so you don't have to do anything else manually. The task is considered `UP-TO-DATE` unless:

1. The `Configuration` object has changed.
2. The output directory of the generator is removed/modified (for example by a clean task or a manual delete).
3. The classpath as specified by `jooqGeneratorRuntime` is modified (one example would be if you add/remove dependencies to it).

In addition to this, if you are using a migration tool like **flyway**, you'd want the generator to run anytime your migration directory has changed. To do so, you can:

    configuration("primary", project.java.sourceSets.main()) {
        databaseSources {
            + "${project.projectDir}/src/main/resources/com/example/migrations"
            + "${project.projectDir}/src/main/resources/schema.sql"
        }

        configuration = jooqCodegenConfiguration {
            ...
        }
        ..
    }

Any object can be specified after the `+` symbol, and the path is resovled by gradle depending on the type, as [documented here](https://docs.gradle.org/current/javadoc/org/gradle/api/Project.html#file-java.lang.Object-). You can also directly specify the list if you wish (if it is being constructed elsewhere or through a procedure):

    databaseSources = listOf( ... ) // Any list of type List<Any>

What you'd also like to do is make your migration task a dependency of the code generation task:

    val `jooq-codegen-primary` by project.tasks
    `jooq-codegen-primary`.dependsOn("flywayMigrate")

Do remember to use the correct name of the task depending on the configuration name you have chosen.

Optionally, if you want to run the task manually, you can disable this default behavior by setting the variable `attachToCompileJava` to false:

    jooqGenerator {
        attachToCompileJava = false
    }

Make sure this is the first line in your `jooqGenerator` block as it only applies to configurations defined after it.

## Configuring the generator JVM

When the generator runs, it uses a java execution spec that is provided by the gradle infrastructure. If you wish to modify the way it runs, and/or add a handler to the post-execution result, you can:

    val `jooq-codegen-priamry` : JooqCodeGenerationTask by project.tasks

    `jooq-codegen-primary` {
        javaExecAction = Action {
            jvmArgs = listOf("-Xmx2G")
        }

        execResultHandler = Action {
            println(">> Exited with $exitValue")
        }
    }

Do not forget to use the correct type annotation here (`JooqCodeGenerationTask`), otherwise it'll just resolve to `gradle.api.tasks.Task` and you will get an error, something along the lines of `javaExecAction` and `execResultHandler` not being resolved.

### Contact

Please file an issue for feature requests or bugs. PRs for improvements are more than welcome.
