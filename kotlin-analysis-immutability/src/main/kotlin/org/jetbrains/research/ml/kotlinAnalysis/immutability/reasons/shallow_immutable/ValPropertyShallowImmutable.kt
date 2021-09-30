package org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.shallow_immutable

import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.idea.util.string.collapseSpaces
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.ShallowImmutableReason
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.basicInfo

data class ValPropertyShallowImmutable(val type: Type, val propertyDescriptor: PropertyDescriptor) :
    ShallowImmutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() {
                return when (type) {
                    Type.MUTABLE_BY_ASSUMPTION -> "val refers to mutable type (assumption)"
                    Type.SHALLOW_IMMUTABLE_BY_ASSUMPTION -> "val refers to shallow immutable type (assumption)"
                    Type.UNKNOWN -> "val refers to unknown type"
                    Type.MUTABLE -> "val refers to mutable type"
                    Type.SHALLOW_IMMUTABLE -> "val refers to shallow immutable type"
                }.collapseSpaces()
            }
        override val info: Map<String, Any?>
            get() = mapOf(*propertyDescriptor.basicInfo)
    }

    enum class Type {
        MUTABLE_BY_ASSUMPTION,
        SHALLOW_IMMUTABLE_BY_ASSUMPTION,
        UNKNOWN,
        MUTABLE,
        SHALLOW_IMMUTABLE
    }
}
