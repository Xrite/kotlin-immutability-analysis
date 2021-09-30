package org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.mutable

import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.MutableReason
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.basicInfo

data class DelegatedProperty(val descriptor: VariableDescriptor) : MutableReason() {
    override val csvData = object : CSVData {
        override val reason = "Delegated property"
        override val info: Map<String, Any?>
            get() = mapOf(*descriptor.basicInfo)
    }
}
