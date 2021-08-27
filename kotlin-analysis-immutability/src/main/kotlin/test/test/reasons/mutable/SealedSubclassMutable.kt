package test.test.reasons.mutable

import com.beust.klaxon.json
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import test.test.reasons.MutableReason

class SealedSubclassMutable(val type: Type, val classifierDescriptor: ClassifierDescriptor) : MutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() = when(type) {
                Type.MUTABLE_BY_ASSUMPTION -> "sealed subclass mutable (assumption)"
                Type.UNKNOWN -> "sealed subclass unknown"
                Type.MUTABLE -> "sealed subclass mutable"
            }
        override val info: String
            get() = json {
                obj(
                    "descriptor" to classifierDescriptor.toString()
                )
            }.toJsonString(true)
    }
    enum class Type {
        MUTABLE_BY_ASSUMPTION,
        UNKNOWN,
        MUTABLE,
    }
}
