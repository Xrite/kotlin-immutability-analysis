package org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.mutable

import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.MutableReason
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.basicInfo

data class PropertyDelegateMutable(val type: Type, val propertyDescriptor: PropertyDescriptor) : MutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() = when (type) {
                Type.MUTABLE_BY_ASSUMPTION -> "Property delegate mutable (assumption)"
                Type.UNKNOWN -> "Property delegate unknown"
                Type.MUTABLE -> "Property delegate mutable"
                Type.SHALLOW_IMMUTABLE -> "Property delegate shallow immutable"
                Type.SHALLOW_IMMUTABLE_BY_ASSUMPTION -> "Property delegate shallow immutable (assumption)"
            }
        override val info: Map<String, Any?>
            get() = mapOf(*propertyDescriptor.basicInfo)
    }

    enum class Type {
        MUTABLE_BY_ASSUMPTION,
        UNKNOWN,
        MUTABLE,
        SHALLOW_IMMUTABLE,
        SHALLOW_IMMUTABLE_BY_ASSUMPTION
    }
}