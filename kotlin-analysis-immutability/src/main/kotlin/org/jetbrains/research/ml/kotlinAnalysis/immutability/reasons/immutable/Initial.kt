package org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.immutable

import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.ImmutableReason

object Initial : ImmutableReason() {
    override val csvData = object : CSVData {
        override val reason = "Initial"
    }
}
