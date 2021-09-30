package org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.mutable

import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.MutableReason
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.basicInfo

data class VarParameter(val variableDescriptor: ValueParameterDescriptor) : MutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() = "var (parameter)"
        override val info: Map<String, Any?>
            get() = mapOf(*variableDescriptor.basicInfo)
    }
}
