package org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.shallow_immutable

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.ShallowImmutableReason
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.basicInfo

data class ParentTypeShallowImmutable(val classifierDescriptor: ClassifierDescriptor) :
    ShallowImmutableReason() {
    override val csvData = object : CSVData {
        override val reason = "Parent type shallow immutable"
        override val info: Map<String, Any?>
            get() = mapOf(*classifierDescriptor.basicInfo)
    }
}
