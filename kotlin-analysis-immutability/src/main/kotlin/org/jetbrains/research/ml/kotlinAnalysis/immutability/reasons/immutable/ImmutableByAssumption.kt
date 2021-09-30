package org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.immutable

import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.ImmutableReason

object ImmutableByAssumption : ImmutableReason() {
    override fun isByAssumption() = true
    override val csvData = object : CSVData {
        override val reason = "Assumption"
    }
}
