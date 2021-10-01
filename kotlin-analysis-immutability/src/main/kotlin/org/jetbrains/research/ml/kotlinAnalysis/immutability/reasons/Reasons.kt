package org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons

/** Reasons should be equals if reasons are the same (not the same object) */
sealed class Reason {
    open fun isByAssumption(): Boolean = false
    open val csvData = object : CSVData {
        override val reason = ""
    }

    interface CSVData {
        val reason: String
        val info: Map<String, Any?>
            get() = mapOf()
    }
}

abstract class ImmutableReason : Reason()

abstract class ShallowImmutableReason : Reason()

abstract class ConditionallyDeeplyImmutableReason : Reason()

abstract class MutableReason : Reason()
