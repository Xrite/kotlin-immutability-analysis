package org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.mutable

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.MutableReason
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.basicInfo

data class ParentTypeMutable(val type: Type, val parentDescriptor: ClassifierDescriptor) : MutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() = when (type) {
                Type.MUTABLE_BY_ASSUMPTION -> "Parent type mutable (assumption)"
                Type.UNKNOWN -> "Parent type unknown"
                Type.MUTABLE -> "Parent type mutable"
            }
        override val info: Map<String, Any?>
            get() =
                mapOf(
                    "fqName" to parentDescriptor.fqNameSafe.asString(),
                    *parentDescriptor.basicInfo
                )
    }

    enum class Type {
        MUTABLE_BY_ASSUMPTION,
        UNKNOWN,
        MUTABLE,
    }
}
