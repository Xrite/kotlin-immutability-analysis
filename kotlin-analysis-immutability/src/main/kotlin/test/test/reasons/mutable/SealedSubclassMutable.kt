package test.test.reasons.mutable

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import test.test.reasons.MutableReason
import test.test.reasons.basicInfo

class SealedSubclassMutable(val type: Type, val classifierDescriptor: ClassifierDescriptor) : MutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() = when(type) {
                Type.MUTABLE_BY_ASSUMPTION -> "sealed subclass mutable (assumption)"
                Type.UNKNOWN -> "sealed subclass unknown"
                Type.MUTABLE -> "sealed subclass mutable"
            }
        override val info: Map<String, Any?>
            get() = mapOf(*classifierDescriptor.basicInfo)
    }
    enum class Type {
        MUTABLE_BY_ASSUMPTION,
        UNKNOWN,
        MUTABLE,
    }
}
