package org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.mutable

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.MutableReason
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.basicInfo

data class MayHaveMutableSubclass(val classifierDescriptor: ClassifierDescriptor) : MutableReason() {
    override val csvData: CSVData
        get() = object : CSVData {
            override val reason: String
                get() = "May have mutable subclass"
            override val info: Map<String, Any?>
                get() = mapOf(*classifierDescriptor.basicInfo)
        }
}
