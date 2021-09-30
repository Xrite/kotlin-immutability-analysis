package org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.conditionally_deeply_immutable

import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.ConditionallyDeeplyImmutableReason

object ConditionallyDeeplyImmutableByAssumption : ConditionallyDeeplyImmutableReason() {
    override fun isByAssumption(): Boolean = true
    override val csvData = object : CSVData {
        override val reason = "Assumption"
    }
}
