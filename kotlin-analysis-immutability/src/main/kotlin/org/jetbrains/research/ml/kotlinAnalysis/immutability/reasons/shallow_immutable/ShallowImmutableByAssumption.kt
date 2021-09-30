package org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.shallow_immutable

import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.ShallowImmutableReason

object ShallowImmutableByAssumption : ShallowImmutableReason() {
    override fun isByAssumption(): Boolean = true
    override val csvData = object : CSVData {
        override val reason = "Assumption"
    }
}
