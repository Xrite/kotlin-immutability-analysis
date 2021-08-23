package test.test

data class Config(val tasks: List<Task>)

data class Task(
    val treatCollectionsAsMutable: Boolean = false,
    val treatLazyAsImmutable: Boolean = false,
    val analyzeSealedSubclasses: Boolean = false,
    val assumeNastyInheritors: Boolean = false,
    val assumeGoodGetters: Boolean = false,
    val includeTests: Boolean = false,
    val outputFileName: String
)

val defaultConfig = Config(listOf(
    Task(
        treatCollectionsAsMutable = false,
        treatLazyAsImmutable = true,
        analyzeSealedSubclasses = false,
        assumeNastyInheritors = false,
        assumeGoodGetters = false,
        includeTests = false,
        outputFileName = "results_default_without_tests.csv"
    ),
    Task(
        treatCollectionsAsMutable = false,
        treatLazyAsImmutable = true,
        analyzeSealedSubclasses = false,
        assumeNastyInheritors = false,
        assumeGoodGetters = false,
        includeTests = true,
        outputFileName = "results_default_with_tests.csv"
    ),
    Task(
        treatCollectionsAsMutable = true,
        treatLazyAsImmutable = true,
        analyzeSealedSubclasses = true,
        assumeNastyInheritors = true,
        assumeGoodGetters = false,
        includeTests = false,
        outputFileName = "results_pessimistic_without_tests.csv"
    ),
    Task(
        treatCollectionsAsMutable = true,
        treatLazyAsImmutable = true,
        analyzeSealedSubclasses = true,
        assumeNastyInheritors = true,
        assumeGoodGetters = false,
        includeTests = true,
        outputFileName = "results_pessimistic_with_tests.csv"
    ),
    Task(
        treatCollectionsAsMutable = false,
        treatLazyAsImmutable = true,
        analyzeSealedSubclasses = false,
        assumeNastyInheritors = false,
        assumeGoodGetters = true,
        includeTests = false,
        outputFileName = "results_optimistic_without_tests.csv"
    ),
    Task(
        treatCollectionsAsMutable = false,
        treatLazyAsImmutable = true,
        analyzeSealedSubclasses = false,
        assumeNastyInheritors = false,
        assumeGoodGetters = true,
        includeTests = true,
        outputFileName = "results_optimistic_with_tests.csv"
    ),
))
