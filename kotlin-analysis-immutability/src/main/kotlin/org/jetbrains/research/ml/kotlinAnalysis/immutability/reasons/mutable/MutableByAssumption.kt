package org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.mutable

import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.MutableReason

object MutableByAssumption : MutableReason() {
    override fun isByAssumption(): Boolean = true
    override val csvData = object : CSVData {
        override val reason = "Assumption"
    }
}
