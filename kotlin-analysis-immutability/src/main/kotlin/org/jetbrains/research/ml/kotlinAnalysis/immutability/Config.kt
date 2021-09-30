package org.jetbrains.research.ml.kotlinAnalysis.immutability

import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.PropertySource
import com.sksamuel.hoplite.decoder.BooleanDecoder
import com.sksamuel.hoplite.decoder.DataClassDecoder
import com.sksamuel.hoplite.decoder.ListDecoder
import com.sksamuel.hoplite.decoder.StringDecoder
import com.sksamuel.hoplite.yaml.YamlParser
import java.io.File
import java.nio.file.Path
import kotlin.reflect.full.memberProperties

data class Config(val tasks: List<TaskConfiguration>, val joinOutputFile: String? = null)

data class TaskConfiguration(
    val treatCollectionsAsMutable: Boolean = false,
    val treatLazyAsImmutable: Boolean = false,
    val analyzeSealedSubclasses: Boolean = false,
    val assumeNastyInheritors: Boolean = false,
    val assumeGoodGetters: Boolean = false,
    val includeTests: Boolean = false,
    val analyzeDelegates: Boolean = false,
    val outputFileName: String
) {
    companion object
}

fun loadConfig(inputDir: Path): Config = ConfigLoader.Builder()
    .addSource(PropertySource.file(File(inputDir.toFile(), "config.yaml")))
    .addDecoder(ListDecoder())
    .addDecoder(DataClassDecoder())
    .addDecoder(BooleanDecoder())
    .addDecoder(StringDecoder())
    .addFileExtensionMapping("yaml", YamlParser())
    .build()
    .loadConfigOrThrow<Config>()


val TaskConfiguration.Companion.flags: List<String>
    get() = TaskConfiguration::class.memberProperties.map { it.name }

val TaskConfiguration.values: List<Any?>
    get() = TaskConfiguration::class.memberProperties.map { it.get(this) }

val defaultConfig = Config(
    listOf(
        TaskConfiguration(
            treatCollectionsAsMutable = false,
            treatLazyAsImmutable = true,
            analyzeSealedSubclasses = false,
            assumeNastyInheritors = false,
            assumeGoodGetters = false,
            includeTests = false,
            outputFileName = "results_default_without_tests.csv"
        ),
        TaskConfiguration(
            treatCollectionsAsMutable = false,
            treatLazyAsImmutable = true,
            analyzeSealedSubclasses = false,
            assumeNastyInheritors = false,
            assumeGoodGetters = false,
            includeTests = true,
            outputFileName = "results_default_with_tests.csv"
        ),
        TaskConfiguration(
            treatCollectionsAsMutable = true,
            treatLazyAsImmutable = true,
            analyzeSealedSubclasses = true,
            assumeNastyInheritors = true,
            assumeGoodGetters = false,
            includeTests = false,
            outputFileName = "results_pessimistic_without_tests.csv"
        ),
        TaskConfiguration(
            treatCollectionsAsMutable = true,
            treatLazyAsImmutable = true,
            analyzeSealedSubclasses = true,
            assumeNastyInheritors = true,
            assumeGoodGetters = false,
            includeTests = true,
            outputFileName = "results_pessimistic_with_tests.csv"
        ),
        TaskConfiguration(
            treatCollectionsAsMutable = false,
            treatLazyAsImmutable = true,
            analyzeSealedSubclasses = false,
            assumeNastyInheritors = false,
            assumeGoodGetters = true,
            includeTests = false,
            outputFileName = "results_optimistic_without_tests.csv"
        ),
        TaskConfiguration(
            treatCollectionsAsMutable = false,
            treatLazyAsImmutable = true,
            analyzeSealedSubclasses = false,
            assumeNastyInheritors = false,
            assumeGoodGetters = true,
            includeTests = true,
            outputFileName = "results_optimistic_with_tests.csv"
        ),
    )
)
