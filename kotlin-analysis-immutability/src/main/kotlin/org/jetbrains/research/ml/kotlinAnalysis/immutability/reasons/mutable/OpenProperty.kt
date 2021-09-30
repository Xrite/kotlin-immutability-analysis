package org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.mutable

import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.MutableReason
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.basicInfo

data class OpenProperty(val propertyDescriptor: PropertyDescriptor) : MutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() = "open property"
        override val info: Map<String, Any?>
            get() = mapOf(*propertyDescriptor.basicInfo)
    }
}
