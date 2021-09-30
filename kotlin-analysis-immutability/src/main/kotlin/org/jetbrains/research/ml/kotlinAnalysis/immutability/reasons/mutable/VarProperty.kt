package org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.mutable

import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.MutableReason
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.basicInfo

data class VarProperty(val variableDescriptor: PropertyDescriptor) : MutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() = "var"
        override val info: Map<String, Any?>
            get() = mapOf(*variableDescriptor.basicInfo)
    }
}
