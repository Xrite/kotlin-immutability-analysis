package test.test

data class Configuration(
    val treatCollectionsAsMutable: Boolean = false,
    val treatLazyAsImmutable: Boolean = false,
    val analyzeSealedSubclasses: Boolean = false,
    val assumeNastyInheritors: Boolean = false,
    val assumeGoodGetters: Boolean = false,
    val includeTests: Boolean = false,
    val outputFileName: String = "results.csv"
)
