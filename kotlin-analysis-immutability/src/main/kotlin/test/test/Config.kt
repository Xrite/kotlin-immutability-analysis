package test.test

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

val TaskConfiguration.Companion.flags: List<String>
    get() = TaskConfiguration::class.memberProperties.map { it.name }

val TaskConfiguration.values: List<Any?>
    get() = TaskConfiguration::class.memberProperties.map { it.get(this) }

val defaultConfig = Config(listOf(
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
))
