package org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.mutable

import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.MutableReason

object Error : MutableReason() {
    override val csvData = object : CSVData {
        override val reason = "Error"
        override val info: Map<String, Any?>
            get() = mapOf("info" to "Something went wrong")
    }
}
